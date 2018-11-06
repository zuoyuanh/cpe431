package llvm;

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
      }
   }
}