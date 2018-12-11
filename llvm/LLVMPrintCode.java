package llvm;

import java.util.List;
import java.util.ArrayList;

public class LLVMPrintCode extends LLVMCode
{
   private boolean newLine;
   private LLVMType opnd;

   public LLVMPrintCode(LLVMType opnd, boolean newLine)
   {
      super();
      this.opnd = opnd;
      this.newLine = newLine;
   }

   public String toString()
   {
      if (newLine) {
         return "call i32 (i8*, ...)* @printf(i8* getelementptr inbounds ([5 x i8]* @.println, i32 0, i32 0), "
               + opnd.getTypeRep() + " " + opnd + ")\n";
      } else {
         return "call i32 (i8*, ...)* @printf(i8* getelementptr inbounds ([5 x i8]* @.print, i32 0, i32 0), "
               + opnd.getTypeRep() + " " + opnd + ")\n";
      }
   }
   
   public void replaceRegister(LLVMType oldVal, LLVMType newVal)
   {
      if (opnd.equals(oldVal)) {
         opnd = newVal;
         if (newVal instanceof LLVMRegisterType) {
            ((LLVMRegisterType)newVal).addUse(this);
         }
      }
   }

   public List<LLVMRegisterType> dependenciesList()
   {
      List<LLVMRegisterType> result = new ArrayList<LLVMRegisterType>();
      if (opnd instanceof LLVMRegisterType) {
         result.add((LLVMRegisterType)opnd);
      }
      return result;
   }
   
   public List<ARMCode> generateArmCode()
   {
      LLVMType opndType = getOperand(opnd);
      armCode.add(new ARMMoveCode(ARMCode.r1, opndType, ARMMoveCode.Operator.MOV, 39));
      armCode.add(new ARMMoveCode(ARMCode.r0, new LLVMPrimitiveType("i32", ":lower16:.PRINTLN_FMT"), ARMMoveCode.Operator.MOVW, 40));
      armCode.add(new ARMMoveCode(ARMCode.r0, new LLVMPrimitiveType("i32", ":upper16:.PRINTLN_FMT"), ARMMoveCode.Operator.MOVT, 41));
      armCode.add(new ARMBranchCode("printf", ARMBranchCode.Operator.BL));
      return armCode;
   }
}
