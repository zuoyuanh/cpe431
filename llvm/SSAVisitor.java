package llvm;

import ast.*;
import staticChecker.*;
import java.io.File;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.Map;
import exceptions.IdentifierNotFoundException;
import exceptions.DuplicatedIdentifierDeclarationException;

public class SSAVisitor implements LLVMVisitor<LLVMType, LLVMBlockType>
{
   private File output;
   private BufferedWriter bufferedWriter;

   private int blockCounter = 0;
   private int registerCounter = 0;

   private Table<Table<LLVMStructFieldEntry>> typesTable = new Table<Table<LLVMStructFieldEntry>>(null, "type");
   private Table<Integer> typesSizeTable = new Table<Integer>(null, "any");
   private Table<FunctionType> funcsTable = new Table<FunctionType>(null, "functions");

   private LLVMBlockType funcExitBlock = null;
   private String funcExitBlockId = null;
   private String funcRetValueTypeRep = null;
   private List<LLVMBlockType> blockList = null;

   private List<LLVMBlockType> globalBlockList;

   public SSAVisitor(File output)
   {
      this.output = output;
      try {
         if (output != null) {
            this.bufferedWriter = new BufferedWriter(new FileWriter(output));
         }
      } catch (Exception e) {
         System.out.println("cannot write to file");
      }
   }

   public LLVMType visit(Program program)
   {
      globalBlockList = new ArrayList<LLVMBlockType>();
      LLVMBlockType programBlock = new LLVMBlockType("PROG", true, LLVMBlockType.Label.PROGRAM);
      printStringToFile("target triple=\"i686\"\n");
      List<TypeDeclaration> types = program.getTypes();
      for (TypeDeclaration typeDecl : types){
         this.visit(typeDecl);
      }
      printStringToFile("\n");

      List<Declaration> decls = program.getDecls();
      for (Declaration decl : decls){
         LLVMType t = this.visit(decl);
         if (t instanceof LLVMDeclType) {
            LLVMDeclType declType = (LLVMDeclType)t;
            String varName = declType.getName();
            String typeRep = declType.getTypeRep();
            String declName = "@" + varName;
            /* try {
               declsTable.insert(declName, decl.getType());
            } catch (Exception e) {
            } */
            printStringToFile(declName + " = common global ");
            printStringToFile(typeRep + " ");
            if (typeRep.equals("i32")) {
               printStringToFile("0, align 4");
            } else if (typeRep.equals("i1")) {
               printStringToFile("0, align 1");
            } else {
               /* try {
                  String structName = typeRep.substring(0, typeRep.length()-1);
                  int size = typesSizeTable.get(structName);
                  printStringToFile("null, align " + Integer.toString(size * 8));
               } catch (Exception esc) {
               } */
               printStringToFile("null, align 8");
            }
            writeVariable(varName, programBlock, new LLVMRegisterType(typeRep, "%" + varName));
            printStringToFile("\n");
         }
      }
      printStringToFile("\n");

      List<Function> funcs = program.getFuncs();
      for (Function func : funcs){
         LLVMType funcType = this.visit(func);
         if (funcType instanceof LLVMBlockType) {
            programBlock.addSuccessor((LLVMBlockType)funcType);
         }
      }

      printStringToFile("declare i8* @malloc(i32)\n");
      printStringToFile("declare void @free(i8*)\n");
      printStringToFile("declare i32 @printf(i8*, ...)\n");
      printStringToFile("declare i32 @scanf(i8*, ...)\n");
      printStringToFile("@.println = private unnamed_addr constant [5 x i8] c\"%ld\\0A\\00\", align 1\n");
      printStringToFile("@.print = private unnamed_addr constant [5 x i8] c\"%ld \\00\", align 1\n");
      printStringToFile("@.read = private unnamed_addr constant [4 x i8] c\"%ld\\00\", align 1\n");
      printStringToFile("@.read_scratch = common global i32 0, align 8\n");
      try {
         bufferedWriter.close();
      } catch (Exception e) {
      }
      return new LLVMVoidType(); 
   }

   public LLVMType visit(TypeDeclaration typeDecl)
   {
      String name = typeDecl.getName();
      String typeName = "%struct." + name;
      String typeDeclString = "%struct." + name + " = type {";
      List<Declaration> fields = typeDecl.getFields();

      try {
         typesTable.insert(typeName, null);
         typesSizeTable.insert(typeName, fields.size());
         typesTable.overwrite(typeName, buildDeclarationsTable(typeDecl.getFields(), null));
      } catch (DuplicatedIdentifierDeclarationException e) {
         System.out.println(e.getErrorMessage());
      }

      for (Declaration decl : fields) {
         LLVMType t = this.visit(decl);
         if (t instanceof LLVMDeclType) {
            typeDeclString += ((LLVMDeclType)t).getTypeRep() + ", ";
         }
      }
      if (typeDeclString.length() > 2 && typeDeclString.charAt(typeDeclString.length()-2) == ',') {
         typeDeclString = typeDeclString.substring(0, typeDeclString.length()-2);
      }
      printStringToFile(typeDeclString + "}\n");
      return new LLVMVoidType(); 
   }

   public LLVMType visit(Declaration decl)
   {
      String name = decl.getName();
      String typeRep = getTypeLLVMRepresentation(decl.getType());
      return new LLVMDeclType(name, typeRep);
   }

