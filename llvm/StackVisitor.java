package llvm;

import ast.*;
import staticChecker.*;
import java.util.List;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import exceptions.IdentifierNotFoundException;
import exceptions.DuplicatedIdentifierDeclarationException;

public class StackVisitor implements LLVMVisitor<LLVMType, LLVMBlockType>
{
   private int blockCounter = 0;

   private Table<Table<LLVMStructFieldEntry>> typesTable = new Table<Table<LLVMStructFieldEntry>>(null, "type");
   private Table<Type> declsTable = new Table<Type>(null, "identifiers");
   private Table<Integer> typesSizeTable = new Table<Integer>(null, "any");
   private Table<FunctionType> funcsTable = new Table<FunctionType>(null, "functions");

   private LLVMBlockType programBlock = null;

   private LLVMBlockType funcExitBlock = null;
   private String funcExitBlockId = null;
   private String funcRetValueTypeRep = null;
   private List<LLVMBlockType> blockList = null;
   private HashSet<LLVMRegisterType> regList = null;

   private List<LLVMBlockType> globalBlockList;

   public LLVMType visit(Program program)
   {
      globalBlockList = new ArrayList<LLVMBlockType>();
      programBlock = new LLVMBlockType("PROG", true, LLVMBlockType.Label.PROGRAM);
      if (Compiler.generateARM) {
         Compiler.printStringToFile("\t.arch armv7-a");
      } else {
         Compiler.printStringToFile("target triple=\"i686\"\n");
      }
      List<TypeDeclaration> types = program.getTypes();
      for (TypeDeclaration typeDecl : types) {
         this.visit(typeDecl);
      }
      Compiler.printStringToFile("\n");

      List<Declaration> decls = program.getDecls();
      for (Declaration decl : decls){
         LLVMType t = this.visit(decl);
         if (t instanceof LLVMDeclType) {
            LLVMDeclType declType = (LLVMDeclType)t;
            String declName = "@" + declType.getName();
            try {
               declsTable.insert(declName, decl.getType());
            } catch (Exception e) {
            }
            String typeRep = declType.getTypeRep();
            Compiler.printStringToFile(declName + " = common global ");
            Compiler.printStringToFile(typeRep + " ");
            if (typeRep.equals("i32")) {
               Compiler.printStringToFile("0, align 4");
            } else if (typeRep.equals("i1")) {
               Compiler.printStringToFile("0, align 1");
            } else {
               Compiler.printStringToFile("null, align 8");
            }
            Compiler.printStringToFile("\n");
         }
      }
      Compiler.printStringToFile("\n");

      if (Compiler.generateARM) {
         Compiler.printStringToFile("\t.text\n");
      }

      List<Function> funcs = program.getFuncs();
      for (Function func : funcs){
         LLVMType funcType = this.visit(func);
         if (funcType instanceof LLVMBlockType) {
            programBlock.addSuccessor((LLVMBlockType)funcType);
         }
      }

      if (Compiler.generateARM) {
         Compiler.printStringToFile("\t.section\t.rodata\n");
         Compiler.printStringToFile("\t.align	2\n");
         Compiler.printStringToFile(".PRINTLN_FMT:\n");
         Compiler.printStringToFile("\t.asciz	\"%ld\\n\"\n");
         Compiler.printStringToFile("\t.align	2\n");
         Compiler.printStringToFile(".PRINT_FMT:\n");
         Compiler.printStringToFile("\t.asciz	\"%ld \"\n");
         Compiler.printStringToFile("\t.align	2\n");
         Compiler.printStringToFile(".READ_FMT:\n");
         Compiler.printStringToFile("\t.asciz	\"%ld\"\n");
         Compiler.printStringToFile("\t.comm	.read_scratch,4,4\n");
         Compiler.printStringToFile("\t.global	__aeabi_idiv\n");
      } else {
         Compiler.printStringToFile("declare i8* @malloc(i32)\n");
         Compiler.printStringToFile("declare void @free(i8*)\n");
         Compiler.printStringToFile("declare i32 @printf(i8*, ...)\n");
         Compiler.printStringToFile("declare i32 @scanf(i8*, ...)\n");
         Compiler.printStringToFile("@.println = private unnamed_addr constant [5 x i8] c\"%ld\\0A\\00\", align 1\n");
         Compiler.printStringToFile("@.print = private unnamed_addr constant [5 x i8] c\"%ld \\00\", align 1\n");
         Compiler.printStringToFile("@.read = private unnamed_addr constant [4 x i8] c\"%ld\\00\", align 1\n");
         Compiler.printStringToFile("@.read_scratch = common global i32 0, align 8\n");
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
      Compiler.printStringToFile(typeDeclString + "}\n");
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
      regList = new HashSet<LLVMRegisterType>();
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

      startBlock.addPredecessor(programBlock);

      List<ARMCode> functionSetup = new ArrayList<ARMCode>();
      List<ARMCode> paramsDecls = new ArrayList<ARMCode>();

      // Push fp, lr
      List<LLVMRegisterType> pushList = new ArrayList<LLVMRegisterType>();
      pushList.add(ARMCode.fp);
      pushList.add(ARMCode.lr);
      functionSetup.add(new ARMPushPopCode(pushList, ARMPushPopCode.Operator.PUSH));
      functionSetup.add(new ARMBinaryOperationCode(ARMCode.sp, new LLVMPrimitiveType("i32", "4"), ARMCode.fp, ARMBinaryOperationCode.Operator.ADD));

      // Reset shared function info
      Compiler.resetFunction();

      // Reset global map for current function
      Compiler.setGlobalVariablesMap(new HashMap<String, String>(Compiler.getOriginalGlobalVariablesMap()));
      for (Declaration local : locals) {
         LLVMType t = this.visit(local);
         if (t instanceof LLVMDeclType) {
            String tNameRep = ((LLVMDeclType)t).getName();
            Compiler.getGlobalVariablesMap().remove(tNameRep);
         }
      }

      // Declare params
      for (Declaration param : params) {
         LLVMType t = this.visit(param);
         if (t instanceof LLVMDeclType) {
            String originalName = ((LLVMDeclType)t).getName();
            String tTypeRep = ((LLVMDeclType)t).getTypeRep();
            paramsRep += tTypeRep + " %" + ((LLVMDeclType)t).getName() + ", ";
            LLVMRegisterType reg = new LLVMRegisterType(tTypeRep, "%" + originalName);
            writeParamDeclVariable(originalName, startBlock, reg);
         }
      }

      // startBlock.addToARMFront(paramsDecls);

      if (paramsRep.length() > 2 && paramsRep.charAt(paramsRep.length()-2) == ',') {
         paramsRep = paramsRep.substring(0, paramsRep.length()-2);
      }
      paramsRep += ")";

      if (Compiler.generateARM) {
         Compiler.printStringToFile("\t.align 2\n");
         Compiler.printStringToFile("\t.global " + func.getName() + "\n");
         Compiler.printStringToFile(func.getName() + ":\n");
      } else {
         Compiler.printStringToFile("define " + returnTypeLLVMRep + " @" + func.getName() + paramsRep + "\n{\n");
      }

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
         retBlock.add(new LLVMReturnCode());
      } else {
         LLVMType retValType = readVariable("_retval_", retBlock);
         if (retValType instanceof LLVMRegisterType) {
            LLVMRegisterType retValRegisterType = (LLVMRegisterType)retValType;
            if (retValRegisterType.getTypeRep() == null || retValRegisterType.getTypeRep().equals("null")) {
               retValRegisterType.setTypeRep(funcRetValueTypeRep);
            }
            LLVMReturnCode retCode = new LLVMReturnCode(retValType);
            retBlock.add(retCode);
            ((LLVMRegisterType)retValType).addUse(retCode);
         } else if (retValType instanceof LLVMPrimitiveType) {
            LLVMPrimitiveType retValPrimitiveType = (LLVMPrimitiveType)retValType;
            if (retValPrimitiveType.getValueRep().equals("null")) {
               retValPrimitiveType.setTypeRep(funcRetValueTypeRep);
            }
            retBlock.add(new LLVMReturnCode(retValType));
         }
      }

      if (Compiler.generateARM) {
         /* for (LLVMBlockType block : blockList) {
            Set<LLVMRegisterType> armGenSet = block.newArmGenSet(); 
            Set<LLVMRegisterType> armKillSet = block.newArmKillSet();
            block.newLiveOutSet();
            List<LLVMCode> llvmCode = block.getLLVMCode();
            for (LLVMCode code : llvmCode) {
               if (code.isMarked() && (!code.isRemoved())) {
                  block.addARMCode(code.generateArmCode());
               }
            }

            // live registers analysis 
            for (ARMCode code : block.getARMCode()) {
               List<LLVMRegisterType> uses = code.getUses();
               LLVMRegisterType def = code.getDef();
               if (def != null) {
                  armKillSet.add(def);
               }
               if (uses != null) {
                  for (LLVMRegisterType u : uses){
                     if (!armKillSet.contains(u)){
                        armGenSet.add(u);
                     }
                  }
               }
            }
         }

         // live registers analysis, already initialize liveOut as empty sets
         boolean changed = true;
         while (changed){
            changed = false;
            for (int i = blockList.size()-1; i>=0; i--){ 
               changed = changed || calculateLiveOutSet(blockList.get(i));
            }
         }

         InterferenceGraph g = new InterferenceGraph();
         for (LLVMBlockType block : blockList) {
            Set<LLVMRegisterType> liveOut = block.getLiveOutSet();
            List<ARMCode> armCode = block.getARMCode();
            for (int i=armCode.size()-1; i>=0; i--) {
               ARMCode code = armCode.get(i);
               LLVMRegisterType target = code.getDef();
               if (target != null) {
                  for (LLVMRegisterType element : liveOut){
                     g.addEdge(target, element);
                  }
               }
               liveOut.remove(target);
               List<LLVMRegisterType> sources = code.getUses();
               if (sources != null) {
                  liveOut.addAll(sources);
               }
            }
         }
         g.allocateRegister();

         List<LLVMRegisterType> calleeSavedRegisters = Compiler.getCalleeSavedRegisters();
         if (calleeSavedRegisters != null && calleeSavedRegisters.size() > 0) {
            functionSetup.add(new ARMPushPopCode(calleeSavedRegisters, ARMPushPopCode.Operator.PUSH));
         }
         int spillsStackSize = Compiler.getLocalVariableStackSize();
         if (spillsStackSize != 0) {
            LLVMType sizeType = new LLVMPrimitiveType("i32", spillsStackSize + 4 + "");
            functionSetup.add(new ARMBinaryOperationCode(ARMCode.sp, sizeType, 
                             ARMCode.sp, ARMBinaryOperationCode.Operator.SUB));
            Compiler.getResetStackPointerToSavedRegsCode().setRightType(sizeType);
            Compiler.getResetStackPointerToSavedRegsCode().enable();
         }
         Compiler.getResetStackPointerToFpCode().enable();
         startBlock.addToARMFront(functionSetup);

         ARMPushPopCode returnPopCode = Compiler.getPopCalleeSavedRegisterCode();
         returnPopCode.setRegList(calleeSavedRegisters);

         for (LLVMBlockType block : blockList) {
            if (block.getPredecessors().size() == 0 && !block.isEntry() 
            && !block.getBlockId().equals(funcExitBlockId) 
            && !block.getBlockId().equals("." + funcExitBlockId)) {
               continue;
            }
            globalBlockList.add(block);
            Compiler.printStringToFile(block.getBlockId() + ": \n");
            for (ARMCode code : block.getARMCode()) {
               String codeString = code.toString();
               if (codeString != null && codeString.length() > 0 && code.isEnabled()) {
                  Compiler.printStringToFile("\t" + code);
               }
            }
            if (!block.isClosed() && !block.getBlockId().equals(funcExitBlockId))
            {
               Compiler.printStringToFile("\tb ." + funcExitBlockId + "\n");
            }
         }
         Compiler.printStringToFile("\t.size " + func.getName() + ", .-" + func.getName() + "\n"); */
      } else {
         for (LLVMBlockType block : blockList) {
            if (block.getPredecessors().size() == 0 && !block.isEntry() 
               && !block.getBlockId().equals(funcExitBlockId) 
               && !block.getBlockId().equals("." + funcExitBlockId)) {
               continue;
            }
            globalBlockList.add(block);
            Compiler.printStringToFile(block.getBlockId() + ": \n");
            List<LLVMCode> llvmCode = block.getLLVMCode();
            for (LLVMCode code : llvmCode) {
               Compiler.printStringToFile("\t" + code);
            }
            if (!block.isClosed() && !block.getBlockId().equals(funcExitBlockId)) {
               Compiler.printStringToFile("\tbr label %" + funcExitBlockId + "\n");
            }
         }
         Compiler.printStringToFile("}\n\n");
      }
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
      if (sourceType instanceof LLVMPrimitiveType) {
         LLVMPrimitiveType sourcePrimitiveType = (LLVMPrimitiveType)sourceType;
         if (sourcePrimitiveType.getValueRep().equals("null")) {
            sourcePrimitiveType.setTypeRep(targetType.getTypeRep());
         }
      }
      if (target instanceof LvalueId) {
         String id = ((LvalueId)target).getId();
         if (sourceType instanceof LLVMReadExpressionType) {
            LLVMRegisterType newReg = Compiler.createNewRegister("i32");
            LLVMCode readCode = ((LLVMReadExpressionType)sourceType).getSSAReadInstruction(newReg);
            block.add(readCode);
            newReg.setDef(readCode);
            writeVariable(id, block, newReg);
         } else {
            writeVariable(id, block, sourceType);
         }
         return new LLVMVoidType();
      }
      if (targetType instanceof LLVMRegisterType) {
         if (sourceType instanceof LLVMReadExpressionType) {
            LLVMType tmpReg = Compiler.createNewRegister("i32");
            LLVMCode readCode = ((LLVMReadExpressionType)sourceType).getSSAReadInstruction(tmpReg);
            ((LLVMRegisterType)tmpReg).setDef(readCode);
            block.add(readCode);
            LLVMStoreCode storeCode = new LLVMStoreCode(tmpReg, targetType);
            addToUsesList(tmpReg, storeCode);
            block.add(storeCode);
            return new LLVMVoidType();
         }
         LLVMStoreCode storeCode = new LLVMStoreCode(sourceType, targetType);
         addToUsesList(sourceType, storeCode);
         block.add(storeCode);
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

         block.addSuccessor(thenLLVMBlock);
         block.addSuccessor(elseLLVMBlock);
         thenLLVMBlock.addPredecessor(block);
         elseLLVMBlock.addPredecessor(block);
     
         blockList.add(thenLLVMBlock);
         blockList.add(elseLLVMBlock);

         LLVMBranchCode branchCode = new LLVMBranchCode(guardType, thenLLVMBlock, elseLLVMBlock);
         block.add(branchCode);
         addToUsesList(guardType, branchCode);
         if (thenBlock != null) {
            LLVMType thenBlockType = this.visit(thenBlock, thenLLVMBlock);
            if (thenBlockType instanceof LLVMBlockType) {
               thenLLVMBlock = (LLVMBlockType)thenBlockType;
               thenLLVMBlockId = ((LLVMBlockType)thenBlockType).getBlockId();
            }
         }
         if (elseBlock != null) {
            LLVMType elseBlockType = this.visit(elseBlock, elseLLVMBlock);
            if (elseBlockType instanceof LLVMBlockType) {
               elseLLVMBlock = (LLVMBlockType)elseBlockType;
               elseLLVMBlockId = ((LLVMBlockType)elseBlockType).getBlockId();
            }
         }

         if (((((LLVMBlockType)thenLLVMBlock).getReturned())) && ((((LLVMBlockType)elseLLVMBlock).getReturned()))) {
            return new LLVMVoidType();
         } else {
            LLVMBlockType jointLLVMBlock = new LLVMBlockType(jointLLVMBlockId, true, LLVMBlockType.Label.JOIN);
            if (!thenLLVMBlock.isClosed()) {
               thenLLVMBlock.add(new LLVMBranchCode(jointLLVMBlock));
               thenLLVMBlock.addSuccessor(jointLLVMBlock);
               jointLLVMBlock.addPredecessor(thenLLVMBlock);
            }
            if (!elseLLVMBlock.isClosed()) {
               elseLLVMBlock.add(new LLVMBranchCode(jointLLVMBlock));
               elseLLVMBlock.addSuccessor(jointLLVMBlock); 
               jointLLVMBlock.addPredecessor(elseLLVMBlock);
            }
            blockList.add(jointLLVMBlock);
            return jointLLVMBlock;
         }
      }
      return new LLVMVoidType();
   }

