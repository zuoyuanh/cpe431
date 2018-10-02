package staticChecker;

import ast.Type;

public class ReturnType implements Type 
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

   public Type getReturnType()
   {
      return returnType;
   }
}