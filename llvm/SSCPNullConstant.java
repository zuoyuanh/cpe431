
package llvm;

public class SSCPNullConstant extends SSCPConstant
{
   public boolean equals(Object other)
   {
      if ((other == null) || (getClass() != other.getClass())) {
         return false;
      }
      return true;
   }
}
