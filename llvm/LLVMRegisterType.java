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
}