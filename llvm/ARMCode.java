package llvm;

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
}
