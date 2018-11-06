package llvm;

import java.util.ArrayList;

public class LLVMReturnConversionCode extends LLVMCode
{
   private LLVMType value;
   private LLVMType resultReg;
   private String returnType;

   public LLVMReturnConversionCode(LLVMType value, String returnType)
   {
      this.value = value;
      this.returnType = returnType;
      this.resultReg = getOperandType(value, returnType);
   }

   private LLVMType getOperandType(LLVMType t, String expectedType)
   {
      if (t instanceof LLVMRegisterType) {
         String tTypeRep = ((LLVMRegisterType)t).getTypeRep();
         String regId = ((LLVMRegisterType)t).getId();
         if (!expectedType.equals("any") && !tTypeRep.equals(expectedType)) {
            return typeConverter(tTypeRep, expectedType, t);
         }
         return new LLVMRegisterType(expectedType, regId);
      } else if (t instanceof LLVMPrimitiveType) {
         String tTypeRep = ((LLVMPrimitiveType)t).getTypeRep();
         String valueRep = ((LLVMPrimitiveType)t).getValueRep();
         if (!tTypeRep.equals(expectedType) && !tTypeRep.equals("null") && !expectedType.equals("any")) {
            return typeConverter(tTypeRep, expectedType, t);
         }
         return new LLVMPrimitiveType(tTypeRep, valueRep);
      }
      return new LLVMVoidType();
   }

   public void replaceRegister(LLVMType oldVal, LLVMType newVal)
   {
      // replace register
      typeConversions = new ArrayList<String>();
      getOperandType(newVal, returnType);
   }

   public LLVMType getConvertedResultReg()
   {
      return this.resultReg;
   }

   public List<LLVMRegisterType> dependenciesList()
   {
      List<LLVMRegisterType> result = new ArrayList<LLVMRegisterType>();
      if (value instanceof LLVMRegisterType) {
         result.add((LLVMRegisterType)value);
      }
      return result;
   }

   public LLVMType def()
   {
      return this.resultReg;
   }
}