   public LLVMType visit(Function func)
   {
      Statement body = func.getBody();
      Type returnType = func.getRetType();
      List<Declaration> params = func.getParams();
      List<Declaration> locals = func.getLocals();
      String returnTypeLLVMRep = getTypeLLVMRepresentation(returnType);

      blockList = new ArrayList<LLVMBlockType>();
      funcExitBlockId = "LU" + Integer.toString(blockCounter++);
      LLVMBlockType retBlock = new LLVMBlockType(funcExitBlockId, LLVMBlockType.Label.EXIT);
      funcExitBlock = retBlock;
      funcRetValueTypeRep = returnTypeLLVMRep;

      insertFunctionsTable(func, funcsTable);

      String paramsRep = "(";

      String startBlockId = "LU" + Integer.toString(blockCounter++);
      LLVMBlockType startBlock = new LLVMBlockType(startBlockId, true, LLVMBlockType.Label.ENTRY);

      // Declare params
      for (Declaration param : params) {
         LLVMType t = this.visit(param);
         if (t instanceof LLVMDeclType) {
            String originalName = ((LLVMDeclType)t).getName();
            String tTypeRep = ((LLVMDeclType)t).getTypeRep();
            writeVariable(originalName, startBlock, new LLVMRegisterType(tTypeRep, "%" + originalName));
         }
      }
      if (paramsRep.length() > 2 && paramsRep.charAt(paramsRep.length()-2) == ',') {
         paramsRep = paramsRep.substring(0, paramsRep.length()-2);
      }
      paramsRep += ")";
      printStringToFile("define " + returnTypeLLVMRep + " @" + func.getName() + paramsRep + "\n{\n");

      // Declare locals
      for (Declaration local : locals) {
         LLVMType t = this.visit(local);
         if (t instanceof LLVMDeclType) {
            String tTypeRep = ((LLVMDeclType)t).getTypeRep();
            String tNameRep = ((LLVMDeclType)t).getName();
            writeVariable(tNameRep, startBlock, new LLVMRegisterType(tTypeRep, "%" + tNameRep));
         }
      }

      blockList.add(startBlock);
      this.visit(body, startBlock);
      blockList.add(retBlock);

      if (returnTypeLLVMRep.equals("void")) {
         retBlock.add("ret void\n");
      } else {
         LLVMType retValType = readVariable("_retval_", retBlock);
         if (retValType instanceof LLVMRegisterType) {
            retBlock.add("ret " + returnTypeLLVMRep + " %" + ((LLVMRegisterType)retValType).getId() + "\n");
         } else if (retValType instanceof LLVMPrimitiveType) {
            retBlock.add("ret " + returnTypeLLVMRep + " %" + ((LLVMPrimitiveType)retValType).getValueRep() + "\n");
         }
      }

      sealBlock(funcExitBlock);  //seal the exit block

      for (LLVMBlockType block : blockList) {
         globalBlockList.add(block);
         printStringToFile(block.getBlockId() + ": \n");
         List<String> llvmCode = block.getLLVMCode();
         HashMap<String, LLVMPhiType> phiTable = block.getPhiTable();
         for (String id : phiTable.keySet()) {
            LLVMPhiType phi = phiTable.get(id);
            LLVMRegisterType phiRegister = phi.getRegister();
            String phiOpnds = "";
            for (LLVMPhiEntryType ty : phi.getPhiOperands()) {
               String blockId = ty.getBlock().getBlockId();
               LLVMType t = ty.getOperand();
               if (t instanceof LLVMRegisterType) {
                  phiOpnds += "[%" + ((LLVMRegisterType)t).getId() + ", %" + blockId + "], ";
               } else if (t instanceof LLVMPrimitiveType) {
                  phiOpnds += "[" + ((LLVMPrimitiveType)t).getValueRep() + ", %" + blockId + "], ";
               }
            }
            if (phiOpnds.length() > 2 && phiOpnds.charAt(phiOpnds.length()-2) == ',') {
               phiOpnds = phiOpnds.substring(0, phiOpnds.length()-2);
            }
            printStringToFile("\t%" + phiRegister.getId() + " = phi " + phiRegister.getTypeRep() + " " + phiOpnds + "\n");
         }
         for (String code : llvmCode) {
            printStringToFile("\t" + code);
         }
         if (!block.isClosed() && !block.getBlockId().equals(funcExitBlockId)) {
            printStringToFile("\tbr label %" + funcExitBlockId + "\n");
         }
      }

      printStringToFile("}\n\n");

      return startBlock;
   }

   public LLVMType visit(Type type)
   {
      return new LLVMVoidType();
   }

   // Statements

   public LLVMType visit(Statement s, LLVMBlockType block)
   {
      if (s instanceof BlockStatement) {
         return this.visit((BlockStatement)s, block);
      } else if (s instanceof ConditionalStatement) {
         return this.visit((ConditionalStatement)s, block);
      } else if (s instanceof PrintLnStatement){
         return this.visit((PrintLnStatement)s, block); 
      } else if (s instanceof PrintStatement){
         return this.visit((PrintStatement)s, block);
      } else if (s instanceof DeleteStatement){
         return this.visit((DeleteStatement)s, block);
      } else if (s instanceof ReturnEmptyStatement){
         return this.visit((ReturnEmptyStatement)s, block);
      } else if (s instanceof AssignmentStatement) {
         return this.visit((AssignmentStatement)s, block);
      } else if (s instanceof WhileStatement) {
         return this.visit((WhileStatement)s, block);
      } else if (s instanceof ReturnStatement) {
         return this.visit((ReturnStatement)s, block);
      } else if (s instanceof InvocationStatement) {
         return this.visit((InvocationStatement)s, block);
      }
      return new LLVMVoidType();
   }

