package llvm;

public class LLVMRegisterType implements  LLVMType
{
   private String id;
   private String typeRep;

   public LLVMRegisterType(String typeRep, String id)
   {
      this.id = id;
      this.typeRep = typeRep;
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