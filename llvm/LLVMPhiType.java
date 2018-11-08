package llvm;
import java.util.ArrayList;
public class LLVMPhiType implements LLVMType {
   private ArrayList<LLVMPhiEntryType> phiOperands;
   private LLVMBlockType block;
   private LLVMRegisterType register;

   public LLVMPhiType(LLVMBlockType b)
   {
      block = b;
      phiOperands =  new ArrayList<LLVMPhiEntryType>();
   }

   public ArrayList<LLVMPhiEntryType> getPhiOperands()
   {
      return this.phiOperands;
   }

   public void addPhiOperand(LLVMPhiEntryType op)
   {
      phiOperands.add(op);
   }

   public LLVMBlockType getBlock()
   {
      return this.block;
   }

   public LLVMRegisterType getRegister()
   {
      return this.register;
   }

   public void setRegister(LLVMRegisterType r)
   {
      this.register = r;
   }

   public void setRegisterType(String typeRep)
   {
      this.register.setTypeRep(typeRep);
   }

   public String getTypeRep()
   {
      return "";
   }
}