   public LLVMType visit(AssignmentStatement assignmentStatement, LLVMBlockType block)
   {
      Lvalue target = assignmentStatement.getTarget();
      Expression source = assignmentStatement.getSource();
      LLVMType targetType = this.visit(target, block);
      LLVMType sourceType = this.visit(source, block);
      if (target instanceof LvalueId) {
         String id = ((LvalueId)target).getId();
         if (sourceType instanceof LLVMReadExpressionType) {
            String newRegId = "u" + Integer.toString(registerCounter++);
            block.add(((LLVMReadExpressionType)sourceType).getSSAReadInstructionString(newRegId));
            writeVariable(id, block, new LLVMRegisterType("i32", newRegId));
         } else {
            writeVariable(id, block, sourceType);
         }
         return new LLVMVoidType();
      }
      if (targetType instanceof LLVMRegisterType) {
         String targetTypeRep = ((LLVMRegisterType)targetType).getTypeRep();
         String targetId = ((LLVMRegisterType)targetType).getId();
         if (sourceType instanceof LLVMReadExpressionType) {
            block.add(((LLVMReadExpressionType)sourceType).getReadInstructionString(
               targetTypeRep, targetId));
            return new LLVMVoidType();
         }
         String sourceId = getOperand(sourceType, targetTypeRep, block);
         block.add("store " + targetTypeRep + " " + sourceId + ", " 
                 + targetTypeRep + "* " + getRegAndValRep(targetId) + "\n");
      }
      return new LLVMVoidType();
   }

   public LLVMType visit(BlockStatement blockStatement, LLVMBlockType block)
   {
      LLVMBlockType currentBlock = block;
      List<Statement> statements = blockStatement.getStatements();
      for (Statement stmt : statements){
         LLVMType stmtType = this.visit(stmt, currentBlock);
         if (stmtType instanceof LLVMBlockType) {
            currentBlock = (LLVMBlockType)stmtType;
         }
      }
      return currentBlock;
   }

   public LLVMType visit(ConditionalStatement conditionalStatement, LLVMBlockType block)
   {
      LLVMType guardType = this.visit(conditionalStatement.getGuard(), block);
      if (guardType instanceof LLVMRegisterType || guardType instanceof LLVMPrimitiveType) {
         Statement thenBlock = conditionalStatement.getThenBlock();
         Statement elseBlock = conditionalStatement.getElseBlock();

         String thenLLVMBlockId = "LU" + Integer.toString(blockCounter++);
         String elseLLVMBlockId = "LU" + Integer.toString(blockCounter++);
         String jointLLVMBlockId = "LU" + Integer.toString(blockCounter++);

         LLVMBlockType thenLLVMBlock = new LLVMBlockType(thenLLVMBlockId, true, LLVMBlockType.Label.THEN);
         LLVMBlockType elseLLVMBlock = new LLVMBlockType(elseLLVMBlockId, true, LLVMBlockType.Label.ELSE);
         LLVMBlockType jointLLVMBlock = new LLVMBlockType(jointLLVMBlockId, true, LLVMBlockType.Label.JOIN);

         block.addSuccessor(thenLLVMBlock);
         block.addSuccessor(elseLLVMBlock);
         thenLLVMBlock.addPredecessor(block);
         elseLLVMBlock.addPredecessor(block);
     

         blockList.add(thenLLVMBlock);
         blockList.add(elseLLVMBlock);
         blockList.add(jointLLVMBlock);

         if (guardType instanceof LLVMRegisterType) {
            String guardRegId = ((LLVMRegisterType)guardType).getId();
            String guardTypeRep = ((LLVMRegisterType)guardType).getTypeRep();
            if (!guardTypeRep.equals("i1")) {
               guardRegId = typeConverter(guardTypeRep, "i1", guardRegId, block);
            }

            block.add("br i1 %" + guardRegId + ", label %" + thenLLVMBlockId 
                    + ", label %" + elseLLVMBlockId + "\n");
         } else {
            if (((LLVMPrimitiveType)guardType).getValueRep().equals("0")) {
               block.add("br label %" + elseLLVMBlockId + ("\n"));
            } else {
               block.add("br label %" + thenLLVMBlockId + ("\n"));
            }
         }

         if (thenBlock != null) {
            LLVMType thenBlockType = this.visit(thenBlock, thenLLVMBlock);
            if (thenBlockType instanceof LLVMBlockType) {
               thenLLVMBlock = (LLVMBlockType)thenBlockType;
               thenLLVMBlockId = ((LLVMBlockType)thenBlockType).getBlockId();
            }
         }
         if (!thenLLVMBlock.isClosed()) {
            thenLLVMBlock.add("br label %" + jointLLVMBlockId + "\n");
            thenLLVMBlock.addSuccessor(jointLLVMBlock);
            jointLLVMBlock.addPredecessor(thenLLVMBlock);
         }

         if (elseBlock != null) {
            LLVMType elseBlockType = this.visit(elseBlock, elseLLVMBlock);
            if (elseBlockType instanceof LLVMBlockType) {
               elseLLVMBlock = (LLVMBlockType)elseBlockType;
               elseLLVMBlockId = ((LLVMBlockType)elseBlockType).getBlockId();
            }
         }
         if (!elseLLVMBlock.isClosed()) {
            elseLLVMBlock.add("br label %" + jointLLVMBlockId + "\n");
            elseLLVMBlock.addSuccessor(jointLLVMBlock); 
            jointLLVMBlock.addPredecessor(elseLLVMBlock);
         }
         
         return jointLLVMBlock;
      }
      return new LLVMVoidType();
   }

