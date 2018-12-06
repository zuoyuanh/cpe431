package llvm;
import java.util.ArrayList;
import java.util.List;
public class ARMCode
{
   // System specified registers
   public static LLVMRegisterType r0 = new LLVMRegisterType("i32", "r0"); 
   public static LLVMRegisterType r1 = new LLVMRegisterType("i32", "r1"); 
   public static LLVMRegisterType r2 = new LLVMRegisterType("i32", "r2"); 
   public static LLVMRegisterType r3 = new LLVMRegisterType("i32", "r3"); 

   public static LLVMRegisterType pc = new LLVMRegisterType("i32", "pc"); 
   public static LLVMRegisterType fp = new LLVMRegisterType("i32", "fp");
   public static LLVMRegisterType lr = new LLVMRegisterType("i32", "lr");
   public static LLVMRegisterType sp = new LLVMRegisterType("i32", "sp");

   public static LLVMRegisterType[] argRegs = {r0, r1, r2, r3};

   // User defined convenience registers
   public static LLVMRegisterType ut = new LLVMRegisterType("i32", "ut");

   private LLVMRegisterType def;
   private List<LLVMRegisterType> uses;

   public void setDef (LLVMRegisterType r){
      this.def = r;
   }
   public LLVMRegisterType getDef(){
      return this.def;
   }
   public void setUses(List<LLVMRegisterType> l){
      this.uses = l;
   }
   public List<LLVMRegisterType> getUses(){
      return this.uses;
   }
   public void addUse(LLVMRegisterType u){
      if (uses == null) {
         uses = new ArrayList<LLVMRegisterType>();
      }
      uses.add(u);
   }
}
