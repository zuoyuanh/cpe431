package llvm;

import java.util.List;
import java.util.ArrayList;

public class LLVMPhiCode extends LLVMCode
{
   private List<LLVMPhiEntryType> entries;
   private LLVMRegisterType phiRegister;

   public LLVMPhiCode(LLVMRegisterType r, List<LLVMPhiEntryType> entries)
   {
      phiRegister = r;
      this.entries = entries;
   }
   public String toString()
   {
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

   public List<LLVMRegisterType> dependenciesList()
   {
      List<LLVMRegisterType> results = new ArrayList<LLVMRegisterType>();
      for (LLVMPhiEntryType phi : entries) {
         LLVMType phiType = phi.getOperand();
         if (phiType instanceof LLVMRegisterType) {
            results.add((LLVMRegisterType)phiType);
         }
      }
      return results;
   }

   public List<LLVMPhiEntryType> getEntries(){
      return entries;
   }

   public void setEntries(List<LLVMPhiEntryType> e){
      this.entries = e;
   }
   public LLVMType getDef()
   {
      return phiRegister;
   }
}
