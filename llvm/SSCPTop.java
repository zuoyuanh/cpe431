
package llvm;

public class SSCPTop implements SSCPValue{
   public boolean equals(Object other)
   {
      if ((other == null) || (getClass() != other.getClass())) {
         return false;
      }
      return true;
   }
}
