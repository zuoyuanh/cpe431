package llvm;

public class ARMBinaryOperationCode extends ARMCode
{
   
   private Operator operator;
   private LLVMRegisterType leftType;
   private LLVMType rightType;
   private LLVMRegisterType resultReg;

   public ARMBinaryOperationCode(LLVMRegisterType leftType, LLVMType rightType, LLVMRegisterType resReg, Operator operator)
   {
      super();
      this.operator = operator;
      this.leftType = leftType;
      this.rightType = rightType;
      this.resultReg = resReg;
      setDef(resReg);
      addUse(leftType);
      if (rightType instanceof LLVMRegisterType) {
         addUse((LLVMRegisterType)rightType);
      }
   }

   public static enum Operator
   {
      ADD, SUB, MUL, AND, ORR, EOR
   }

   public String operatorToString(Operator op){
      switch (op)
      {
      case ADD: 
         return "add";
      case SUB: 
         return "sub";
      case MUL:
         return "mul";
      case AND:
         return "and";
      case ORR:
         return "orr";
      case EOR:
         return "eor";
      default:
         return "";
      }
   }

   public String toString()
   {  
      String res = ""; 
      String lf = leftType.toString();
      if (leftType.getAllocatedARMRegister() == null) {
         res  = loadSpill(res, ARMCode.r9, leftType);
         lf = ARMCode.r9.toString();
      }
      String rt = "";
      if (rightType instanceof LLVMRegisterType) {
         if (((LLVMRegisterType)rightType).getAllocatedARMRegister() == null) {
            res = loadSpill(res, ARMCode.r10, (LLVMRegisterType)rightType);
            rt = ARMCode.r10.toString();
         } else {
            rt = ((LLVMRegisterType)rightType).toString();
         }
      } else if (rightType instanceof LLVMPrimitiveType) {
         rt = "#"+ ((LLVMPrimitiveType)rightType).getValueRep();
      }
      
      String resReg = resultReg.toString();
      if ((resultReg).getAllocatedARMRegister() == null) {
            resReg = ARMCode.r10.toString();
      }
      res += operatorToString(operator) + " " + resReg + ", " + lf + ", " + rt + "\n";
      if ((resultReg).getAllocatedARMRegister() == null) {
            res = loadSpill(res, ARMCode.r10, resultReg);
      }
      return res;
   }
}
