
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
}
