package llvm;

import java.util.List;
import java.util.ArrayList;

public class LLVMPhiCode extends LLVMCode
{
   private List<LLVMPhiEntryType> entries;
   private LLVMRegisterType phiRegister;
   private LLVMRegisterType phiDefRegister;
   private List<LLVMCode> defCodes;

   public LLVMPhiCode(LLVMRegisterType r, List<LLVMPhiEntryType> entries)
   {
      phiRegister = r;
      this.entries = entries;
      this.defCodes = new ArrayList<LLVMCode>();
      this.phiDefRegister = SSAVisitor.createNewRegister("i32");
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

   public void replaceRegister(LLVMType oldVal, LLVMType newVal)
   {
      for (LLVMPhiEntryType phi : entries) {
         if (phi.getOperand().equals(oldVal)) {
            phi.setOperand(newVal);
            if (newVal instanceof LLVMRegisterType) {
               ((LLVMRegisterType)newVal).addUse(this);
            }
         }
      }
   }

   public List<LLVMPhiEntryType> getEntries()
   {
      return entries;
   }

   public void setEntries(List<LLVMPhiEntryType> e)
   {
      this.entries = e;
   }

   public LLVMType getDef()
   {
      return phiRegister;
   }

   public List<ARMCode> generateArmCode()
   {
      this.armCode.add(new ARMMoveCode(phiRegister, phiDefRegister, ARMMoveCode.Operator.MOV));
      return armCode;
   }

   public void processPhiDefs()
   {
      this.phiDefRegister.setTypeRep(this.phiRegister.getTypeRep());
      for (LLVMPhiEntryType entry : entries) {
         defCodes.add(entry.getBlock().addPhiRegisterDef(phiDefRegister, entry.getOperand()));
      }
   }

   public void remove()
   {
      this.removed = true;
      for (LLVMCode code : defCodes) {
         code.remove();
      }
   }

   public void mark()
   {
      super.mark();
      for (LLVMCode code : defCodes) {
         code.mark();
      }
   }
}
