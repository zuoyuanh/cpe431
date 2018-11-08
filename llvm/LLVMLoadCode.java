package llvm;

import java.util.List;
import java.util.ArrayList;

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
         if (newVal instanceof LLVMRegisterType) {
            ((LLVMRegisterType)newVal).addUse(this);
         }
      }
   }

   public List<LLVMRegisterType> dependenciesList()
   {
      List<LLVMRegisterType> result = new ArrayList<LLVMRegisterType>();
      if (ptrReg instanceof LLVMRegisterType) {
         result.add((LLVMRegisterType)ptrReg);
      }
      return result;
   }

   public LLVMType getDef()
   {
      return resultReg;
   }
}