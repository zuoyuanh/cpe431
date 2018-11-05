package llvm;

public class LLVMLoadCode extends LLVMCode
{
   private LLVMType ptrReg;
   private LLVMType resultReg;

   public LLVMLoadCode(LLVMType ptrReg, LLVMType resultReg)
   {
      super();
      this.ptrReg = ptrReg;
      this.resultReg = resultReg;
   }

   public String toString()
   {
      return getConversions() + resultReg + " = load " + ptrReg.getTypeRep() + " " + ptrReg + "\n";
   }

   public void replaceRegister(LLVMType oldVal, LLVMType newVal)
   {
      if (ptrReg.equals(oldVal)) {
         ptrReg = newVal;
      }
   }
}