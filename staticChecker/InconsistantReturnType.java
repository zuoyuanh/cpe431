import java.util.List;
import java.util.ArrayList;

public class InconsistantReturnType implements ast.Type 
{
   private List<Type> types = new ArrayList<Type>;

   public ReturnType(Type firstReturnType, Type secondReturnType)
   {
      this.firstReturnType = firstReturnType;
      this.secondReturnType = secondReturnType;
   }

   public Type getFirstReturnType()
   {
      return firstReturnType;
   }

   public int getSecondReturnType()
   {
      return secondReturnType;
   }
}