   public LLVMType visit(DeleteStatement deleteStatement, LLVMBlockType block)
   {
      Expression exp = deleteStatement.getExpression();
      LLVMType expType = this.visit(exp, block);
      LLVMFreeCode freeCode = new LLVMFreeCode(expType);
      addToUsesList(expType, freeCode);
      block.add(freeCode);
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
      LLVMPrintCode printCode = new LLVMPrintCode(expType, true);
      block.add(printCode);
      addToUsesList(expType, printCode);
      return new LLVMVoidType();
   }

   public LLVMType visit(PrintStatement printStatement, LLVMBlockType block)
   {
      Expression exp = printStatement.getExpression();
      LLVMType expType = this.visit(exp, block);
      LLVMPrintCode printCode = new LLVMPrintCode(expType, false);
      block.add(printCode);
      addToUsesList(expType, printCode); 
      return new LLVMVoidType();
   }

   public LLVMType visit(ReturnEmptyStatement returnEmptyStatement, LLVMBlockType block)
   {
      block.add(new LLVMBranchCode(funcExitBlock));
      block.setReturned(true);
      block.addSuccessor(funcExitBlock);
      funcExitBlock.addPredecessor(block);
      return new LLVMVoidType();
   }

   public LLVMType visit(ReturnStatement returnStatement, LLVMBlockType block)
   {
      Expression exp = returnStatement.getExpression();
      LLVMType expType = this.visit(exp, block);
      if (expType instanceof LLVMRegisterType || expType instanceof LLVMPrimitiveType) {
         LLVMReturnConversionCode c = new LLVMReturnConversionCode(expType, funcRetValueTypeRep);
         if (c.toString().length() != 0) {
            addToUsesList(expType, c);
            block.add(c);
         }
         LLVMType opnd = c.getConvertedResultReg();
         if ((opnd instanceof LLVMRegisterType) && (!opnd.equals(expType)) && (opnd != expType)) {
            ((LLVMRegisterType)opnd).setDef(c);
         }
         writeVariable("_retval_", block, opnd);
         block.add(new LLVMBranchCode(funcExitBlock));
      }
      block.addSuccessor(funcExitBlock);
      funcExitBlock.addPredecessor(block);
      block.setReturned(true);
      return new LLVMVoidType();
   }

