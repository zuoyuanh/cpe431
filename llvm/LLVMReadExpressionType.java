package llvm;

public class LLVMReadExpressionType implements LLVMType
{
   public LLVMCode getSSAReadInstruction(LLVMType opnd)
   {
      return new LLVMReadCode(opnd);
   }

   public String getTypeRep()
   {
      return "";
   }
}
