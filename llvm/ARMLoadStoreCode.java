package llvm;

public class ARMLoadStoreCode extends ARMCode
{
   
   private Operator operator;
   private LLVMRegisterType reg;
   private LLVMRegisterType address;
   private String offset;

   public ARMLoadStoreCode(LLVMRegisterType reg, LLVMRegisterType address,  Operator operator)
   {
      super();
      this.address = address;
      this.reg = reg;
      this.operator = operator;
      if (operator == Operator.LDR) {
         addUse(address);
         setDef(reg);
      } else {
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
      switch (op) {
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
      String res = "";
      String regString = "";
      String addressRep = "";
      if ((reg).getAllocatedARMRegister() == null) {
         res = loadSpill(res, ARMCode.r9, reg);
         regString = ARMCode.r9.toString();
      } else {
         regString = reg.toString();
      }
      if (address instanceof ARMRegister || address.getAllocatedARMRegister() != null) {
         addressRep = address.toString();
      } else {
         int offset = Compiler.getLocalVariableOffset(address.getId());
         if (offset != -1) {
            addressRep = "sp, #" + offset + "";
         } else {
            if (Compiler.getOriginalGlobalVariablesMap().containsKey(address.getId().substring(1))) {
               res += "ldr ip, =" + address.getId().substring(1) + "\n\t";
               addressRep = "ip";
            }
         }
      }
      res += operatorToString(operator) + " " + regString + ", [" + addressRep + "]\n";
      return res;
   }
}
