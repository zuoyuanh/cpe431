package llvm;

import java.util.List;
import java.util.ArrayList;
import ast.Type;
import ast.IntType;
import ast.BoolType;
import ast.StructType;
import ast.VoidType;

public class LLVMCode
{
   private boolean marked;
   protected List<String> typeConversions;

   public LLVMCode()
   {
      this.marked = false;
      this.typeConversions = new ArrayList<String>();
   }

   protected LLVMType getOperand(LLVMType t, String expectedType)
   {
      if (t instanceof LLVMRegisterType) {
         String tTypeRep = ((LLVMRegisterType)t).getTypeRep();
         if (!expectedType.equals("any") && !tTypeRep.equals(expectedType)) {
            return typeConverter(tTypeRep, expectedType, t);
         }
         return t;
      } else if (t instanceof LLVMPrimitiveType) {
         String tTypeRep = ((LLVMPrimitiveType)t).getTypeRep();
         String valueRep = ((LLVMPrimitiveType)t).getValueRep();
         if (!tTypeRep.equals(expectedType) && !tTypeRep.equals("null") && !expectedType.equals("any")) {
            return typeConverter(tTypeRep, expectedType, t);
         }
         return new LLVMPrimitiveType(expectedType, valueRep);
      }
      return new LLVMVoidType();
   }

   protected LLVMType typeConverter(String originalType, String expectedType, LLVMType opndType)
   {
      String opnd = opndType.toString();
      if (originalType.equals("i32") && expectedType.equals("i1")) {
         String tmpRegId = "u" + Integer.toString((SSAVisitor.registerCounter)++);
         typeConversions.add("%" + tmpRegId + " = trunc " + originalType 
                            + " " + opnd + " to " + expectedType + "\n");
         return new LLVMRegisterType(expectedType, tmpRegId);
      } else if (originalType.equals("i1") && expectedType.equals("i32")) {
         String tmpRegId = "u" + Integer.toString((SSAVisitor.registerCounter)++);
         typeConversions.add("%" + tmpRegId + " = zext " + originalType 
                            + " " + opnd + " to " + expectedType + "\n");
         return new LLVMRegisterType(expectedType, tmpRegId);
      } else {
         String tmpRegId = "u" + Integer.toString((SSAVisitor.registerCounter)++);
         typeConversions.add("%" + tmpRegId + " = bitcast " + originalType 
                            + " " + opnd + " to " + expectedType + "\n");
         return new LLVMRegisterType(expectedType, tmpRegId);
      }
   }

   protected String getConversions()
   {
      String result = "";
      for (String s : typeConversions) {
         result += "\t" + s;
      }
      return result;
   }

   protected String getTypeLLVMRepresentation(Type t)
   {
      if ((t instanceof IntType) || (t instanceof BoolType)) {
         return "i32";
      } else if (t instanceof VoidType) {
         return "void";
      } else if (t instanceof StructType) {
         StructType st = (StructType)t;
         String structName = st.getName();
         return "%struct." + structName + "*";
      }
      return "unknown";
   }

   public boolean isRedirectInstruction()
   {
      return false;
   }

   public void replaceRegister(LLVMType oldVal, LLVMType newVal)
   {
   }

   public List<LLVMRegisterType> dependenciesList()
   {
      return new ArrayList<LLVMRegisterType>();
   }

   public LLVMType def()
   {
      return null;
   }

   public void mark()
   {
      this.marked = true;
   }

   public boolean isMarked()
   {
      return this.marked;
   }

   public String toString()
   {
      return getConversions();
   }
}