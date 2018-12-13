package llvm;

public class LLVMAllocaCode extends LLVMCode
{
   private LLVMType resultReg;

   public LLVMAllocaCode(LLVMType resultReg)
   {
      super();
      this.resultReg = resultReg;
      if (resultReg instanceof LLVMRegisterType) {
         ((LLVMRegisterType)resultReg).setDef(this);
      }
   }

   public String toString()
   {
      return resultReg + " = alloca " + resultReg.getTypeRep() + "\n";
   }

   public void replaceRegister(LLVMType oldVal, LLVMType newVal)
   {
      if (resultReg.equals(oldVal)) {
         resultReg = newVal;
         if (resultReg instanceof LLVMRegisterType) {
            ((LLVMRegisterType)resultReg).setDef(this);
         }
      }
   }
   
   /* 
   public List<ARMCode> generateArmCode()
   {
      LLVMType opndType = getOperand(opnd);
      armCode.add(new ARMMoveCode(ARMCode.r0, opndType, ARMMoveCode.Operator.MOV, 32));
      armCode.add(new ARMBranchCode("free", ARMBranchCode.Operator.BL));
      return armCode;
   } */
}
