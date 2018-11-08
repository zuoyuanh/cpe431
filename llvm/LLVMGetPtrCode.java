package llvm;

import java.util.List;
import java.util.ArrayList;

public class LLVMGetPtrCode extends LLVMCode
{
   private LLVMType resultReg;
   private LLVMType source;
   private String positionOffset;

   public LLVMGetPtrCode(LLVMType resultReg, LLVMType source, String positionOffset)
   {
      super();
      this.resultReg = resultReg;
      this.source = source;
      this.positionOffset = positionOffset;
   }

   public String toString()
   {
      return resultReg + " = getelementptr " + source.getTypeRep() + " " + source
            + ", i1 0, i32 " + positionOffset + "\n";
   }
   
   public void replaceRegister(LLVMType oldVal, LLVMType newVal)
   {
      if (source.equals(oldVal)) {
         source = newVal;
         if (newVal instanceof LLVMRegisterType) {
            ((LLVMRegisterType)newVal).addUse(this);
         }
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
      return resultReg;
   }
}