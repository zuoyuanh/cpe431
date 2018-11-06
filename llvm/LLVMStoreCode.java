package llvm;

public class LLVMStoreCode extends LLVMCode
{
   private LLVMType source;
   private LLVMType target;

   public LLVMStoreCode(LLVMType source, LLVMType target)
   {
      super();
      this.source = source;
      this.target = target;
   }

   public String toString()
   {
      source = getOperand(source, target.getTypeRep());
      return getConversions() + "store " + target.getTypeRep() + " " + source + ", " 
         + target.getTypeRep() + "* " + target + "\n";
   }

   public void replaceRegister(LLVMType oldVal, LLVMType newVal)
   {
      if (source.equals(oldVal)) {
         source = newVal;
      }
   }
}