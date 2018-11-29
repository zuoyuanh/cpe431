package llvm;
import java.util.ArrayList;
import java.util.List;
public class LLVMReadCode extends LLVMCode
{
   private LLVMType opnd;

   public LLVMReadCode(LLVMType opnd)
   {
      super();
      this.opnd = opnd;
   }

   public String toString()
   {
      return "call i32 (i8*, ...)* @scanf(i8* getelementptr inbounds ([4 x i8]* @.read, i32 0, i32 0), i32* @.read_scratch)\n\t" + opnd + " = load i32* @.read_scratch\n";
   }

   public LLVMType getDef()
   {
      return opnd;
   }
   public List<ARMCode> generateArmCode()
   {
      this.armCode = new ArrayList<ARMCode>();
      armCode.add(new ARMMoveCode(ARMCode.r1, new LLVMPrimitiveType("i32", ":lower16:.read_scratch"), ARMMoveCode.Operator.MOVW));
      armCode.add(new ARMMoveCode(ARMCode.r1, new LLVMPrimitiveType("i32", ":upper16:.read_scratch"), ARMMoveCode.Operator.MOVT));
      armCode.add(new ARMMoveCode(ARMCode.r0, new LLVMPrimitiveType("i32", ":lower16:.READ_FMT"), ARMMoveCode.Operator.MOVW));
      armCode.add(new ARMMoveCode(ARMCode.r0, new LLVMPrimitiveType("i32", ":upper16:.READ_FMT"), ARMMoveCode.Operator.MOVT));
      armCode.add(new ARMBranchCode("scanf", ARMBranchCode.Operator.BL));
      LLVMRegisterType opndReg = getReg(opnd);
      armCode.add(new ARMMoveCode(opndReg, new LLVMPrimitiveType("i32", ":lower16:.read_scratch"), ARMMoveCode.Operator.MOVW));
      armCode.add(new ARMMoveCode(opndReg, new LLVMPrimitiveType("i32", ":upper16:.read_scratch"), ARMMoveCode.Operator.MOVT));
      armCode.add(new ARMLoadStoreCode(opndReg, opndReg, ARMLoadStoreCode.Operator.LDR));
      return armCode;
   }
}
