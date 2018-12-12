package llvm;

import ast.*;
import staticChecker.*;
import java.io.File;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.List;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import exceptions.IdentifierNotFoundException;
import exceptions.DuplicatedIdentifierDeclarationException;

public class SSAVisitor implements LLVMVisitor<LLVMType, LLVMBlockType>
{
   public static int registerCounter = 0;

   public static final int PARAM_REG_NUMS = 4;

   private File output;
   private BufferedWriter bufferedWriter;

   private int blockCounter = 0;

   private Table<Table<LLVMStructFieldEntry>> typesTable = new Table<Table<LLVMStructFieldEntry>>(null, "type");
   private Table<Integer> typesSizeTable = new Table<Integer>(null, "any");
   private Table<FunctionType> funcsTable = new Table<FunctionType>(null, "functions");

   private LLVMBlockType programBlock = null;

   private LLVMBlockType funcExitBlock = null;
   private String funcExitBlockId = null;
   private String funcRetValueTypeRep = null;
   private List<LLVMBlockType> blockList = null;
   private HashSet<LLVMRegisterType> regList = null;

   private List<LLVMBlockType> globalBlockList;

   /**
    * @local generateARM
    *
    * if set to be true, the visitor will generate ARM code
    * otherwise the visitor will generate LLVM code
    */
   public static boolean generateARM = true;

   public SSAVisitor(File output)
   {
      registerCounter = 0;
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
      programBlock = new LLVMBlockType("PROG", true, LLVMBlockType.Label.PROGRAM);
      if (generateARM) {
         printStringToFile("\t.arch armv7-a");
      } else {
         printStringToFile("target triple=\"i686\"\n");
      }
      List<TypeDeclaration> types = program.getTypes();
      for (TypeDeclaration typeDecl : types) {
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
            if (generateARM) {
               printStringToFile("\t.comm   " + varName + ",4,4");
            } else {
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
            }
            // writeVariable(varName, programBlock, new LLVMRegisterType(typeRep, "@" + varName));
            Compiler.putIntoOriginalGlobalVariablesMap(varName, typeRep);
            printStringToFile("\n");
         }
      }
      printStringToFile("\n");

      if (generateARM) {
         printStringToFile("\t.text\n");
      }

      List<Function> funcs = program.getFuncs();
      for (Function func : funcs){
         LLVMType funcType = this.visit(func);
         if (funcType instanceof LLVMBlockType) {
            programBlock.addSuccessor((LLVMBlockType)funcType);
         }
      }

      if (generateARM) {
         printStringToFile("\t.section\t.rodata\n");
         printStringToFile("\t.align	2\n");
         printStringToFile(".PRINTLN_FMT:\n");
         printStringToFile("\t.asciz	\"%ld\\n\"\n");
         printStringToFile("\t.align	2\n");
         printStringToFile(".PRINT_FMT:\n");
         printStringToFile("\t.asciz	\"%ld \"\n");
         printStringToFile("\t.align	2\n");
         printStringToFile(".READ_FMT:\n");
         printStringToFile("\t.asciz	\"%ld\"\n");
         printStringToFile("\t.comm	.read_scratch,4,4\n");
         printStringToFile("\t.global	__aeabi_idiv\n");
      } else {
         printStringToFile("declare i8* @malloc(i32)\n");
         printStringToFile("declare void @free(i8*)\n");
         printStringToFile("declare i32 @printf(i8*, ...)\n");
         printStringToFile("declare i32 @scanf(i8*, ...)\n");
         printStringToFile("@.println = private unnamed_addr constant [5 x i8] c\"%ld\\0A\\00\", align 1\n");
         printStringToFile("@.print = private unnamed_addr constant [5 x i8] c\"%ld \\00\", align 1\n");
         printStringToFile("@.read = private unnamed_addr constant [4 x i8] c\"%ld\\00\", align 1\n");
         printStringToFile("@.read_scratch = common global i32 0, align 8\n");
      }

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