   public LLVMType visit(WhileStatement whileStatement, LLVMBlockType block)
   {
      LLVMType guardType = this.visit(whileStatement.getGuard(), block);
      if (guardType instanceof LLVMRegisterType || guardType instanceof LLVMPrimitiveType) {
         Statement bodyBlock = whileStatement.getBody();

         String bodyLLVMBlockId = "LU" + Integer.toString(blockCounter++);
         String jointLLVMBlockId = "LU" + Integer.toString(blockCounter++);

         LLVMBlockType bodyLLVMBlock = new LLVMBlockType(bodyLLVMBlockId, LLVMBlockType.Label.WHILE_LOOP);
         LLVMBlockType jointLLVMBlock = new LLVMBlockType(jointLLVMBlockId, true, LLVMBlockType.Label.WHILE_EXIT);
         LLVMBlockType originalBodyLLVMBlock = bodyLLVMBlock;

         blockList.add(bodyLLVMBlock);
         blockList.add(jointLLVMBlock);

         block.addSuccessor(bodyLLVMBlock);
         bodyLLVMBlock.addPredecessor(block);
         block.addSuccessor(jointLLVMBlock);
         jointLLVMBlock.addPredecessor(block);

         LLVMBranchCode branchCode1 = new LLVMBranchCode(guardType, bodyLLVMBlock, jointLLVMBlock);
         block.add(branchCode1);
         addToUsesList(guardType, branchCode1);

         if (bodyBlock != null) {
            LLVMType bodyBlockType = this.visit(bodyBlock, bodyLLVMBlock);
            if (bodyBlockType instanceof LLVMBlockType) {
               bodyLLVMBlock = (LLVMBlockType)bodyBlockType;
               bodyLLVMBlockId = ((LLVMBlockType)bodyLLVMBlock).getBlockId();
            }
         }

         guardType = this.visit(whileStatement.getGuard(), bodyLLVMBlock);
         LLVMBranchCode branchCode2 = new LLVMBranchCode(guardType, originalBodyLLVMBlock, jointLLVMBlock);
         bodyLLVMBlock.add(branchCode2);
         addToUsesList(guardType, branchCode2);
         bodyLLVMBlock.addSuccessor(originalBodyLLVMBlock);
         originalBodyLLVMBlock.addPredecessor(bodyLLVMBlock);
         bodyLLVMBlock.addSuccessor(jointLLVMBlock);
         jointLLVMBlock.addPredecessor(bodyLLVMBlock);
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
   
   public LLVMType visit(BinaryExpression binaryExpression, LLVMBlockType block)
   {
      LLVMType leftType = this.visit(binaryExpression.getLeft(), block);
      LLVMType rightType = this.visit(binaryExpression.getRight(), block);
      BinaryExpression.Operator operator = binaryExpression.getOperator();
      LLVMBinaryOperationCode c = new LLVMBinaryOperationCode(leftType, rightType, operator);
      block.add(c);
      addToUsesList(leftType, c);
      addToUsesList(rightType, c);
      LLVMRegisterType resReg = (LLVMRegisterType)(c.getResultReg());
      resReg.setDef(c);
      regList.add(resReg);
      return resReg;
   }

   public LLVMType visit(DotExpression dotExpression, LLVMBlockType block)
   {
      Expression left = dotExpression.getLeft();
      String id = dotExpression.getId();
      LLVMType leftType = this.visit(left, block);
      if (leftType instanceof LLVMRegisterType) {
         String structTypeRep = ((LLVMRegisterType)leftType).getTypeRep();
         while (structTypeRep.length() > 1 && structTypeRep.charAt(structTypeRep.length()-1) == '*') {
            structTypeRep = structTypeRep.substring(0, structTypeRep.length()-1);
         }
         try {
            Table<LLVMStructFieldEntry> fieldsTable = typesTable.get(structTypeRep);
            LLVMStructFieldEntry field = fieldsTable.get(id);
            String fieldPositionRep = field.getPositionRep();
            String fieldTypeRep = field.getTypeRep();
            LLVMRegisterType tempReg = Compiler.createNewRegister(fieldTypeRep + "*");
            regList.add(tempReg);
            LLVMGetPtrCode getPtrCode = new LLVMGetPtrCode(tempReg, leftType, fieldPositionRep);
            block.add(getPtrCode);
            tempReg.setDef(getPtrCode);
            addToUsesList(leftType, getPtrCode);
            LLVMRegisterType resultReg = Compiler.createNewRegister(fieldTypeRep);
            LLVMLoadCode loadCode = new LLVMLoadCode(tempReg, resultReg);
            block.add(loadCode);
            addToUsesList(tempReg, loadCode);
            resultReg.setDef(loadCode);
            regList.add(resultReg);
            return resultReg;
         } catch (Exception e) {
            Compiler.printStringToFile(id + ": IDENTIFIER NOT FOUND 2\n");
         }
      }
      return new LLVMVoidType();
   }

   public LLVMType visit(IdentifierExpression identifierExpression, LLVMBlockType block)
   {
      String id = identifierExpression.getId();
      return readVariable(id, block);
   }

   public LLVMType visit(IntegerExpression integerExpression)
   {
      return new LLVMPrimitiveType("i32", integerExpression.getValue());
   }

   public LLVMType visit(TrueExpression trueExpression)
   {
      return new LLVMPrimitiveType("i1", "1");
   }
   
   public LLVMType visit(FalseExpression falseExpression)
   {
      return new LLVMPrimitiveType("i1", "0");
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
         List<LLVMType> visitedArgs = new ArrayList<LLVMType>();
         String returnTypeRep = getTypeLLVMRepresentation(f.getRetType());

         for (Expression arg : args) {
            visitedArgs.add(this.visit(arg, block));
         }

         if (!returnTypeRep.equals("void")) {
            LLVMRegisterType returnReg = Compiler.createNewRegister(returnTypeRep);
            LLVMCallCode callCode = new LLVMCallCode(name, params, visitedArgs, returnReg);
            for (LLVMType arg : visitedArgs){
               addToUsesList(arg, callCode);
            }
            regList.add(returnReg);
            block.add(callCode);
            returnReg.setDef(callCode);
            return returnReg;
         } else {
            LLVMCallCode callCode = new LLVMCallCode(name, params, visitedArgs);
            block.add(callCode);
            for (LLVMType arg : visitedArgs){
               addToUsesList(arg, callCode);
            }
         }
      } catch (IdentifierNotFoundException exc) {
         Compiler.printStringToFile("IDENTIFIER NOT FOUND 3\n");
      }
      return new LLVMVoidType();
   }

   public LLVMType visit(NewExpression newExpression, LLVMBlockType block)
   {
      String id = newExpression.getId();
      String structRep = "%struct." + id;
      int size = 4;

      try {
         size = 4 * typesSizeTable.get(structRep);
      } catch (Exception exc) {
      }

      LLVMNewCode c = new LLVMNewCode(size, structRep);
      block.add(c);
      LLVMType resType = c.getConvertedResultReg();
      if (resType instanceof LLVMRegisterType) ((LLVMRegisterType)resType).setDef(c);
      return resType;
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
         String structTypeRep = ((LLVMRegisterType)leftType).getTypeRep();
         while (structTypeRep.length() > 1 && structTypeRep.charAt(structTypeRep.length()-1) == '*') {
            structTypeRep = structTypeRep.substring(0, structTypeRep.length()-1);
         }
         try {
            Table<LLVMStructFieldEntry> fieldsTable = typesTable.get(structTypeRep);
            LLVMStructFieldEntry field = fieldsTable.get(id);
            String fieldPositionRep = field.getPositionRep();
            String fieldTypeRep = field.getTypeRep();
            LLVMRegisterType tempReg = Compiler.createNewRegister(fieldTypeRep);
            LLVMGetPtrCode getPtrCode = new LLVMGetPtrCode(tempReg, leftType, fieldPositionRep);
            block.add(getPtrCode);
            tempReg.setDef(getPtrCode);
            addToUsesList(leftType, getPtrCode);
            regList.add(tempReg);
            return tempReg;
         } catch (Exception e) {
            Compiler.printStringToFile(id + ": IDENTIFIER NOT FOUND 1\n");
         }
      }
      return new LLVMVoidType();
   }

