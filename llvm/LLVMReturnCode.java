package llvm;

public class LLVMReturnCode extends LLVMCode
{
   private LLVMType value;
   private boolean isVoid;

   public LLVMReturnCode()
   {
      super();
      this.isVoid = true;
   }

   public LLVMReturnCode(LLVMType value)
   {
      this.value = value;
   }

   public String toString()
   {
      if (this.isVoid) {
         return "ret void\n";
      }
      return "ret " + value.getTypeRep() + " " + value + "\n";
   }

   public boolean isRedirectInstruction()
   {
      return true;
   }

   public void replaceRegister(LLVMType oldVal, LLVMType newVal)
   {
      if (value.equals(oldVal)) {
         value = newVal;
      }
   }
}