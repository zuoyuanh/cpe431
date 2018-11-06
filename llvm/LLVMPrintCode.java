package llvm;

import java.util.List;
import java.util.ArrayList;

public class LLVMPrintCode extends LLVMCode
{
   private boolean newLine;
   private LLVMType opnd;

   public LLVMPrintCode(LLVMType opnd, boolean newLine)
   {
      super();
      this.opnd = opnd;
      this.newLine = newLine;
   }

   public String toString()
   {
      if (newLine) {
         return "call i32 (i8*, ...)* @printf(i8* getelementptr inbounds ([5 x i8]* @.println, i32 0, i32 0), "
               + opnd.getTypeRep() + " " + opnd + ")\n";
      } else {
         return "call i32 (i8*, ...)* @printf(i8* getelementptr inbounds ([5 x i8]* @.print, i32 0, i32 0), "
               + opnd.getTypeRep() + " " + opnd + ")\n";
      }
   }
   
   public void replaceRegister(LLVMType oldVal, LLVMType newVal)
   {
      if (opnd.equals(oldVal)) {
         opnd = newVal;
      }
   }

   public List<LLVMRegisterType> dependenciesList()
   {
      List<LLVMRegisterType> result = new ArrayList<LLVMRegisterType>();
      if (opnd instanceof LLVMRegisterType) {
         result.add((LLVMRegisterType)opnd);

      }
      return result;
   }
}