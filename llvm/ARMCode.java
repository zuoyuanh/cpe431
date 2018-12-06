package llvm;

import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;

import java.util.Set;
import java.util.HashSet;

public class ARMCode
{
   // real ARM registers
   public static ARMRegister r0 = new ARMRegister("r0"); 
   public static ARMRegister r1 = new ARMRegister("r1"); 
   public static ARMRegister r2 = new ARMRegister("r2"); 
   public static ARMRegister r3 = new ARMRegister("r3");
   public static ARMRegister r4 = new ARMRegister("r4"); 
   public static ARMRegister r5 = new ARMRegister("r5"); 
   public static ARMRegister r6 = new ARMRegister("r6"); 
   public static ARMRegister r7 = new ARMRegister("r7"); 
   public static ARMRegister r8 = new ARMRegister("r8"); 
   public static ARMRegister r9 = new ARMRegister("r9"); 
   public static ARMRegister r10 = new ARMRegister("r10"); 

   public static ARMRegister pc = new ARMRegister("pc"); 
   public static ARMRegister fp = new ARMRegister("fp");
   public static ARMRegister lr = new ARMRegister("lr");
   public static ARMRegister sp = new ARMRegister("sp");

   public static LLVMRegisterType[] argRegs = {r0, r1, r2, r3};
   public static ArrayList<ARMRegister> systemRegs = new ArrayList<ARMRegister>(Arrays.asList(new ARMRegister[]{pc, fp, lr, sp}));
   public static ArrayList<ARMRegister> availableRegs = new ArrayList<ARMRegister>(Arrays.asList(new ARMRegister[]{r0, r1, r2, r3, r4, r5, r6, r7, r8}));
   public static ArrayList<ARMRegister> spillRegs = new ArrayList<ARMRegister>(Arrays.asList(new ARMRegister[]{r9, r10}));

   public static Set<ARMRegister> systemRegsSet = new HashSet<ARMRegister>(systemRegs);

   // User defined convenience registers (virtual)
   public static LLVMRegisterType ut = new LLVMRegisterType("i32", "ut");

   private LLVMRegisterType def;
   private List<LLVMRegisterType> uses;

   public void setDef(LLVMRegisterType r)
   {
      if (!systemRegs.contains(r)){
         this.def = r;
      }
   }

   public LLVMRegisterType getDef()
   {
      return this.def;
   }

   public void setUses(List<LLVMRegisterType> l)
   {
      this.uses = l;
   }

   public List<LLVMRegisterType> getUses()
   {
      return this.uses;
   }
   
   public void addUse(LLVMRegisterType u){
      if (uses == null) {
         uses = new ArrayList<LLVMRegisterType>();
      }
      if (!systemRegs.contains(u)){
         uses.add(u);
      }
   }
}
