package llvm;
import java.util.ArrayList;
public class LLVMRegisterType implements  LLVMType
{
   private String id;
   private String typeRep;
   private LLVMCode def;
   private boolean dependenciesMarked;
   private ArrayList<LLVMCode> uses;

   public LLVMRegisterType(String typeRep, String id)
   {
      this.id = id;
      this.typeRep = typeRep;
      this.uses = new ArrayList<LLVMCode>();
      this.dependenciesMarked = false;
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

   public String toString()
   {
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

   public boolean equals(Object other)
   {
      if ((other == null) || (getClass() != other.getClass())) {
         return false;
      } else {
         return id.equals(((LLVMRegisterType)other).getId());
      }
   }
}