      if (!generateARM) {
         printStringToFile(typeDeclString + "}\n");
      }
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
      int paramCnt = 0;
      List<LLVMRegisterType> overedRegisterList = new ArrayList<LLVMRegisterType>();
      for (Declaration param : params) {
         LLVMType t = this.visit(param);
         if (t instanceof LLVMDeclType) {
            String originalName = ((LLVMDeclType)t).getName();
            String tTypeRep = ((LLVMDeclType)t).getTypeRep();
            paramsRep += tTypeRep + " %" + ((LLVMDeclType)t).getName() + ", ";
            LLVMRegisterType reg = new LLVMRegisterType(tTypeRep, "%" + originalName);
            writeVariable(originalName, startBlock, reg);
            if (paramCnt < PARAM_REG_NUMS) {
               ARMMoveCode movCode = new ARMMoveCode(reg, ARMCode.argRegs[paramCnt], ARMMoveCode.Operator.MOV, 49);
               paramsDecls.add(movCode);
            } else {
               overedRegisterList.add(0, reg);
            }
            paramCnt++;
         }
      }

      int offset = 4;
      for (LLVMRegisterType reg : overedRegisterList) {
         Compiler.putLocalVariable(reg.getId());
         paramsDecls.add(new ARMLoadStoreCode(reg, ARMCode.fp, ARMLoadStoreCode.Operator.LDR, offset + ""));
         offset += 4;
      }

      startBlock.addToARMFront(paramsDecls);

      if (paramsRep.length() > 2 && paramsRep.charAt(paramsRep.length()-2) == ',') {
         paramsRep = paramsRep.substring(0, paramsRep.length()-2);
      }
      paramsRep += ")";

