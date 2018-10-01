import java.util.List;
import java.util.HashMap;

public class SymbolicTable<T>
{
   private final Table ROOT_TABLE = null;
   private HashMap<String, T> map = new HashMap<String, T>();

   public void insert(String name, T value)
   {
      map.put(name, value);
   }

   public T get(String name)
   {
      return map.get(name);
   }
}