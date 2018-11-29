package llvm;

public class ARMCode
{
   public static LLVMRegisterType r0 = new LLVMRegisterType("r0", "i32"); 
   public static LLVMRegisterType r1 = new LLVMRegisterType("r1", "i32"); 
   public static LLVMRegisterType r2 = new LLVMRegisterType("r2", "i32"); 
   public static LLVMRegisterType r3 = new LLVMRegisterType("r3", "i32"); 

   public static LLVMRegisterType pc = new LLVMRegisterType("pc", "i32"); 
   public static LLVMRegisterType fp = new LLVMRegisterType("fp", "i32"); 
}
