package llvm;

public class ARMLoadStoreCode extends ARMCode
{
   
   private Operator operator;
   private LLVMRegisterType reg;
   private LLVMRegisterType address;
   private String offset;

   public ARMLoadStoreCode(LLVMRegisterType reg, LLVMRegisterType address, Operator operator)
   {
      super();
      this.address = address;
      this.reg = reg;
      this.operator = operator;
      this.offset = null;
      if (operator == Operator.LDR) {
         addUse(address);
         setDef(reg);
      } else {
         addUse(reg);
         addUse(address); 
      }
   }

   public ARMLoadStoreCode(LLVMRegisterType reg, LLVMRegisterType address, Operator operator, String offset)
   {
      super();
      this.address = address;
      this.reg = reg;
      this.operator = operator;
      this.offset = offset;
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
      String spill = "";
      String regString = "";
      String addressRep = "";
      if (!(reg instanceof ARMRegister) && reg.getAllocatedARMRegister() == null) {
         if (operator == Operator.LDR) {
            spill = storeSpill(res, ARMCode.r9, reg);
         } else {
            res = loadSpill(spill, ARMCode.r9, reg);
         }
         regString = ARMCode.r9.toString();
      } else {
         regString = reg.toString();
      }
      String varName = address.getId().substring(1);
      if (Compiler.getGlobalVariablesMap().containsKey(varName)) {
         res += "movw ip, #:lower16:" + varName + "\n\t";
         res += "movt ip, #:upper16:" + varName + "\n\t";
         addressRep = "ip";
      } else {
         if (address instanceof ARMRegister || address.getAllocatedARMRegister() != null) {
            addressRep = address.toString();
            if (offset != null) {
               addressRep += ", #" + offset;
            }
         } else {
            int offset = Compiler.getLocalVariableOffset(address.getId());
            if (offset == -1 && operator == Operator.STR) {
               Compiler.putLocalVariable(address.getId());
               offset = Compiler.getLocalVariableOffset(address.getId());
            }
            addressRep = "sp, #" + offset + "";
         }
      }
      res += operatorToString(operator) + " " + regString + ", [" + addressRep + "]\n" + spill;
      return res;
   }
}
