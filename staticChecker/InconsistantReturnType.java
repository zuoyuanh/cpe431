package staticChecker;

import ast.Type;
import java.util.List;
import java.util.ArrayList;

public class InconsistantReturnType implements ast.Type 
{
   private List<Type> types;

   public InconsistantReturnType()
   {
      types = new ArrayList<Type>();
   }

   public InconsistantReturnType(List<Type> types)
   {
      this.types = types;
   }

   public List<Type> getTypes()
   {
      return types;
   }

   public void add(Type t)
   {
      types.add(t);
   }

   public void remove(int index)
   {
      types.remove(index);
   }

   public int size()
   {
      return types.size();
   }
}