package llvm;

public class LLVMStructFieldEntry
{
   private String positionRep;
   private String typeRep;

   public LLVMStructFieldEntry(String positionRep, String typeRep)
   {
      this.positionRep = positionRep;
      this.typeRep = typeRep;
   }

   public String getPositionRep()
   {
      return positionRep;
   }

   public String getTypeRep()
   {
      return typeRep;
   }
}