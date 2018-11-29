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
   }

   public static enum Operator
   {
      ADD, SUB, MUL, AND, ORR, EOR
   }

   public String operatorToString(Operator op){
      switch (op){
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

   public String toString(){
      String lf = leftType.toString();
      String rt = "";
      if (rightType instanceof LLVMRegisterType) rt = ((LLVMRegisterType)rightType).toString();
      else if (rightType instanceof LLVMPrimitiveType)  rt = "#"+ ((LLVMPrimitiveType)rightType).getValueRep();
      return operatorToString(operator) + " " + resultReg.toString() + ", " + lf + ", " + rt + "\n";
   }
}
