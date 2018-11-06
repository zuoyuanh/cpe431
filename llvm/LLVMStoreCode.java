package llvm;

import java.util.List;
import java.util.ArrayList;

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

   public List<LLVMRegisterType> dependenciesList()
   {
      List<LLVMRegisterType> result = new ArrayList<LLVMRegisterType>();
      if (source instanceof LLVMRegisterType) {
         result.add((LLVMRegisterType)source);
      }
      return result;
   }

   public LLVMType getDef()
   {
      return target;
   }
}