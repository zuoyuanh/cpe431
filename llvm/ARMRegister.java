package llvm;

public class ARMRegister extends LLVMRegisterType
{
   public ARMRegister(String id)
   {
      super("i32", id);
   }

   public String toString()
   {
      return id;
   }
}