import java.util.HashMap;
import exceptions.IdentifierNotFoundException;
import exceptions.DuplicatedIdentifierDeclarationException;

public class Table<T>
{
   private final Table<T> prev;
   private final String typeName;
   private HashMap<String, T> map = new HashMap<String, T>();

   public Table(Table<T> prev, String typeName)
   {
      this.prev = prev;
      this.typeName = typeName;
   }

   public void insert(String name, T value) throws DuplicatedIdentifierDeclarationException
   {
      if (map.containsKey(name)) {
         throw new DuplicatedIdentifierDeclarationException(name, typeName);
      }
      map.put(name, value);
   }

   public void overwrite(String name, T value)
   {
      map.put(name, value);
   }

   public T get(String name) throws IdentifierNotFoundException
   {
      if (map.containsKey(name)) {
         return map.get(name);
      }
      if (prev == null) {
         throw new IdentifierNotFoundException(name, typeName);
      }
      return prev.get(name);
   }

   public boolean containsKey(String name) {
      if (map.containsKey(name)) {
         return true;
      }
      if (prev == null) {
         return false;
      }
      return prev.containsKey(name);
   }
}