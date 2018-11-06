
package llvm;

public class SSCPIntConstant extends SSCPConstant{
   private int value;
   public SSCPIntConstant(int i){
      value = i;
   }
   public int getValue(){
      return value;
   }
   public void setValue(int i){
      value = i;
   }
   public boolean equals(Object other)
   {
      if ((other == null) || (getClass() != other.getClass())) {
         return false;
      } else {
         return (((SSCPIntConstant)other).getValue() == value);
      }
   }
}
