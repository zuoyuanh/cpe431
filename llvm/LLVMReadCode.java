package llvm;

public class LLVMReadCode extends LLVMCode
{
   private LLVMType opnd;

   public LLVMReadCode(LLVMType opnd)
   {
      super();
      this.opnd = opnd;
   }

   public String toString()
   {
      return "call i32 (i8*, ...)* @scanf(i8* getelementptr inbounds ([4 x i8]* @.read, i32 0, i32 0), i32* @.read_scratch)\n\t" + opnd + " = load i32* @.read_scratch\n";
   }

   public LLVMType def()
   {
      return opnd;
   }
}