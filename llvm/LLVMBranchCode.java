package llvm;

public class LLVMBranchCode extends LLVMCode
{
   private LLVMType guardType;
   private LLVMBlockType thenLLVMBlock;
   private LLVMBlockType elseLLVMBlock;
   private boolean conditonal;

   public LLVMBranchCode(LLVMType guardType, LLVMBlockType thenLLVMBlock, 
                        LLVMBlockType elseLLVMBlock)
   {
      super();
      this.guardType = guardType;
      this.thenLLVMBlock = thenLLVMBlock;
      this.elseLLVMBlock = elseLLVMBlock;
      this.conditonal = true;
   }

   public LLVMBranchCode(LLVMBlockType thenLLVMBlock)
   {
      this.thenLLVMBlock = thenLLVMBlock;
      this.conditonal = false;
   }

   public String toString()
   {
      if (conditonal) {
         if (guardType instanceof LLVMRegisterType) {
            String guardTypeRep = ((LLVMRegisterType)guardType).getTypeRep();
            if (!guardTypeRep.equals("i1")) {
               guardType = typeConverter(guardTypeRep, "i1", guardType);
            }

            return getConversions() + "br i1 " + guardType + ", label %" + thenLLVMBlock.getBlockId()
                    + ", label %" + elseLLVMBlock.getBlockId() + "\n";
         } else {
            if (((LLVMPrimitiveType)guardType).getValueRep().equals("0")) {
               return "br label %" + elseLLVMBlock.getBlockId() + "\n";
            } else {
               return "br label %" + thenLLVMBlock.getBlockId() + "\n";
            }
         }
      } else {
         return "br label %" + thenLLVMBlock.getBlockId() + "\n";
      }
   }

   public boolean isRedirectInstruction()
   {
      return true;
   }

   public void replaceRegister(LLVMType oldVal, LLVMType newVal)
   {
      if (guardType.equals(oldVal)) {
         this.guardType = newVal;
      }
   }
}