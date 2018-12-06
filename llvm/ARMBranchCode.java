package llvm;

public class ARMBranchCode extends ARMCode
{
   private Operator operator;
   private String label;

   public ARMBranchCode(String label, Operator operator)
   {
      super();
      this.label = label;
      this.operator = operator;
   }

   public static enum Operator
   {
      BEQ, BNE, BGE, BLT, B, BL
   }

   public String operatorToString(Operator op)
   {
      switch (op){
      case BEQ:
         return "beq";
      case BNE:
         return "bne";
      case BGE:
         return "bge";
      case BLT:
         return "blt";
      case B:
         return "b";
      case BL:
         return "bl";
      default:
         return "";
      }
   }

   public String toString()
   {
      return operatorToString(operator) + " " + label + "\n";
   }
}
