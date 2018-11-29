package llvm;
import java.util.ArrayList;
import java.util.List;
public class LLVMNewCode extends LLVMCode
{
   private int size;
   private String structTypeRep;
   private LLVMType intermediatorReg;
   private LLVMType resultReg;

   public LLVMNewCode(int size, String structTypeRep)
   {
      super();
      this.size = size;
      this.structTypeRep = structTypeRep;
      this.intermediatorReg = SSAVisitor.createNewRegister("i8*");
      this.resultReg = typeConverter("i8*", structTypeRep + "*", intermediatorReg);
   }

   public LLVMType getConvertedResultReg()
   {
      return this.resultReg;
   }

   public String toString()
   {
      return intermediatorReg + " = call i8* @malloc(i32 " + size + ")\n" + getConversions();
   }

   public LLVMType getDef()
   {
      return resultReg;
   }

   public List<ARMCode> generateArmCode()
   {
      armCode.add(new ARMMoveCode(ARMCode.r0, new LLVMPrimitiveType("i32", ""+size), ARMMoveCode.Operator.MOV));
      armCode.add(new ARMBranchCode("malloc", ARMBranchCode.Operator.BL));
      LLVMRegisterType iReg = getReg(intermediatorReg);
      armCode.add(new ARMMoveCode(iReg, ARMCode.r0, ARMMoveCode.Operator.MOV));
      mergeARMConversions();
      return armCode;
   }
}
