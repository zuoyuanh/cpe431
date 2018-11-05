package llvm;

import ast.BinaryExpression;

public class LLVMBinaryOperationCode extends LLVMCode
{
   private BinaryExpression.Operator operator;
   private LLVMType leftType;
   private LLVMType rightType;
   private LLVMType resultReg;

   public LLVMBinaryOperationCode(LLVMType leftType, LLVMType rightType, 
                        BinaryExpression.Operator operator)
   {
      super();
      this.operator = operator;
      this.leftType = leftType;
      this.rightType = rightType;
      this.resultReg = SSAVisitor.createNewRegister(binaryOperationResultType(operator));
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

   public LLVMType getResultReg()
   {
      return this.resultReg;
   }

   public String toString()
   {
      String oprty = binaryOperationType(operator);
      LLVMType opnd1 = getOperand(leftType, oprty);
      LLVMType opnd2 = getOperand(rightType, oprty);
      if (oprty.equals("any")) {
         if (leftType instanceof LLVMRegisterType) {
            oprty = ((LLVMRegisterType)leftType).getTypeRep();
         } else if (leftType instanceof LLVMPrimitiveType) {
            oprty = ((LLVMPrimitiveType)leftType).getTypeRep();
         }
      }
      return getConversions() + resultReg + " = " + binaryOperationOpcode(operator) 
            + " " + oprty + " " + opnd1 + ", " + opnd2 + "\n";
   }

   public void replaceRegister(LLVMType oldVal, LLVMType newVal)
   {
      if (leftType.equals(oldVal)) {
         leftType = newVal;
      }
      if (rightType.equals(oldVal)) {
         rightType = newVal;
      }
   }
}