package llvm;

import java.util.List;

public class LLVMPhiDefCode extends LLVMCode
{
   private LLVMRegisterType phiRegister;
   private LLVMType target;

   public LLVMPhiDefCode(LLVMRegisterType r, LLVMType target)
   {
      this.phiRegister = r;
      this.target = target;
   }

   public String toString()
   {
      return "aha";
   }

   public void replaceRegister(LLVMType oldVal, LLVMType newVal)
   {
      if (target.equals(oldVal)) {
         target = newVal;
         if (newVal instanceof LLVMRegisterType) {
            ((LLVMRegisterType)newVal).addUse(this);
         }
      }
   }

   public List<ARMCode> generateArmCode()
   {
      this.armCode.add(new ARMMoveCode(phiRegister, target, ARMMoveCode.Operator.MOV));
      return armCode;
   }
}