   public LLVMType visit(DeleteStatement deleteStatement, LLVMBlockType block)
   {
      Expression exp = deleteStatement.getExpression();
      LLVMType expType = this.visit(exp, block);
      String opnd = getOperand(expType, "i8*", block);
      block.add("call void @free(i8* " + opnd + ")\n");
      return new LLVMVoidType();
   }

   public LLVMType visit(InvocationStatement invocationStatement, LLVMBlockType block)
   {
      Expression e = invocationStatement.getExpression();
      this.visit(e, block);
      return new LLVMVoidType();
   }

   public LLVMType visit(PrintLnStatement printLnStatement, LLVMBlockType block)
   {
      Expression exp = printLnStatement.getExpression();
      LLVMType expType = this.visit(exp, block);
      if (expType instanceof LLVMRegisterType) {
         block.add("call i32 (i8*, ...)* @printf(i8* getelementptr inbounds ([5 x i8]* @.println, i32 0, i32 0), "
                 + ((LLVMRegisterType)expType).getTypeRep() + " %" + ((LLVMRegisterType)expType).getId() + ")\n");
      } else if (expType instanceof LLVMPrimitiveType) {
         block.add("call i32 (i8*, ...)* @printf(i8* getelementptr inbounds ([5 x i8]* @.println, i32 0, i32 0), "
                 + ((LLVMPrimitiveType)expType).getTypeRep() + " " + ((LLVMPrimitiveType)expType).getValueRep() + ")\n");
      }
      return new LLVMVoidType();
   }

   public LLVMType visit(PrintStatement printStatement, LLVMBlockType block)
   {
      Expression exp = printStatement.getExpression();
      LLVMType expType = this.visit(exp, block);
      if (expType instanceof LLVMRegisterType) {
         block.add("call i32 (i8*, ...)* @printf(i8* getelementptr inbounds ([5 x i8]* @.print, i32 0, i32 0), "
                 + ((LLVMRegisterType)expType).getTypeRep() + " %" + ((LLVMRegisterType)expType).getId() + ")\n");
      } else if (expType instanceof LLVMPrimitiveType) {
         block.add("call i32 (i8*, ...)* @printf(i8* getelementptr inbounds ([5 x i8]* @.print, i32 0, i32 0), "
                 + ((LLVMPrimitiveType)expType).getTypeRep() + " " + ((LLVMPrimitiveType)expType).getValueRep() + ")\n");
      }
      return new LLVMVoidType();
   }

   public LLVMType visit(ReturnEmptyStatement returnEmptyStatement, LLVMBlockType block)
   {
      block.add("br label %" + funcExitBlockId + "\n");
      return new LLVMVoidType();
   }

   public LLVMType visit(ReturnStatement returnStatement, LLVMBlockType block)
   {
      Expression exp = returnStatement.getExpression();
      LLVMType expType = this.visit(exp, block);
      if (expType instanceof LLVMRegisterType || expType instanceof LLVMPrimitiveType) {
         LLVMType opnd = getOperandType(expType, funcRetValueTypeRep, block);
         writeVariable("_retval_", block, opnd);
         block.add("br label %" + funcExitBlockId + "\n");
      }
      block.addSuccessor(funcExitBlock);
      funcExitBlock.addPredecessor(block);
      return new LLVMVoidType();
   }

   public LLVMType visit(WhileStatement whileStatement, LLVMBlockType block)
   {
      LLVMType guardType = this.visit(whileStatement.getGuard(), block);
      if (guardType instanceof LLVMRegisterType || guardType instanceof LLVMPrimitiveType) {
         Statement bodyBlock = whileStatement.getBody();

         String bodyLLVMBlockId = "LU" + Integer.toString(blockCounter++);
         String jointLLVMBlockId = "LU" + Integer.toString(blockCounter++);
         String originalBodyLLVMBlockId = bodyLLVMBlockId;

         LLVMBlockType bodyLLVMBlock = new LLVMBlockType(bodyLLVMBlockId, LLVMBlockType.Label.WHILE_LOOP);
         LLVMBlockType jointLLVMBlock = new LLVMBlockType(jointLLVMBlockId, true, LLVMBlockType.Label.WHILE_EXIT);
         LLVMBlockType originalBodyLLVMBlock = bodyLLVMBlock;

         block.addSuccessor(bodyLLVMBlock);
         bodyLLVMBlock.addPredecessor(block);
         block.addSuccessor(jointLLVMBlock);
         jointLLVMBlock.addPredecessor(block);

         blockList.add(bodyLLVMBlock);
         blockList.add(jointLLVMBlock);

         if (guardType instanceof LLVMRegisterType) {
            String guardRegId = ((LLVMRegisterType)guardType).getId();
            String guardTypeRep = ((LLVMRegisterType)guardType).getTypeRep();

            if (!guardTypeRep.equals("i1")) {
               guardRegId = typeConverter(guardTypeRep, "i1", guardRegId, block);
            }

            block.add("br i1 %" + guardRegId + ", label %" + bodyLLVMBlockId 
                    + ", label %" + jointLLVMBlockId + "\n");
         } else {
            if (((LLVMPrimitiveType)guardType).getValueRep().equals("0")) {
               block.add("br label %" + jointLLVMBlockId + ("\n"));
            } else {
               block.add("br label %" + bodyLLVMBlockId + ("\n"));
            }
         }

         if (bodyBlock != null) {
            LLVMType bodyBlockType = this.visit(bodyBlock, bodyLLVMBlock);
            if (bodyBlockType instanceof LLVMBlockType) {
               bodyLLVMBlock = (LLVMBlockType)bodyBlockType;
               bodyLLVMBlockId = ((LLVMBlockType)bodyLLVMBlock).getBlockId();
            }
         }

         guardType = this.visit(whileStatement.getGuard(), bodyLLVMBlock);
         
         if (guardType instanceof LLVMRegisterType) {
            String guardRegId = ((LLVMRegisterType)guardType).getId();
            String guardTypeRep = ((LLVMRegisterType)guardType).getTypeRep();
            bodyLLVMBlock.add("br i1 %" + guardRegId + ", label %" + originalBodyLLVMBlockId 
                             + ", label %" + jointLLVMBlockId + "\n");
         } else if (guardType instanceof LLVMPrimitiveType) {
            if (((LLVMPrimitiveType)guardType).getValueRep().equals("0")) {
               bodyLLVMBlock.add("br label %" + jointLLVMBlockId + ("\n"));
            } else {
               bodyLLVMBlock.add("br label %" + originalBodyLLVMBlockId + ("\n"));
            }
         }

         bodyLLVMBlock.addSuccessor(originalBodyLLVMBlock);
         originalBodyLLVMBlock.addPredecessor(bodyLLVMBlock);
         bodyLLVMBlock.addSuccessor(jointLLVMBlock);
         jointLLVMBlock.addPredecessor(bodyLLVMBlock);
         sealBlock(originalBodyLLVMBlock);   //seal the while block
         return jointLLVMBlock;
      }
      return new LLVMVoidType();
   }

