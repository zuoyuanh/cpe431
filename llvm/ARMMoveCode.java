package llvm;

public class ARMMoveCode extends ARMCode
{
   private Operator operator;
   private LLVMType operand;
   private LLVMRegisterType resultReg;

   public ARMMoveCode(LLVMRegisterType resReg, LLVMType op, Operator operator)
   {
      super();
      this.operator = operator;
      this.operand = op;
      this.resultReg = resReg;
   }

   public static enum Operator
   {
      MOV, MOVW, MOVT, MOVEQ, MOVLT, MOVGT, MOVNE, MOVLE, MOVGE, CMP, TMP
   }

   public String operatorToString(Operator op)
   {
      switch (op)
      {
      case MOV: 
         return "mov";
      case MOVW: 
         return "movw";
      case MOVT:
         return "movt";
      case MOVEQ:
         return "moveq";
      case MOVLT:
         return "movlt";
      case MOVGT:
         return "movgt";
      case MOVNE:
         return "movne";
      case MOVGE:
         return "movge";
      case MOVLE:
         return "movle";
      case CMP:
         return "cmp";
      case TMP:
         return "mov-tmp";
      default:
         return "";
      }
   }

   public String toString()
   {
      String opr = "";
      if (operand instanceof LLVMRegisterType) {
         opr = ((LLVMRegisterType)operand).toString();
      } else if (operand instanceof LLVMPrimitiveType) {
         opr = "#"+ ((LLVMPrimitiveType)operand).getValueRep();
      }
      return operatorToString(operator) + " " + resultReg.toString() + ", " + opr + "\n";
   }
}
