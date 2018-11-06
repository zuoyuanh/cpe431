package llvm;

import java.util.List;

public class LLVMPhiCode extends LLVMCode
{
   private List<LLVMPhiEntryType> entries;
   private LLVMRegisterType phiRegister;

   public LLVMPhiCode(LLVMRegisterType r, List<LLVMPhiEntryType> entries)
   {
      phiRegister = r;
      this.entries = entries;
   }
   public String toString(){
      String phiOpnds = "";
      for (LLVMPhiEntryType ty : entries) 
      {
         String blockId = ty.getBlock().getBlockId();
         LLVMType t = ty.getOperand();
         if (t instanceof LLVMRegisterType) {
            phiOpnds += "[" + ((LLVMRegisterType)t) + ", %" + blockId + "], ";
         } else if (t instanceof LLVMPrimitiveType) {
            phiOpnds += "[" + ((LLVMPrimitiveType)t).getValueRep() + ", %" + blockId + "], ";
         }
      }
      if (phiOpnds.length() > 2 && phiOpnds.charAt(phiOpnds.length()-2) == ',') {
         phiOpnds = phiOpnds.substring(0, phiOpnds.length()-2);
      }
      return ("%" + phiRegister.getId() + " = phi " + phiRegister.getTypeRep() + " " + phiOpnds + "\n");
         
   }
}
