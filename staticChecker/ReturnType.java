public class ReturnType implements ast.Type 
{
   private int lineNum;
   private Type returnType;

   public ReturnType(int lineNum, Type returnType)
   {
      this.lineNum = lineNum;
      this.returnType = returnType;
   }

   public int getLineNum()
   {
      return lineNum;
   }

   public int getReturnType()
   {
      return returnType;
   }
}