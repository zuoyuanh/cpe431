package llvm;

import java.util.List;
import java.util.ArrayList;

public class LLVMFreeCode extends LLVMCode
{
   private LLVMType opnd;

   public LLVMFreeCode(LLVMType opnd)
   {
      super();
      this.opnd = opnd;
   }

   public String toString()
   {
      opnd = getOperand(opnd, "i8*");
      return getConversions() + "\tcall void @free(i8* " + opnd + ")\n";
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
      this.armCode = new ArrayList<ARMCode>();
      LLVMType opndType = getOperand(opnd);
      armCode.add(new ARMMoveCode(ARMCode.r0, opndType, ARMMoveCode.Operator.MOV));
      armCode.add(new ARMBranchCode("free", ARMBranchCode.Operator.BL));
      return armCode;
   }
}