   // Expressions

   public LLVMType visit(Expression e, LLVMBlockType block)
   {
      if (e instanceof TrueExpression) {
         return this.visit((TrueExpression)e);
      } else if (e instanceof FalseExpression) {
         return this.visit((FalseExpression)e);
      } else if (e instanceof IntegerExpression) {
         return this.visit((IntegerExpression)e);
      } else if (e instanceof NullExpression) {
         return this.visit((NullExpression)e);
      } else if (e instanceof ReadExpression) {
         return this.visit((ReadExpression)e, block);
      } else if (e instanceof IdentifierExpression) {
         return this.visit((IdentifierExpression)e, block);
      } else if (e instanceof DotExpression) {
         return this.visit((DotExpression)e, block);
      } else if (e instanceof UnaryExpression) {
         return this.visit((UnaryExpression)e, block);
      } else if (e instanceof NewExpression) {
         return this.visit((NewExpression)e, block);
      } else if (e instanceof BinaryExpression) {
         return this.visit((BinaryExpression)e, block);
      } else if (e instanceof InvocationExpression) {
         return this.visit((InvocationExpression)e, block);
      }
      return new LLVMVoidType();
   }

   private String binaryOperationOpcode(BinaryExpression.Operator operator)
   {
      switch (operator) {
      case TIMES:
         return "mul";
      case DIVIDE:
         return "sdiv";
      case PLUS:
         return "add";
      case MINUS:
         return "sub";
      case LT:
         return "icmp slt";
      case GT:
         return "icmp sgt";
      case LE:
         return "icmp sle";
      case GE:
         return "icmp sge";
      case EQ:
         return "icmp eq";
      case NE:
         return "icmp ne";
      case AND:
         return "and";
      case OR:
         return "or";
      default:
         return "unknown";
      }
   }

   private String binaryOperationType(BinaryExpression.Operator operator)
   {
      switch (operator) {
      case TIMES: case DIVIDE: 
      case PLUS: case MINUS:
      // case LT: case GT: case LE:
      // case GE: case EQ: case NE:
         return "i32";
      case AND: case OR:
         return "i1";
      default:
         return "any";
      }
   }

   private String typeConverter(String originalType, String expectedType, 
                                         String opnd, LLVMBlockType block)
   {
      if (originalType.equals("i32") && expectedType.equals("i1")) {
         String tmpRegId = "u" + Integer.toString(registerCounter++);
         block.add(getRegAndValRep(tmpRegId) + " = trunc " + originalType 
                  + " " + getRegAndValRep(opnd) + " to " + expectedType + "\n");
         return tmpRegId;
      } else if (originalType.equals("i1") && expectedType.equals("i32")) {
         String tmpRegId = "u" + Integer.toString(registerCounter++);
         block.add(getRegAndValRep(tmpRegId) + " = zext " + originalType 
                  + " " + getRegAndValRep(opnd) + " to " + expectedType + "\n");
         return tmpRegId;
      } else {
         String tmpRegId = "u" + Integer.toString(registerCounter++);
         block.add(getRegAndValRep(tmpRegId) + " = bitcast " + originalType 
                  + " " + getRegAndValRep(opnd) + " to " + expectedType + "\n");
         return tmpRegId;
      }
   }

   private String getOperand(LLVMType t, String expectedType, LLVMBlockType block)
   {
      if (t instanceof LLVMRegisterType) {
         String tTypeRep = ((LLVMRegisterType)t).getTypeRep();
         String regId = ((LLVMRegisterType)t).getId();
         if (!expectedType.equals("any") && !tTypeRep.equals(expectedType)) {
            return getRegAndValRep(typeConverter(tTypeRep, expectedType, "%"+regId, block));
         }
         return getRegAndValRep(regId);
      } else if (t instanceof LLVMPrimitiveType) {
         String tTypeRep = ((LLVMPrimitiveType)t).getTypeRep();
         String valueRep = ((LLVMPrimitiveType)t).getValueRep();
         if (!tTypeRep.equals(expectedType) && !tTypeRep.equals("null") && !expectedType.equals("any")) {
            return getRegAndValRep(typeConverter(tTypeRep, expectedType, valueRep, block));
         }
         return valueRep;
      }
      return "unknown";
   }

