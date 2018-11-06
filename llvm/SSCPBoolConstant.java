
package llvm;

public class SSCPBoolConstant extends SSCPConstant{
   private boolean value;
   public SSCPBoolConstant(boolean i){
      value = i;
   }
   public boolean getValue(){
      return value;
   }
   public void setValue(boolean i){
      value = i;
   }

   public boolean equals(Object other)
   {
      if ((other == null) || (getClass() != other.getClass())) {
         return false;
      } else {
         return (((SSCPBoolConstant)other).getValue() == value);
      }
   }
}
