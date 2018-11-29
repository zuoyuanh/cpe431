package llvm;

import java.util.List;
import java.util.ArrayList;
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

   public  BinaryExpression.Operator getOperator(){
      return operator;
   }
   
   public LLVMType getRightType()
   {
      return this.rightType;
   }
   
   public LLVMType getLeftType()
   {
      return this.leftType;
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
         if (newVal instanceof LLVMRegisterType) {
            ((LLVMRegisterType)newVal).addUse(this);
         }
      }
      if (rightType.equals(oldVal)) {
         rightType = newVal;
         if (newVal instanceof LLVMRegisterType) {
            ((LLVMRegisterType)newVal).addUse(this);
         }
      }
   }

   public List<LLVMRegisterType> dependenciesList()
   {
      List<LLVMRegisterType> result = new ArrayList<LLVMRegisterType>();
      if (leftType instanceof LLVMRegisterType) {
         result.add((LLVMRegisterType)leftType);
      }
      if (rightType instanceof LLVMRegisterType) {
         result.add((LLVMRegisterType)rightType);
      }
      return result;
   }

   public LLVMType getDef()
   {
      return resultReg;
   }

   public List<ARMCode> generateArmCode()
   {
      switch (operator) {
         case TIMES:
            LLVMRegisterType lftOp1 = getReg(leftType);
            LLVMType rtOp1 = getReg(rightType);
            armCode.add(new ARMBinaryOperationCode(lftOp1, rtOp1, (LLVMRegisterType)resultReg, ARMBinaryOperationCode.Operator.MUL));
            break;
         case DIVIDE:
            LLVMType lftOp12 = getOperand(leftType);
            LLVMType rtOp12 = getOperand(rightType);
            armCode.add(new ARMMoveCode(ARMCode.r0, lftOp12, ARMMoveCode.Operator.MOV));
            armCode.add(new ARMMoveCode(ARMCode.r1, rtOp12, ARMMoveCode.Operator.MOV));
            armCode.add(new ARMBranchCode("__aeabi_idiv", ARMBranchCode.Operator.BL));
            armCode.add(new ARMMoveCode((LLVMRegisterType)resultReg, ARMCode.r0, ARMMoveCode.Operator.MOV));

            break;
         case PLUS:
            LLVMRegisterType lftOp2 = getReg(leftType);
            LLVMType rtOp2 = getOperand(rightType);
            armCode.add(new ARMBinaryOperationCode(lftOp2, rtOp2, (LLVMRegisterType)resultReg, ARMBinaryOperationCode.Operator.ADD));
            break;
         case MINUS:
            LLVMRegisterType lftOp3 = getReg(leftType);
            LLVMType rtOp3 = getOperand(rightType);
            armCode.add(new ARMBinaryOperationCode(lftOp3, rtOp3, (LLVMRegisterType)resultReg, ARMBinaryOperationCode.Operator.SUB));
            break;
         case LT:
            armCode.add(new ARMMoveCode((LLVMRegisterType)resultReg, new LLVMPrimitiveType("i32","0"), ARMMoveCode.Operator.MOV));
            LLVMRegisterType lftOp6 = getReg(leftType);
            LLVMType rtOp6 = getReg(rightType);
            armCode.add(new ARMMoveCode(lftOp6, rtOp6, ARMMoveCode.Operator.CMP));
            armCode.add(new ARMMoveCode((LLVMRegisterType)resultReg, new LLVMPrimitiveType("i32","1"), ARMMoveCode.Operator.MOVLT));  
            break;
         case GT:
            armCode.add(new ARMMoveCode((LLVMRegisterType)resultReg, new LLVMPrimitiveType("i32","0"), ARMMoveCode.Operator.MOV));
            LLVMRegisterType lftOp7 = getReg(leftType);
            LLVMType rtOp7 = getReg(rightType);
            armCode.add(new ARMMoveCode(lftOp7, rtOp7, ARMMoveCode.Operator.CMP));
            armCode.add(new ARMMoveCode((LLVMRegisterType)resultReg, new LLVMPrimitiveType("i32","1"), ARMMoveCode.Operator.MOVGT));  
            break;
         case LE:
            armCode.add(new ARMMoveCode((LLVMRegisterType)resultReg, new LLVMPrimitiveType("i32","0"), ARMMoveCode.Operator.MOV));
            LLVMRegisterType lftOp9 = getReg(leftType);
            LLVMType rtOp9 = getReg(rightType);
            armCode.add(new ARMMoveCode(lftOp9, rtOp9, ARMMoveCode.Operator.CMP));
            armCode.add(new ARMMoveCode((LLVMRegisterType)resultReg, new LLVMPrimitiveType("i32","1"), ARMMoveCode.Operator.MOVLE));  
            break;
         case GE:
            armCode.add(new ARMMoveCode((LLVMRegisterType)resultReg, new LLVMPrimitiveType("i32","0"), ARMMoveCode.Operator.MOV));
            LLVMRegisterType lftOp8 = getReg(leftType);
            LLVMType rtOp8 = getReg(rightType);
            armCode.add(new ARMMoveCode(lftOp8, rtOp8, ARMMoveCode.Operator.CMP));
            armCode.add(new ARMMoveCode((LLVMRegisterType)resultReg, new LLVMPrimitiveType("i32","1"), ARMMoveCode.Operator.MOVGE));  
            break;
         case EQ:
            armCode.add(new ARMMoveCode((LLVMRegisterType)resultReg, new LLVMPrimitiveType("i32","0"), ARMMoveCode.Operator.MOV));
            LLVMRegisterType lftOp10 = getReg(leftType);
            LLVMType rtOp10 = getReg(rightType);
            armCode.add(new ARMMoveCode(lftOp10, rtOp10, ARMMoveCode.Operator.CMP));
            armCode.add(new ARMMoveCode((LLVMRegisterType)resultReg, new LLVMPrimitiveType("i32","1"), ARMMoveCode.Operator.MOVEQ));  
            break;
         case NE:
            armCode.add(new ARMMoveCode((LLVMRegisterType)resultReg, new LLVMPrimitiveType("i32","0"), ARMMoveCode.Operator.MOV));
            LLVMRegisterType lftOp11 = getReg(leftType);
            LLVMType rtOp11 = getReg(rightType);
            armCode.add(new ARMMoveCode(lftOp11, rtOp11, ARMMoveCode.Operator.CMP));
            armCode.add(new ARMMoveCode((LLVMRegisterType)resultReg, new LLVMPrimitiveType("i32","1"), ARMMoveCode.Operator.MOVNE));  
            break;
         case AND:
            LLVMRegisterType lftOp4 = getReg(leftType);
            LLVMType rtOp4 = getOperand(rightType);
            armCode.add(new ARMBinaryOperationCode(lftOp4, rtOp4, (LLVMRegisterType)resultReg, ARMBinaryOperationCode.Operator.AND));
            break;
         case OR:
            LLVMRegisterType lftOp5 = getReg(leftType);
            LLVMType rtOp5 = getOperand(rightType);
            armCode.add(new ARMBinaryOperationCode(lftOp5, rtOp5, (LLVMRegisterType)resultReg, ARMBinaryOperationCode.Operator.ORR));
            break;
         default:
            break;
      }
      return armCode;
   }
}
