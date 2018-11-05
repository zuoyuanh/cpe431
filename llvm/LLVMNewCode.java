package llvm;

public class LLVMNewCode extends LLVMCode
{
   private int size;
   private String structTypeRep;
   private LLVMType intermediatorReg;
   private LLVMType resultReg;

   public LLVMNewCode(int size, String structTypeRep)
   {
      super();
      this.size = size;
      this.structTypeRep = structTypeRep;
      this.intermediatorReg = SSAVisitor.createNewRegister("i8*");
      this.resultReg = typeConverter("i8*", structTypeRep + "*", intermediatorReg);
   }

   public LLVMType getConvertedResultReg()
   {
      return this.resultReg;
   }

   public String toString()
   {
      return intermediatorReg + " = call i8* @malloc(i32 " + size + ")\n" + getConversions();
   }
}