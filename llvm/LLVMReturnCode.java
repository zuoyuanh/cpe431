package llvm;

import java.util.List;
import java.util.ArrayList;

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

   public List<LLVMRegisterType> dependenciesList()
   {
      List<LLVMRegisterType> result = new ArrayList<LLVMRegisterType>();
      if (value instanceof LLVMRegisterType) {
         result.add((LLVMRegisterType)value);
      }
      return result;
   }
}