      if (generateARM) {
         printStringToFile("\t.align 2\n");
         printStringToFile("\t.global " + func.getName() + "\n");
         printStringToFile(func.getName() + ":\n");
      } else {
         printStringToFile("define " + returnTypeLLVMRep + " @" + func.getName() + paramsRep + "\n{\n");
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

      sealBlock(funcExitBlock);  //seal the exit block

      List<LLVMPhiCode> phiCodes = new ArrayList<LLVMPhiCode>();
      
      for (LLVMBlockType block : blockList) {
         if (block.getPredecessors().size() == 0 && !block.isEntry() && !block.getBlockId().equals(funcExitBlockId)) {
            continue;
         }
         HashMap<String, LLVMPhiType> phiTable = block.getPhiTable();
         for (String id : phiTable.keySet()) {
            LLVMPhiType phi = phiTable.get(id);
            LLVMPhiCode phiCode = new LLVMPhiCode(phi.getRegister(), phi.getPhiOperands());
            phi.getRegister().setDef(phiCode);
            block.addToFront(phiCode);
            phiCodes.add(phiCode);
            for (LLVMPhiEntryType ty : phi.getPhiOperands()) {
               LLVMType t = ty.getOperand();
               addToUsesList(t, phiCode);
            }
         }
      }

      removeTrivialPhis(phiCodes);
      sparseSimpleConstantPropagation();

      if (generateARM) {
         for (LLVMPhiCode phiCode : phiCodes) {
            phiCode.processPhiDefs();
         }
      }
      
      markUsefulInstructionInBlock(blockList);
      localVariableNumbering(startBlock);

      if (generateARM) {
         for (LLVMBlockType block : blockList) {
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
            LLVMType sizeType = new LLVMPrimitiveType("i32", spillsStackSize + "");
            functionSetup.add(new ARMBinaryOperationCode(ARMCode.sp, sizeType, 
                             ARMCode.sp, ARMBinaryOperationCode.Operator.SUB));
            Compiler.getResetStackPointerToFpCode().enable();
            Compiler.getResetStackPointerToSavedRegsCode().setRightType(sizeType);
            Compiler.getResetStackPointerToSavedRegsCode().enable();
         }
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
            printStringToFile(block.getBlockId() + ": \n");
            /*
            List<LLVMCode> llvmCode = block.getLLVMCode();
            for (LLVMCode code : llvmCode) {
               if (code.isMarked() && (!code.isRemoved())) {
                  block.addARMCode(code.generateArmCode());
               }
            }*/
            for (ARMCode code : block.getARMCode()) {
               String codeString = code.toString();
               if (codeString != null && codeString.length() > 0 && code.isEnabled()) {
                  printStringToFile("\t" + code);
               }
            }
            if (!block.isClosed() && !block.getBlockId().equals(funcExitBlockId))
            {
               printStringToFile("\tb ." + funcExitBlockId + "\n");
            }

         }
         printStringToFile("\t.size " + func.getName() + ", .-" + func.getName() + "\n");
      } else {
         for (LLVMBlockType block : blockList) {
            if (block.getPredecessors().size() == 0 && !block.isEntry() 
               && !block.getBlockId().equals(funcExitBlockId) 
               && !block.getBlockId().equals("." + funcExitBlockId)) {
               continue;
            }
            globalBlockList.add(block);
            printStringToFile(block.getBlockId() + ": \n");
            List<LLVMCode> llvmCode = block.getLLVMCode();
            for (LLVMCode code : llvmCode) {
               if (code.isMarked() && (!code.isRemoved())) {
                  printStringToFile("\t" + code);
               }
            }
            if (!block.isClosed() && !block.getBlockId().equals(funcExitBlockId)) {
               printStringToFile("\tbr label %" + funcExitBlockId + "\n");
            }
         }
         printStringToFile("}\n\n");
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
            String newRegId = "u" + Integer.toString(registerCounter++);
            LLVMRegisterType newReg = new LLVMRegisterType("i32", newRegId);
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
            LLVMType tmpReg = createNewRegister("i32");
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
            LLVMRegisterType tempReg = new LLVMRegisterType(fieldTypeRep + "*", "u" + Integer.toString(registerCounter++));
            /* block.add("%" + tempReg + " = getelementptr " + typeRep + " %" + regName 
                    + ", i1 0, " + fieldTypeRep + " " + fieldPositionRep + "\n"); */
            regList.add(tempReg);
            LLVMGetPtrCode getPtrCode = new LLVMGetPtrCode(tempReg, leftType, fieldPositionRep);
            block.add(getPtrCode);
            tempReg.setDef(getPtrCode);
            addToUsesList(leftType, getPtrCode);
            LLVMRegisterType resultReg = new LLVMRegisterType(fieldTypeRep, "u" + Integer.toString(registerCounter++));
            LLVMLoadCode loadCode = new LLVMLoadCode(tempReg, resultReg);
            block.add(loadCode);
            addToUsesList(tempReg, loadCode);
            resultReg.setDef(loadCode);
            regList.add(resultReg);
            return resultReg;
         } catch (Exception e) {
            printStringToFile(id + ": IDENTIFIER NOT FOUND 2\n");
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
            LLVMRegisterType returnReg = new LLVMRegisterType(returnTypeRep, "u" + Integer.toString(registerCounter++));
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
         printStringToFile("IDENTIFIER NOT FOUND 3\n");
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
            LLVMRegisterType tempReg = new LLVMRegisterType(fieldTypeRep, "u" + Integer.toString(registerCounter++));
            // block.add("%" + tempReg + " = getelementptr " + typeRep + " %" + regName 
            //         + ", i1 0, " + fieldTypeRep + " " + fieldPositionRep + "\n");
            LLVMGetPtrCode getPtrCode = new LLVMGetPtrCode(tempReg, leftType, fieldPositionRep);
            block.add(getPtrCode);
            tempReg.setDef(getPtrCode);
            addToUsesList(leftType, getPtrCode);
            regList.add(tempReg);
            return tempReg;
         } catch (Exception e) {
            printStringToFile(id + ": IDENTIFIER NOT FOUND 1\n");
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
      String typeRep = Compiler.getGlobalVariablesMap().get(variable);
      if (Compiler.getGlobalVariablesMap().containsKey(variable)) {
         LLVMStoreCode storeCode = new LLVMStoreCode(value, new LLVMRegisterType(typeRep, "@" + variable));
         addToUsesList(value, storeCode);
         block.add(storeCode);
         return;
      }
      HashMap<String, LLVMType> m = block.getVarTable();
      if (m.containsKey(variable)) {
         m.replace(variable, value);
      } else { 
         m.put(variable, value); 
      }
      // add to SSA regList
      if ((regList != null) && (value instanceof LLVMRegisterType)) {
         regList.add((LLVMRegisterType)value); 
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
      if (Compiler.getGlobalVariablesMap().containsKey(variable)) {
         String typeRep = Compiler.getGlobalVariablesMap().get(variable);
         LLVMRegisterType resultReg = createNewRegister(typeRep);
         LLVMLoadCode loadCode = new LLVMLoadCode(new LLVMRegisterType(typeRep + "*", "@" + variable), resultReg);
         block.add(loadCode);
         ((LLVMRegisterType)resultReg).setDef(loadCode);
         regList.add((LLVMRegisterType)resultReg);
         return resultReg;
      }
      LLVMType resultReg = readVariableCore(variable, block);
      return resultReg;
   }

   private LLVMType readVariableCore(String variable, LLVMBlockType block)
   {
      HashMap<String, LLVMType> m = block.getVarTable();
      if (m.containsKey(variable)) {
         return m.get(variable);
      } else {
         return readVariableFromPredecessors(variable, block);
      }
   }

   private LLVMType readVariableFromPredecessors(String variable, LLVMBlockType block)
   {
      LLVMType val;
      if (!block.isSealed()) {
         LLVMPhiType phi = new LLVMPhiType(block);
         LLVMRegisterType reg = createNewRegister(null);
         phi.setRegister(reg);
         writePhiVariable(variable, block, phi);
         String t = getValTypeFromPredecessors(block, variable);
         phi.setRegisterType(t);
         val = reg;
      } else if (block.getPredecessors().size() == 0) {
         val =  new LLVMVoidType(); //undefined
      } else if (block.getPredecessors().size() == 1) {
         val =  readVariableCore(variable, block.getPredecessors().get(0));
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
      List<LLVMBlockType> preds = phi.getBlock().getPredecessors();
      for (LLVMBlockType pred : preds) {
         LLVMType val = readVariableCore(variable, pred);
         phi.addPhiOperand(new LLVMPhiEntryType(val, pred));
         if (val instanceof LLVMRegisterType) {
            phi.setRegisterType(((LLVMRegisterType)val).getTypeRep());
            //phi.getRegister().addUse();
            //r.
         } else if (val instanceof LLVMPrimitiveType) {
            phi.setRegisterType(((LLVMPrimitiveType)val).getTypeRep());
         } else {
            phi.setRegisterType("i32");
         }
      }
   }
   private String getValTypeFromPredecessor(LLVMBlockType block, String variable)
   {
      HashMap<String, LLVMType> m = block.getVarTable();
      if (m.containsKey(variable)) {
         LLVMType t = m.get(variable);
         if (t instanceof LLVMRegisterType) {
            return ((LLVMRegisterType)t).getTypeRep();
         } else if (t instanceof LLVMPrimitiveType) {
            return ((LLVMPrimitiveType)t).getTypeRep();
         } else {
            return "i32";
         }
      } else {
         return getValTypeFromPredecessors(block, variable);
      }
   }
   private String getValTypeFromPredecessors(LLVMBlockType block, String variable)
   {
      for (LLVMBlockType pred : block.getPredecessors()) {
         String s =  getValTypeFromPredecessor(pred, variable);
         if (s == null) {
            continue;
         }
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

   public static LLVMRegisterType createNewRegister(String type)
   {
      String regId = "u" + Integer.toString(registerCounter++);
      return new LLVMRegisterType(type, regId);
   }

   public static LLVMRegisterType createNewPhiDefRegister(String type)
   {
      String regId = "_phi_u" + Integer.toString(registerCounter++);
      return new LLVMRegisterType(type, regId);
   }

   private void addToUsesList(LLVMType reg, LLVMCode c){
      if (reg instanceof LLVMRegisterType){
         ((LLVMRegisterType)reg).addUse(c);
      }
   }

   private void sparseSimpleConstantPropagation()
   {
      ArrayList<LLVMRegisterType> workList = new ArrayList<LLVMRegisterType>();
      HashMap<LLVMRegisterType, SSCPValue> valueTable = new HashMap<LLVMRegisterType, SSCPValue>();
      
      for (LLVMRegisterType r : regList) {
         initialize(r, valueTable);
      }
      for (LLVMRegisterType r : regList){
         SSCPValue orig = valueTable.get(r);
         SSCPValue v = evaluate(r.getDef(), valueTable);
         if (! orig.equals(v))
            valueTable.put(r, v);
         if (!(v instanceof SSCPTop)) {
            workList.add(r);
         }
      }
   
      while (!workList.isEmpty()) {
         LLVMRegisterType reg = workList.remove(0);

         List<LLVMCode> uses = reg.getUses();

         for (LLVMCode use : uses) {
            LLVMType def = use.getDef(); //for a code, need to find the reg it defined
            if (def!=null && def instanceof LLVMRegisterType && !(valueTable.get((LLVMRegisterType)def) instanceof SSCPBottom)) {
               LLVMRegisterType m = (LLVMRegisterType)def;
               SSCPValue val = valueTable.get(m);
               SSCPValue resVal = evaluate(use, valueTable);
               if (resVal != null && (resVal instanceof SSCPIntConstant) && (val instanceof SSCPIntConstant) && !resVal.equals(val)){
                  System.out.println(m+" not constant");
                  resVal = new SSCPBottom();
               }
               if ((resVal != null)  && !(resVal.equals(val))) {
                  valueTable.put(m, resVal);
                  if (!workList.contains(m)) {
                     workList.add(m);
                  }
               }
            }
         }
      }
      
      for (LLVMRegisterType key : valueTable.keySet()) {
         SSCPValue val = valueTable.get(key);
         if (val instanceof SSCPConstant) {
            LLVMPrimitiveType constant = null;
            if (val instanceof SSCPIntConstant) {
               constant = new LLVMPrimitiveType("i32", Integer.toString(((SSCPIntConstant)val).getValue()));
            //} else if (val instanceof SSCPBoolConstant) {
            //   constant = new LLVMPrimitiveType("i1", Integer.toString(((SSCPBoolConstant)val).getValue() ? 1 : 0));
            } else {
               constant = new LLVMPrimitiveType("null", "null");
            }
            for (LLVMCode code : key.getUses()) {
               code.replaceRegister(key, constant);
            }
         }
      }
   }
   
   private void initialize(LLVMRegisterType reg, HashMap<LLVMRegisterType, SSCPValue> valueTable)
   {  
      SSCPValue res = null;
      if (reg.getId().charAt(0) == '@' || reg.toString().charAt(1) != 'u') {
         res = new SSCPBottom(); //global or param
      }
      else {
         LLVMCode c = reg.getDef();
         if (c instanceof LLVMReadCode || c instanceof LLVMCallCode || c instanceof LLVMLoadCode) {
            res = new SSCPBottom();
         } else {
            res = new SSCPTop();
         }
      }
      valueTable.put(reg, res);
      return; 
   }
/*
   private void initialize(LLVMRegisterType reg, HashMap<LLVMRegisterType, SSCPValue> valueTable)
   {
      LLVMCode c = reg.getDef();

   }*/
   private SSCPValue evaluate(LLVMCode c, HashMap<LLVMRegisterType, SSCPValue> valueTable)
   {
      SSCPValue res = null;
      if (c instanceof LLVMBinaryOperationCode) {
         LLVMType lfType = ((LLVMBinaryOperationCode)c).getLeftType();
         LLVMType rtType = ((LLVMBinaryOperationCode)c).getRightType();
         SSCPValue lfVal = new SSCPBottom();
         SSCPValue rtVal = new SSCPBottom();

         if (lfType instanceof LLVMRegisterType)
            lfVal = valueTable.get((LLVMRegisterType)lfType);
         if (rtType instanceof LLVMRegisterType)
            rtVal = valueTable.get((LLVMRegisterType)rtType);
         if (lfType instanceof LLVMPrimitiveType)
            lfVal = getPrimitiveValue((LLVMPrimitiveType)lfType);
         if (rtType instanceof LLVMPrimitiveType)
            rtVal = getPrimitiveValue((LLVMPrimitiveType)rtType);
         

         if (lfVal instanceof SSCPBottom || rtVal instanceof SSCPBottom) {
            res = new SSCPBottom();
         } else if (lfVal instanceof SSCPTop || rtVal instanceof SSCPTop) {
            if (lfVal instanceof SSCPTop && rtVal instanceof SSCPTop) return new SSCPTop();
            if (lfVal instanceof SSCPTop) return rtVal;
            else return lfVal;
         } else {
            res = evaluateBinaryConstants(((LLVMBinaryOperationCode)c).getOperator(), lfVal, rtVal);
            
         }
         
         return res;
      }
      if (c instanceof LLVMPhiCode) {
         List<SSCPValue> phiTypes = new ArrayList<SSCPValue>();
         SSCPTop t = new SSCPTop();
         List<LLVMPhiEntryType> entries = ((LLVMPhiCode)c).getEntries();
         for (LLVMPhiEntryType r : entries){
            LLVMType operand = r.getOperand();
            if (operand instanceof LLVMRegisterType){
               SSCPValue val = valueTable.get((LLVMRegisterType)operand);
               if (val instanceof SSCPBottom ) {
                  return new SSCPBottom();
               } else if (val instanceof SSCPTop) {
                  //phiTypes.add(t);
               } else {
                  phiTypes.add(val);
               }
            }
            else if (operand instanceof LLVMPrimitiveType){
               phiTypes.add(getPrimitiveValue((LLVMPrimitiveType)operand));
            }
         }
         /*
         if (phiTypes.contains(t)) {
            return t;
         }*/
         SSCPValue tmp = null;
         for (SSCPValue v : phiTypes) {
            if (tmp == null) {
               tmp = v;
            } else if (!tmp.equals(v)) { //different constants 
               return new SSCPBottom(); 
            }
         }
         if (tmp ==null){
            return t; //all top
         }
         else {
            return tmp;
         }
      }
      if (c instanceof LLVMStoreCode) {
         LLVMType source = ((LLVMStoreCode)c).getSource();
         if (source instanceof LLVMPrimitiveType) return getPrimitiveValue((LLVMPrimitiveType)source);
         else if (source instanceof LLVMRegisterType) return valueTable.get((LLVMRegisterType)source);
      }
      return new SSCPBottom();
   }

   private SSCPValue evaluateBinaryConstants(BinaryExpression.Operator op, SSCPValue lfVal, SSCPValue rtVal)
   {
      switch (op)
      {
      case TIMES:
         int lf1 = ((SSCPIntConstant)lfVal).getValue();
         int rt1 = ((SSCPIntConstant)rtVal).getValue();
         return new SSCPIntConstant(lf1*rt1);
      case PLUS:
         int lf2 = ((SSCPIntConstant)lfVal).getValue();
         int rt2 = ((SSCPIntConstant)rtVal).getValue();
         return new SSCPIntConstant(lf2+rt2);
      case MINUS:
         int lf3 = ((SSCPIntConstant)lfVal).getValue();
         int rt3 = ((SSCPIntConstant)rtVal).getValue();
         return new SSCPIntConstant(lf3-rt3);
      case DIVIDE: 
         int lf4 = ((SSCPIntConstant)lfVal).getValue();
         int rt4 = ((SSCPIntConstant)rtVal).getValue();
         if (rt4 == 0) return new SSCPBottom();
         return new SSCPIntConstant(lf4/rt4);
      case AND:
         int lf5 = ((SSCPIntConstant)lfVal).getValue();
         int rt5 = ((SSCPIntConstant)rtVal).getValue();
         if (lf5 == 1 && rt5 == 1) return new SSCPIntConstant(1);
         else return new SSCPIntConstant(0);
      case OR:    
         int lf6 = ((SSCPIntConstant)lfVal).getValue();
         int rt6 = ((SSCPIntConstant)rtVal).getValue();
         if (lf6 == 1 || rt6 == 1) return new SSCPIntConstant(1);
         else return new SSCPIntConstant(0);      
      case LT:    
         int lf7 = ((SSCPIntConstant)lfVal).getValue();
         int rt7 = ((SSCPIntConstant)rtVal).getValue();
         if (lf7<rt7) return new SSCPIntConstant(1);
         else return new SSCPIntConstant(0);
      case GT:    
         int lf8 = ((SSCPIntConstant)lfVal).getValue();
         int rt8 = ((SSCPIntConstant)rtVal).getValue();
         if (lf8>rt8) return new SSCPIntConstant(1);
         else return new SSCPIntConstant(0);
      case GE:    
         int lf9 = ((SSCPIntConstant)lfVal).getValue();
         int rt9 = ((SSCPIntConstant)rtVal).getValue();
         if (lf9>=rt9) return new SSCPIntConstant(1);
         else return new SSCPIntConstant(0);
      case LE:    
         int lf10 = ((SSCPIntConstant)lfVal).getValue();
         int rt10 = ((SSCPIntConstant)rtVal).getValue();
         if (lf10<=rt10) return new SSCPIntConstant(1);
         else return new SSCPIntConstant(0);
      case EQ:   
         if (lfVal.equals(rtVal)) return new SSCPIntConstant(1);
         else return new SSCPIntConstant(0);
      case NE:   
         if (!lfVal.equals(rtVal)) return new SSCPIntConstant(1);
         else return new SSCPIntConstant(0);
      default:
         return new SSCPIntConstant(0);
      }
   }

   private SSCPValue getPrimitiveValue(LLVMPrimitiveType p)
   {
      String valRep = p.getValueRep();
      String typeRep = p.getTypeRep();
      try {
         int i = Integer.parseInt(valRep);
         /*
         if (typeRep.equals("i1")){
            return new SSCPBoolConstant(i);
            if (i==0) return new SSCPBoolConstant(false);
         }*/
         return new SSCPIntConstant(i);
      } catch (Exception e) {
      }
      return new SSCPNullConstant();
   }
   
   private void markUsefulInstructionInBlock(List<LLVMBlockType> blocks)
   {
      for (LLVMBlockType block : blocks) {
         for (LLVMCode code : block.getLLVMCode()) {
            if ((code instanceof LLVMReturnCode) || (code instanceof LLVMBranchCode)
            || (code instanceof LLVMCallCode) || (code instanceof LLVMStoreCode)
            || (code instanceof LLVMPrintCode) || (code instanceof LLVMReadCode)
            || (code instanceof LLVMFreeCode)) {
               markUsefulInstruction(code);
            }
         }
      }
   }

   private void markUsefulInstruction(LLVMCode code)
   {
      if (code == null) {
         return;
      }
      code.mark();
      List<LLVMRegisterType> dependencies = code.dependenciesList();
      for (LLVMRegisterType t : dependencies) {
         LLVMRegisterType reg = (LLVMRegisterType)t;
         if (reg.getDependenciesMarked()) {
            continue;
         }
         reg.setDependenciesMarked(true);
         this.markUsefulInstruction(reg.getDef());
      }
   }

   // remove trivial phis

   private boolean isTrivialPhiCode(LLVMPhiCode phiCode)
   {
      List<LLVMPhiEntryType> entries = phiCode.getEntries();
      Set<String> set = new HashSet<String>();
      for (LLVMPhiEntryType phiTy : entries) {
         LLVMType t = phiTy.getOperand();
         String tRep = t.toString();
         if (!set.contains(tRep)) {
            set.add(tRep);
         }
      }
      if (set.size() == 0 || set.size() == 1) {
         return true;
      }
      return false;
   }

   private void removeTrivialPhi(LLVMPhiCode phiCode)
   {
      List<LLVMPhiEntryType> entries = phiCode.getEntries();
      if (entries.size() > 0) {
         for (LLVMCode code : ((LLVMRegisterType)(phiCode.getDef())).getUses()) {
            LLVMType operand = entries.get(0).getOperand();
            if (operand instanceof LLVMPrimitiveType) {
               LLVMPrimitiveType operandPrimitiveType = (LLVMPrimitiveType)operand;
               if (operandPrimitiveType.getValueRep().equals("null")) {
                  operandPrimitiveType.setTypeRep(phiCode.getDef().getTypeRep());
               }
            } else {
               LLVMRegisterType operandRegisterType = (LLVMRegisterType)operand;
               if (operandRegisterType.getTypeRep() == null || operandRegisterType.getTypeRep().equals("null")) {
                  operandRegisterType.setTypeRep(phiCode.getDef().getTypeRep());
               }
            }
            code.replaceRegister(phiCode.getDef(), operand);
         }
      }
      phiCode.remove();
   }

   private List<LLVMPhiCode> splitPhiCodes(List<LLVMPhiCode> workList)
   {
      List<LLVMPhiCode> results = new ArrayList<LLVMPhiCode>();
      List<LLVMPhiCode> originalWorkList = new ArrayList<LLVMPhiCode>();
      for (LLVMPhiCode code : workList) {
         originalWorkList.add(code);
      }
      for (LLVMPhiCode code : originalWorkList) {
         if (isTrivialPhiCode(code)) {
            workList.remove(code);
            results.add(code);
         }
      }
      return results;
   }

   private void removeTrivialPhis(List<LLVMPhiCode> workList)
   {
      List<LLVMPhiCode> trivialList = splitPhiCodes(workList);
      while (trivialList.size() > 0) {
         for (LLVMPhiCode phiCode : trivialList) {
            removeTrivialPhi(phiCode);
         }
         trivialList = splitPhiCodes(workList);
      }
   }

   private void localVariableNumbering(LLVMBlockType block)
   {
      Map<LocalNumberingExpression, LLVMType> table = new HashMap<LocalNumberingExpression, LLVMType>();
      getLocalVariableNumberingSets(block, table);
   }

   private void getLocalVariableNumberingSets(LLVMBlockType block, 
                                Map<LocalNumberingExpression, LLVMType> table)
   {
      if (block.getGenSet() != null || block.getLabel() == LLVMBlockType.Label.PROGRAM) {
         return;
      }
      block.newGenSet();
      block.newKillSet();
      block.newAvailSet();
      Set<LLVMType> modifiedTargets = new HashSet<LLVMType>();

      // generate avail set
      HashSet<LocalNumberingExpression> avail = null;

      for (LLVMBlockType b : block.getPredecessors()) {
         if (b.getAvailSet() == null) {
            this.getLocalVariableNumberingSets(b, table);
         }
         if (b.getGenSet() == null || b.getAvailSet() == null) {
            break;
         }
         HashSet<LocalNumberingExpression> blockGen = new HashSet<LocalNumberingExpression>(b.getGenSet());
         HashSet<LocalNumberingExpression> blockAvail = new HashSet<LocalNumberingExpression>(b.getAvailSet());
         blockAvail.removeAll(b.getKillSet());
         blockGen.addAll(blockAvail);

         if (avail == null) {
            avail = blockGen;
         } else {
            avail.retainAll(blockGen);
         }
      }
      if (avail != null) {
         block.setAvailSet(avail);
      }

      HashSet<LocalNumberingExpression> availSet;

      if (avail != null) {
         availSet = new HashSet<LocalNumberingExpression>(avail);
      } else {
         availSet = new HashSet<LocalNumberingExpression>();
      }

      // generate gen set
      for (LLVMCode code : block.getLLVMCode()) {
         if (!code.isRemoved() && code.isMarked()) {
            LocalNumberingExpression exp = code.getExpressionForLocalNumbering();
            if (exp != null) {
               if (code instanceof LLVMBinaryOperationCode) {
                  LLVMType resultReg = ((LLVMBinaryOperationCode)code).getResultReg();
                  if (availSet.contains(exp)) {
                     if (resultReg instanceof LLVMRegisterType) {
                        for (LLVMCode use : ((LLVMRegisterType)resultReg).getUses()) {
                           use.replaceRegister(resultReg, table.get(exp));
                        }
                     }
                     code.remove();
                  } else {
                     availSet.add(exp);
                     table.put(exp, resultReg);
                  }
                  modifiedTargets.add(resultReg);
               }
               block.addToGenSet(exp);
            }
         }   
      }

      // generate kill set
      for (LLVMType t : modifiedTargets) {
         if (t instanceof LLVMRegisterType) {
            ArrayList<LLVMCode> uses = ((LLVMRegisterType)t).getUses();
            for (LLVMCode use : uses) {
               if (!use.isRemoved() && use.isMarked()) {
                  LocalNumberingExpression exp = use.getExpressionForLocalNumbering();
                  if (exp != null) {
                     block.addToKillSet(exp);
                  }
               }
            }
         }
      }

      for (LLVMBlockType b : block.getSuccessors()) {
         this.getLocalVariableNumberingSets(b, table);
      }
   }

   private boolean calculateLiveOutSet(LLVMBlockType block){
      ArrayList<LLVMBlockType> successors = block.getSuccessors();
      if (successors.size() == 0) {
         return false;
      }
      HashSet<LLVMRegisterType> newLiveOutSet = new HashSet<LLVMRegisterType>();
      Set<LLVMRegisterType> oldLiveOutSet = block.getLiveOutSet();
      for (LLVMBlockType b : successors) {
         HashSet<LLVMRegisterType> temp = new HashSet<LLVMRegisterType>();
         temp.addAll(b.getLiveOutSet());
         temp.removeAll(b.getArmKillSet());
         temp.addAll(b.getArmGenSet());
         newLiveOutSet.addAll(temp);
      }
      if (!(newLiveOutSet.containsAll(oldLiveOutSet)) || !(oldLiveOutSet.containsAll(newLiveOutSet))) { //not the same
         block.setLiveOutSet(newLiveOutSet);
         return true; //changed
      }
      return false;
   }
}