   private LLVMType getOperandType(LLVMType t, String expectedType, LLVMBlockType block)
   {
      if (t instanceof LLVMRegisterType) {
         String tTypeRep = ((LLVMRegisterType)t).getTypeRep();
         String regId = ((LLVMRegisterType)t).getId();
         if (!expectedType.equals("any") && !tTypeRep.equals(expectedType)) {
            return new LLVMRegisterType(expectedType, typeConverter(tTypeRep, expectedType, "%"+regId, block));
         }
         return new LLVMRegisterType(expectedType, regId);
      } else if (t instanceof LLVMPrimitiveType) {
         String tTypeRep = ((LLVMPrimitiveType)t).getTypeRep();
         String valueRep = ((LLVMPrimitiveType)t).getValueRep();
         if (!tTypeRep.equals(expectedType) && !tTypeRep.equals("null") && !expectedType.equals("any")) {
            return new LLVMPrimitiveType(tTypeRep, typeConverter(tTypeRep, expectedType, valueRep, block));
         }
         return new LLVMPrimitiveType(tTypeRep, valueRep);
      }
      return new LLVMVoidType();
   }

   private String binaryOperationResultType(BinaryExpression.Operator operator)
   {
      switch (operator) {
      case TIMES: case DIVIDE: 
      case PLUS: case MINUS:
         return "i32";
      case LT: case GT: case LE:
      case GE: case EQ: case NE:
      case AND: case OR:
         return "i1";
      default:
         return "unknown";
      }
   }

   public LLVMType visit(BinaryExpression binaryExpression, LLVMBlockType block)
   {
      LLVMType leftType = this.visit(binaryExpression.getLeft(), block);
      LLVMType rightType = this.visit(binaryExpression.getRight(), block);
      BinaryExpression.Operator operator = binaryExpression.getOperator();
      String opr = binaryOperationOpcode(operator);
      String oprty = binaryOperationType(operator);
      String opnd1 = getOperand(leftType, oprty, block);
      String opnd2 = getOperand(rightType, oprty, block);
      if (oprty.equals("any")) {
         if (leftType instanceof LLVMRegisterType) {
            oprty = ((LLVMRegisterType)leftType).getTypeRep();
         } else if (leftType instanceof LLVMPrimitiveType) {
            oprty = ((LLVMPrimitiveType)rightType).getTypeRep();
         }
      }
      String resultReg = "u" + Integer.toString(registerCounter++);
      block.add(getRegAndValRep(resultReg) + " = " + opr + " " + oprty + " " + opnd1 + ", " + opnd2 + "\n");
      return new LLVMRegisterType(binaryOperationResultType(operator), resultReg);
   }

   public LLVMType visit(DotExpression dotExpression, LLVMBlockType block)
   {
      Expression left = dotExpression.getLeft();
      String id = dotExpression.getId();
      LLVMType leftType = this.visit(left, block);
      if (leftType instanceof LLVMRegisterType) {
         String typeRep = ((LLVMRegisterType)leftType).getTypeRep();
         String regName = ((LLVMRegisterType)leftType).getId();
         String structTypeRep = typeRep;
         if (structTypeRep.length() > 1 && structTypeRep.charAt(structTypeRep.length()-1) == '*') {
            structTypeRep = structTypeRep.substring(0, structTypeRep.length()-1);
         }
         try {
            Table<LLVMStructFieldEntry> fieldsTable = typesTable.get(structTypeRep);
            LLVMStructFieldEntry field = fieldsTable.get(id);
            String fieldPositionRep = field.getPositionRep();
            String fieldTypeRep = field.getTypeRep();
            String tempReg = "u" + Integer.toString(registerCounter++);
            /* block.add("%" + tempReg + " = getelementptr " + typeRep + " %" + regName 
                    + ", i1 0, " + fieldTypeRep + " " + fieldPositionRep + "\n"); */
            String resultReg = "u" + Integer.toString(registerCounter++);
            block.add(getRegAndValRep(resultReg) + " = load " + fieldTypeRep + "* %" + tempReg + "\n");
            return new LLVMRegisterType(fieldTypeRep, resultReg);
         } catch (Exception e) {
            printStringToFile(id + ": IDENTIFIER NOT FOUND 2\n");
         }
      }
      return new LLVMVoidType();
   }

   public LLVMType visit(IdentifierExpression identifierExpression, LLVMBlockType block)
   {
      String id = identifierExpression.getId();
      String registerName = "u" + Integer.toString(registerCounter++);
      return readVariable(id, block);
   }

   public LLVMType visit(IntegerExpression integerExpression)
   {
      return new LLVMPrimitiveType("i32", integerExpression.getValue());
   }

   public LLVMType visit(TrueExpression trueExpression)
   {
      return new LLVMPrimitiveType("i32", "1");
   }
   
   public LLVMType visit(FalseExpression falseExpression)
   {
      return new LLVMPrimitiveType("i32", "0");
   }

   public LLVMType visit(NullExpression nullExpression)
   {
      return new LLVMPrimitiveType("null", "null");
   }

