package llvm;
import java.util.ArrayList;
public class ARMPushPopCode extends ARMCode
{
   
   private Operator operator;
   private ArrayList<LLVMRegisterType> regList;

   public ARMPushPopCode(ArrayList<LLVMRegisterType> regList,  Operator operator)
   {
      super();
      this.regList = regList;
      this.operator = operator;
   }

   public static enum Operator
   {
      PUSH, POP
   }
   public String operatorToString(Operator op){
      switch (op){
         case PUSH:
            return "push";
         case POP:
            return "pop";
         default:
            return "";
      }
   }
   public String toString(){
      if (regList.size() == 0) return "";
      String res = operatorToString(operator) + " {";
      for (LLVMRegisterType reg : regList){
         res = res + " " + reg +", ";
      }
      res = res.substring(0, res.length()-2);
      res +="}\n";
      return res;
   }
}
