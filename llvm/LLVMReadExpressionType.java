package llvm;

public class LLVMReadExpressionType implements LLVMType
{
   public String getReadInstructionString(String typeRep, String opnd)
   {
   	  if (opnd.charAt(0) == 'u') {
   	  	 opnd = "%" + opnd;
   	  }
      return "call i32 (i8*, ...)* @scanf(i8* getelementptr inbounds ([4 x i8]* @.read, i32 0, i32 0), " + typeRep + "* " + opnd + ")\n";
   }
}