   public LLVMType visit(InvocationExpression e, LLVMBlockType block)
   {
      String name = e.getName();
      FunctionType f = null;
      try {
         f = funcsTable.get(name);
         List<Declaration> params = f.getParams();
         List<Expression> args = e.getArguments();
         String callArgsRep = "(";
         String returnTypeRep = getTypeLLVMRepresentation(f.getRetType());
         for (int i=0; i<params.size(); i++) {
            Declaration param = params.get(i);
            Expression arg = args.get(i);
            LLVMType argType = this.visit(arg, block);
            String paramTypeRep = getTypeLLVMRepresentation(param.getType());
            if (argType instanceof LLVMRegisterType) {
               String argTypeRep = ((LLVMRegisterType)argType).getTypeRep();
               String regId = ((LLVMRegisterType)argType).getId();
               if (!argTypeRep.equals(paramTypeRep)) {
                  String originalRegId = regId;
                  regId = "u" + Integer.toString(registerCounter++);
                  block.add("%" + regId + " = bitcast " + argTypeRep 
                          + " %" + originalRegId + " to " + paramTypeRep + "\n");
               }
               callArgsRep += paramTypeRep + " %" + regId + ", ";
            } else if (argType instanceof LLVMPrimitiveType) {
               String argTypeRep = ((LLVMPrimitiveType)argType).getTypeRep();
               String valueRep = ((LLVMPrimitiveType)argType).getValueRep();
               if (!argTypeRep.equals(paramTypeRep) && !argTypeRep.equals("null")) {
                  String regId = "u" + Integer.toString(registerCounter++);
                  block.add("%" + regId + " = bitcast " + argTypeRep 
                          + " " + valueRep + " to " + paramTypeRep + "\n");
               }
               callArgsRep += paramTypeRep + " " + valueRep + ", ";
            }
         }
         if (callArgsRep.length() > 2 && callArgsRep.charAt(callArgsRep.length()-2) == ',') {
            callArgsRep = callArgsRep.substring(0, callArgsRep.length()-2);
         }
         callArgsRep += ")";
         if (!returnTypeRep.equals("void")) {
            String returnRegId = "u" + Integer.toString(registerCounter++);
            block.add("%" + returnRegId + " = call " + returnTypeRep + " @" + name + " " + callArgsRep + "\n");
            return new LLVMRegisterType(returnTypeRep, returnRegId);
         }
         block.add("call " + returnTypeRep + " @" + name + callArgsRep + "\n");
      } catch (IdentifierNotFoundException exc) {
         printStringToFile("IDENTIFIER NOT FOUND 3\n");
      }
      return new LLVMVoidType();
   }

   public LLVMType visit(NewExpression newExpression, LLVMBlockType block)
   {
      String id = newExpression.getId();
      String structRep = "%struct." + id;
      String returnRegId = "u" + Integer.toString(registerCounter++);

      try {
         block.add("%" + returnRegId + " = call i8* @malloc(i32 " + (8 * typesSizeTable.get(structRep)) + ")\n");
      } catch (Exception exc) {
      }
      return new LLVMRegisterType(structRep + "*", typeConverter("i8*", structRep + "*", returnRegId, block));
   }

   public LLVMType visit(ReadExpression readExpression, LLVMBlockType block)
   {
      return new LLVMReadExpressionType();
   }

   public LLVMType visit(UnaryExpression unaryExpression, LLVMBlockType block)
   {
      UnaryExpression.Operator operator = unaryExpression.getOperator();
      Expression exp = unaryExpression.getOperand();
      Expression newExp = null;

      switch (operator)
      {
      case NOT:
         newExp = BinaryExpression.create(-1, "-",
            new IntegerExpression(-1, "1"), exp);
         return this.visit(newExp, block);
      case MINUS:
         newExp = BinaryExpression.create(-1, "-",
            new IntegerExpression(-1, "0"), exp);
         return this.visit(newExp, block);
      default:
         return new LLVMVoidType();
      }
   }

   // L values for Assignments

   public LLVMType visit(Lvalue lvalue, LLVMBlockType block)
   {
      if (lvalue instanceof LvalueId) {
         return this.visit((LvalueId)lvalue, block);
      } else if (lvalue instanceof LvalueDot) {
         return this.visit((LvalueDot)lvalue, block);
      }
      return new LLVMVoidType();
   }

   public LLVMType visit(LvalueDot lvalueDot, LLVMBlockType block)
   {
      Expression left = lvalueDot.getLeft();
      String id = lvalueDot.getId();
      LLVMType leftType = this.visit(left, block);
      if (leftType instanceof LLVMRegisterType) {
         String typeRep = ((LLVMRegisterType)leftType).getTypeRep();
         String regName = ((LLVMRegisterType)leftType).getId();
         String structTypeRep = typeRep;
         if (structTypeRep.length() > 1 && structTypeRep.charAt(structTypeRep.length()-1) == '*') {
            structTypeRep = structTypeRep.substring(0, structTypeRep.length()-1);
         }
         try {
            Table<LLVMStructFieldEntry> fieldsTable = typesTable.get(structTypeRep);
            LLVMStructFieldEntry field = fieldsTable.get(id);
            String fieldPositionRep = field.getPositionRep();
            String fieldTypeRep = field.getTypeRep();
            String tempReg = "u" + Integer.toString(registerCounter++);
            // block.add("%" + tempReg + " = getelementptr " + typeRep + " %" + regName 
            //         + ", i1 0, " + fieldTypeRep + " " + fieldPositionRep + "\n");
            block.add("%" + tempReg + " = getelementptr " + typeRep + " %" + regName 
                    + ", i1 0, i32 " + fieldPositionRep + "\n");
            return new LLVMRegisterType(fieldTypeRep, tempReg);
         } catch (Exception e) {
            printStringToFile(id + ": IDENTIFIER NOT FOUND 1\n");
         }
      }
      return new LLVMVoidType();
   }

   public LLVMType visit(LvalueId lvalueId, LLVMBlockType block)
   {
      return new LLVMVoidType();
   }

   public List<LLVMBlockType> getGlobalBlockList()
   {
      return globalBlockList;
   }

   // Helper functions

