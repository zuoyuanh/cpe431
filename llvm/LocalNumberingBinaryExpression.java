package llvm;

import ast.BinaryExpression;

public class LocalNumberingBinaryExpression implements LocalNumberingExpression
{
   private BinaryExpression.Operator operator;
   private LLVMType opnd1;
   private LLVMType opnd2;

   public LocalNumberingBinaryExpression(BinaryExpression.Operator operator, LLVMType opnd1, LLVMType opnd2)
   {
      this.operator = operator;
      this.opnd1 = opnd1;
      this.opnd2 = opnd2;
   }

   public BinaryExpression.Operator getOperator()
   {
      return this.operator;
   }

   public LLVMType getFirstOpnd()
   {
      return this.opnd1;
   }

   public LLVMType getSecondOpnd()
   {
      return this.opnd2;
   }

   @Override
   public boolean hasOpnd(LLVMType opnd)
   {
      if (opnd1.equals(opnd) || opnd2.equals(opnd)) {
         return true;
      }
      return false;
   }

   private String binaryOperationString(BinaryExpression.Operator operator)
   {
      switch (operator) {
      case TIMES:
         return "*";
      case DIVIDE:
         return "/";
      case PLUS:
         return "+";
      case MINUS:
         return "-";
      case LT:
         return "<";
      case GT:
         return ">";
      case LE:
         return "<=";
      case GE:
         return ">=";
      case EQ:
         return "==";
      case NE:
         return "!=";
      case AND:
         return "&&";
      case OR:
         return "||";
      default:
         return "unknown";
      }
   }

   @Override
   public int hashCode()
   {
      return (binaryOperationString(this.operator) + opnd1.toString() + opnd2.toString()).hashCode();
   }

   @Override
   public String toString()
   {
      return this.opnd1 + " " + this.operator + " " + this.opnd2;
   }

   @Override
   public boolean equals(Object other)
   {
      if ((other == null) || (getClass() != other.getClass())) {
         return false;
      } else {
         LocalNumberingBinaryExpression exp = (LocalNumberingBinaryExpression)other;
         return (operator == exp.getOperator()) && (opnd1.equals(exp.getFirstOpnd())) && (opnd2.equals(exp.getSecondOpnd())); 
      }
   }
}