
package llvm;

public class SSCPBottom implements SSCPValue{
   public boolean equals(Object other)
   {
      if ((other == null) || (getClass() != other.getClass())) {
         return false;
      }
      return true;
   }
}