   public LLVMType visit(LvalueId lvalueId, LLVMBlockType block)
   {
      return readVariable(lvalueId.getId(), block);
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
   
   private void writeParamDeclVariable(String variable, LLVMBlockType block, LLVMType value)
   {
      HashMap<String, LLVMType> m = block.getVarTable();
      if (m.containsKey(variable)) {
         LLVMType t = m.get(variable);
         LLVMStoreCode storeCode = new LLVMStoreCode(value, t);
         addToUsesList(value, storeCode);
         block.add(storeCode);
      } else {
         LLVMRegisterType r = new LLVMRegisterType(value.getTypeRep(), "%_P_" + variable);
         LLVMAllocaCode allocaCode = new LLVMAllocaCode(r);
         LLVMStoreCode storeCode = new LLVMStoreCode(value, r);
         addToUsesList(value, storeCode);
         block.add(allocaCode);
         block.add(storeCode);
         m.put(variable, r); 
      }
   }

   private void writeVariable(String variable, LLVMBlockType block, LLVMType value)
   {
      HashMap<String, LLVMType> m = block.getVarTable();
      if (m.containsKey(variable)) {
         LLVMType t = m.get(variable);
         LLVMStoreCode storeCode = new LLVMStoreCode(value, t);
         addToUsesList(value, storeCode);
         block.add(storeCode);
      } else {
         LLVMAllocaCode allocaCode = new LLVMAllocaCode(value);
         block.add(allocaCode);
         m.put(variable, value); 
      }
   }

   private LLVMType readVariable(String variable, LLVMBlockType block)
   {
      return readVariableCore(variable, block, new HashSet<String>());
   }

   private LLVMType readVariableCore(String variable, LLVMBlockType block, Set<String> visitedBlocks)
   {
      HashMap<String, LLVMType> m = block.getVarTable();
      visitedBlocks.add(block.getBlockId());
      if (m.containsKey(variable)) {
         LLVMType varType = m.get(variable);
         String typeRep = varType.getTypeRep();
         LLVMRegisterType resultReg = Compiler.createNewRegister(typeRep);
         LLVMLoadCode loadCode = new LLVMLoadCode(varType, resultReg);
         block.add(loadCode);
         ((LLVMRegisterType)resultReg).setDef(loadCode);
         regList.add((LLVMRegisterType)resultReg);
         return resultReg;
      } else {
         return readVariableFromPredecessors(variable, block, visitedBlocks);
      }
   }

   private LLVMType readVariableFromPredecessors(String variable, LLVMBlockType block, Set<String> visitedBlocks)
   {
      for (LLVMBlockType b : block.getPredecessors()) {
         if (!visitedBlocks.contains(b.getBlockId())) {
            return readVariableCore(variable, b, visitedBlocks);
         }
      }
      return new LLVMVoidType();
   }

   private void addToUsesList(LLVMType reg, LLVMCode c){
      if (reg instanceof LLVMRegisterType){
         ((LLVMRegisterType)reg).addUse(c);
      }
   }
}
