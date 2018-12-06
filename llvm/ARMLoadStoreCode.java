package llvm;

public class ARMLoadStoreCode extends ARMCode
{
   
   private Operator operator;
   private LLVMRegisterType reg;
   private LLVMRegisterType address;

   public ARMLoadStoreCode(LLVMRegisterType reg, LLVMRegisterType address,  Operator operator)
   {
      super();
      this.address = address;
      this.reg = reg;
      this.operator = operator;
      if (operator==Operator.LDR){
         addUse(address);
         setDef(reg);
      }
      else{
         addUse(reg);
         addUse(address); 
      }
   }

   public static enum Operator
   {
      LDR, STR
   }

   public String operatorToString(Operator op)
   {
      switch (op){
         case LDR:
            return "ldr";
         case STR:
            return "str";
         default:
            return "";
      }
   }

   public String toString()
   {
      return operatorToString(operator) + " " + reg +", [" + address + "]\n";
   }
}
