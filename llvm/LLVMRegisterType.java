package llvm;
import java.util.ArrayList;
public class LLVMRegisterType implements LLVMType
{
   protected String id;
   private String typeRep;
   private LLVMCode def;
   private boolean dependenciesMarked;
   private ArrayList<LLVMCode> uses;
   private ARMRegister allocatedARMRegister;

   public LLVMRegisterType(String typeRep, String id)
   {
      this.id = id;
      this.typeRep = typeRep;
      this.uses = new ArrayList<LLVMCode>();
      this.dependenciesMarked = false;
      this.allocatedARMRegister = null;
      if (this instanceof ARMRegister){
         this.allocatedARMRegister = (ARMRegister)this;
      }
   }

   public String getId()
   {
      return id;
   }

   public String getTypeRep()
   {
      return typeRep;
   }

   public void setTypeRep(String typeRep)
   {
      this.typeRep = typeRep;
   }

   public void setDef(LLVMCode c)
   {
      this.def = c;
   }

   public LLVMCode getDef()
   {
      return this.def;
   }

   public ArrayList<LLVMCode> getUses()
   {
      return this.uses;
   }

   public void addUse(LLVMCode c)
   {
      if (uses.contains(c)) {
         return;
      }
      this.uses.add(c);
   }

   public boolean getDependenciesMarked()
   {
      return this.dependenciesMarked;
   }

   public void setDependenciesMarked(boolean marked)
   {
      this.dependenciesMarked = marked;
   }

   public void allocateARMRegister(ARMRegister register)
   {
      this.allocatedARMRegister = register;
   }

   public ARMRegister getAllocatedARMRegister()
   {
      return this.allocatedARMRegister;
   }

   @Override
   public String toString()
   {
      if (SSAVisitor.generateARM) {
         if (allocatedARMRegister != null) {
            return allocatedARMRegister.toString();
         } else {
            return "SPILL(" + id + ")";
         }
      }
      if (id.charAt(0) != '@' && id.charAt(0) != '%') {
         return "%" + id;
      }
      if (id.charAt(0) == '%') {
         while (id.charAt(0) == '%') {
            id = id.substring(1);
         }
         return "%" + id;
      }
      return id;
   }

   @Override
   public boolean equals(Object other)
   {
      if ((other == null) || (getClass() != other.getClass())) {
         return false;
      } else {
         return id.equals(((LLVMRegisterType)other).getId());
      }
   }

   @Override
   public int hashCode() {
      int hash = 7;
      hash = 31 * hash + id.hashCode();
      return hash;
   }
}