   private String getTypeLLVMRepresentation(Type t)
   {
      if ((t instanceof IntType) || (t instanceof BoolType)) {
         return "i32";
      } else if (t instanceof VoidType) {
         return "void";
      } else if (t instanceof StructType) {
         StructType st = (StructType)t;
         String structName = st.getName();
         return "%struct." + structName + "*";
      }
      return "unknown";
   }

   private Table<LLVMStructFieldEntry> buildDeclarationsTable(
      List<Declaration> decls, 
      Table<LLVMStructFieldEntry> prev)
   {
      int position = 0;
      Table<LLVMStructFieldEntry> declsTable = new Table<LLVMStructFieldEntry>(prev, "identifiers");
      for (Declaration d : decls) {
         String typeRep = getTypeLLVMRepresentation(d.getType());
         try {
            declsTable.insert(d.getName(), new LLVMStructFieldEntry(Integer.toString(position), typeRep));
         } catch (DuplicatedIdentifierDeclarationException e) {
            System.out.println(e.getErrorMessage());
         }
         position++;
      }
      return declsTable;
   }

   private void insertFunctionsTable(Function f, Table<FunctionType> funcsTable)
   {
      try {
         funcsTable.insert(f.getName(), new FunctionType(f.getLineNum(), f.getName(), f.getParams(), f.getRetType()));
      } catch (DuplicatedIdentifierDeclarationException e) {
         System.out.println(e.getErrorMessage());
      }
      return;
   }

   private String getRegAndValRep(String name)
   {
      if (name.charAt(0) != '@' && name.charAt(0) != '%') {
         return "%" + name;
      }
      return name;
   }

   private void printStringToFile(String s)
   {
      if (output != null) {
         System.out.print(s);
         try {
            bufferedWriter.write(s);
         } catch (Exception e) {
            e.printStackTrace();
            try {
               bufferedWriter.close();
            } catch (Exception exc) {
            }
         }
      }
   }


   //SSA methods
   
   private void writeVariable(String variable, LLVMBlockType block, LLVMType value)
   {
      HashMap<String, LLVMType> m = block.getVarTable();
      if (m.containsKey(variable)) {
         m.replace(variable, value);
      } else { 
         m.put(variable, value); 
      }
   }

   private void writePhiVariable(String variable, LLVMBlockType block, LLVMPhiType value) //write to phiTable
   {
      HashMap<String, LLVMPhiType> m = block.getPhiTable();
      if (m.containsKey(variable)) {
         m.replace(variable, value);
      } else { 
         m.put(variable, value); 
      } 
   }

   private LLVMType readVariable(String variable, LLVMBlockType block)
   {
      HashMap<String, LLVMType> m = block.getVarTable();
      if (m.containsKey(variable)) {
         return m.get(variable);
      } else {
         return readVariableFromPredecessors(variable, block);
      }
   }

   private LLVMType readVariableFromPredecessors (String variable, LLVMBlockType block)
   {
      LLVMType val;
      if (!block.isSealed()) {
         String t = getValTypeFromPredecessors(block, variable);
         LLVMPhiType phi = new LLVMPhiType(block);
         LLVMRegisterType reg = createNewRegister(t);
         phi.setRegister(reg);
         writePhiVariable(variable, block, phi);
         val = reg;
      } else if (block.getPredecessors().size() == 0) {
         val =  new LLVMVoidType(); //undefined
      } else if (block.getPredecessors().size() == 1) {
         val =  readVariable(variable, block.getPredecessors().get(0));
      } else {
         LLVMPhiType phi = new LLVMPhiType(block);
         LLVMRegisterType reg = createNewRegister("i32");
         phi.setRegister(reg);
         writePhiVariable(variable, block, phi);
         writeVariable(variable,block,reg);
         addPhiOperands(variable, phi); 
         val = reg;
      }
      writeVariable(variable, block, val); //update current map
      return val;
   }

   private void addPhiOperands(String variable, LLVMPhiType phi)
   {
      ArrayList<LLVMBlockType> preds = phi.getBlock().getPredecessors();
      for (LLVMBlockType pred : preds) {
         phi.addPhiOperand(new LLVMPhiEntryType(readVariable(variable, pred), pred));
      }
   }
   private String getValTypeFromPredecessor(LLVMBlockType block, String variable){
      HashMap<String, LLVMType> m = block.getVarTable();
      if (m.containsKey(variable)) {
         LLVMType t = m.get(variable);
         if (t instanceof LLVMRegisterType) return ((LLVMRegisterType)t).getTypeRep();
         else if (t instanceof LLVMPrimitiveType ) return ((LLVMPrimitiveType)t).getTypeRep();
         else return "i32";
      } 
      else {
         if (block.getPredecessors().size() == 0) return null;
         for (LLVMBlockType pred : block.getPredecessors()){
            return getValTypeFromPredecessor(block, variable);
         }
         return null;
      }
   }
   private String getValTypeFromPredecessors(LLVMBlockType block, String variable){
      for (LLVMBlockType pred : block.getPredecessors()){
         String s =  getValTypeFromPredecessor(pred, variable);
         if (s==null) continue;
         else return s;
      }
      return null;
   }

   private void sealBlock(LLVMBlockType block)
   {
      HashMap<String, LLVMPhiType> tbl = block.getPhiTable();
      Set<Map.Entry<String, LLVMPhiType>> entries = tbl.entrySet();
      for (Map.Entry<String, LLVMPhiType> e : entries) {
         String variable = e.getKey();
         LLVMPhiType phi = e.getValue();
         addPhiOperands(variable, phi);
      }
      block.seal();
   }
   private LLVMRegisterType createNewRegister(String type){
      String regId = "u" + Integer.toString(registerCounter++);
      return new LLVMRegisterType(type, regId);
   }

}
