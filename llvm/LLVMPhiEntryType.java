package llvm;

public class LLVMPhiEntryType implements LLVMType {
   private LLVMType operand;
   private LLVMBlockType block;

   public LLVMPhiEntryType(LLVMType op, LLVMBlockType b)
   {
      this.operand = op;
      this.block = b;
   }

   public void setOperand(LLVMType op)
   {
      this.operand = op;
   }

   public LLVMType getOperand()
   {
      return this.operand;
   }

   public void setBlock(LLVMBlockType b)
   {
      this.block = b;
   }

   public LLVMBlockType getBlock()
   {
      return this.block;
   }

   public String getTypeRep() 
   {
      return "";
   }

   public boolean equals(Object o)
   {
      if (o == this) { 
         return true; 
      }
      if (!(o instanceof LLVMPhiEntryType)) { 
         return false; 
      }
      LLVMPhiEntryType p = (LLVMPhiEntryType) o;
      return p.getBlock().equals(block) && p.getOperand().equals(operand);
   }
}
