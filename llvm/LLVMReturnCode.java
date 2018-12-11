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
         if (newVal instanceof LLVMRegisterType) {
            ((LLVMRegisterType)newVal).addUse(this);
         }
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

   public List<ARMCode> generateArmCode()
   {
      mergeARMConversions();
      if (!isVoid) {
         armCode.add(new ARMMoveCode(ARMCode.r0, value, ARMMoveCode.Operator.MOV));
      }
      armCode.add(Compiler.createResetStackPointerToSavedRegsCode());
      armCode.add(Compiler.createPopCalleeSavedRegisterCode());
      List<LLVMRegisterType> popList = new ArrayList<LLVMRegisterType>();
      popList.add(ARMCode.fp);
      popList.add(ARMCode.pc);
      armCode.add(Compiler.createResetStackPointerToFpCode());
      armCode.add(new ARMPushPopCode(popList, ARMPushPopCode.Operator.POP));
      return armCode;
   }
}