package llvm;

public class LLVMDeclType implements LLVMType
{
   private String name;
   private String typeRep;

   public LLVMDeclType(String name, String typeRep)
   {
      this.name = name;
      this.typeRep = typeRep;
   }

   public String getName()
   {
      return name;
   }

   public String getTypeRep()
   {
      return typeRep;
   }
   
}