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

   public LLVMType getSource(){
      return this.source;
   }

   public LLVMType getTarget(){
      return this.target;
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
      if (target instanceof LLVMRegisterType) {
         result.add((LLVMRegisterType)target);
      }
      return result;
   }
   public List<ARMCode> generateArmCode(){
      this.armCode = new ArrayList<ARMCode>();
      LLVMRegisterType addr = getReg(target);
      LLVMRegisterType sourceReg = getReg(source);
      armCode.add(new ARMLoadStoreCode(sourceReg, addr, ARMLoadStoreCode.Operator.STR));
      return armCode;
   }
}
