package staticChecker;

import ast.Type;
import ast.Declaration;
import java.util.List;

public class StructTableType implements Type
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

}