package llvm;
import java.util.ArrayList;
public class ARMMoveCode extends ARMCode
{
   private Operator operator;
   private LLVMType operand;
   private LLVMRegisterType resultReg;
   private String debugTag;

   public ARMMoveCode(LLVMRegisterType resReg, LLVMType op, Operator operator, int debugTag)
   {
      super();
      this.operator = operator;
      this.operand = op;
      this.resultReg = resReg;
      this.debugTag = debugTag + " ";
      if (operator == Operator.CMP) {
         addUse(resReg);
         if (op instanceof LLVMRegisterType) {
            addUse((LLVMRegisterType)op);
         }
      } else {
         setDef(resReg);
         if (op instanceof LLVMRegisterType) {
            addUse((LLVMRegisterType)op);
         }
      }
      if (operator == Operator.MOVEQ
       || operator == Operator.MOVLT
       || operator == Operator.MOVGT
       || operator == Operator.MOVNE
       || operator == Operator.MOVGE
       || operator == Operator.MOVLE) {
         addUse(resReg);
      }
   }

   public static enum Operator
   {
      MOV, MOVW, MOVT, MOVEQ, MOVLT, MOVGT, MOVNE, MOVLE, MOVGE, CMP
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
      default:
         return "";
      }
   }

   public String toString()
   {
      String res = "";
      if (operator == Operator.CMP) { //cmp r1, operand
         String opr = "";
         String resReg = "";
         if (operand instanceof LLVMRegisterType) {
            if (((LLVMRegisterType)operand).getAllocatedARMRegister() == null) {
               res = loadSpill(res, ARMCode.r9, (LLVMRegisterType)operand);
               opr = ARMCode.r9.toString();
            } else { 
               opr = ((LLVMRegisterType)operand).toString();
            }
         } else if (operand instanceof LLVMPrimitiveType) {
            opr = "#" + ((LLVMPrimitiveType)operand).getValueRep();
         }
         if ((resultReg).getAllocatedARMRegister() == null) {
            resReg = ARMCode.r10.toString();
            res = loadSpill(res, ARMCode.r10, (LLVMRegisterType)resultReg);
            res += operatorToString(operator) + " " + resReg + ", " + opr + "\n";
         } else {
            resReg = resultReg.toString();
            res += operatorToString(operator) + " " + resReg + ", " + opr + "\n";
         }
      } else { //mov r1, operand
         String opr = "";
         String resReg = "";
         if (operand instanceof LLVMRegisterType
          && ((LLVMRegisterType)operand).getAllocatedARMRegister() != null
          && ((LLVMRegisterType)operand).getAllocatedARMRegister().equals(resultReg.getAllocatedARMRegister())) {
            return "";
         }
         if (operand instanceof LLVMRegisterType) {
            if (((LLVMRegisterType)operand).getAllocatedARMRegister() == null) {
               res = loadSpill(res, ARMCode.r9, (LLVMRegisterType)operand);
               opr = ARMCode.r9.toString();
            } else { 
               opr = ((LLVMRegisterType)operand).toString();
            }
         } else if (operand instanceof LLVMPrimitiveType) {
            opr = "#"+ ((LLVMPrimitiveType)operand).getValueRep();
         }
         if ((resultReg).getAllocatedARMRegister() == null) {
            resReg = ARMCode.r10.toString();
            res += operatorToString(operator) + " " + resReg + ", " + opr + "\n";
            res = storeSpill(res, ARMCode.r10, resultReg);
         } else {
            resReg = resultReg.toString();
            res += operatorToString(operator) + " " + resReg + ", " + opr + "\n";
         }
      }
      return res;
   }
}
