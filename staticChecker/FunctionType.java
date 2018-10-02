package staticChecker;

import ast.Type;
import ast.Declaration;
import java.util.List;

public class FunctionType
{
   private final int lineNum;
   private final String name;
   private final Type retType;
   private final List<Declaration> params;

   public FunctionType(int lineNum, String name, List<Declaration> params, Type retType)
   {
      this.lineNum = lineNum;
      this.name = name;
      this.params = params;
      this.retType = retType;
   }

   public List<Declaration> getParams(){
      return this.params;
   }
   public Type getRetType(){
      return this.retType;
   }
   public String getName(){
      return this.name;
   }

}
