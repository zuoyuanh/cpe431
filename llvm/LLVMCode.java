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
   protected boolean removed;
   protected List<String> typeConversions;
   protected List<ARMCode> armConversions;
   private LLVMBlockType block;
   protected List<ARMCode> armCode;

   public LLVMCode()
   {
      this.marked = false;
      this.removed = false;
      this.typeConversions = new ArrayList<String>();
      this.block = null;
      this.armCode = new ArrayList<ARMCode>();
      this.armConversions = new ArrayList<ARMCode>();
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
      LLVMRegisterType res;
      if (originalType.equals("i32") && expectedType.equals("i1")) {
         String tmpRegId = "u" + Integer.toString((SSAVisitor.registerCounter)++);
         typeConversions.add("%" + tmpRegId + " = trunc " + originalType 
                            + " " + opnd + " to " + expectedType + "\n");
         res =  new LLVMRegisterType(expectedType, tmpRegId);
      } else if (originalType.equals("i1") && expectedType.equals("i32")) {
         String tmpRegId = "u" + Integer.toString((SSAVisitor.registerCounter)++);
         typeConversions.add("%" + tmpRegId + " = zext " + originalType 
                            + " " + opnd + " to " + expectedType + "\n");
         res =  new LLVMRegisterType(expectedType, tmpRegId);
      } else {
         String tmpRegId = "u" + Integer.toString((SSAVisitor.registerCounter)++);
         typeConversions.add("%" + tmpRegId + " = bitcast " + originalType 
                            + " " + opnd + " to " + expectedType + "\n");
         res =  new LLVMRegisterType(expectedType, tmpRegId);
      }
      LLVMRegisterType opndReg = getReg(opndType);
      armConversions.add(new ARMMoveCode(res, opndReg, ARMMoveCode.Operator.MOV, 1));
      return res;
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

   protected void mergeARMConversions()
   {
      for (ARMCode code : armConversions) {
         this.armCode.add(code);
      }
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

   public LLVMType getDef()
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

   public void remove()
   {
      this.removed = true;
   }

   public boolean isRemoved()
   {
      return this.removed;
   }

   public String toString()
   {
      return getConversions();
   }

   public LocalNumberingExpression getExpressionForLocalNumbering()
   {
      return null;
   }

   public void setBlock(LLVMBlockType b)
   {
      this.block = b;
   }

   public LLVMBlockType getBlock()
   {
      return this.block;
   }

   protected LLVMType getOperand(LLVMType t)
   {
      if (t instanceof LLVMRegisterType) return (LLVMRegisterType)t ;
      if (t instanceof LLVMPrimitiveType) {
         LLVMPrimitiveType p = (LLVMPrimitiveType)t;
         String v = p.getValueRep();
         if (v.equals("null"))
         {
            System.out.println("null in binary operation");
         }
         int i = 0;
         try {
            i = Integer.parseInt(v);
         } catch (Exception e) {
            System.out.println("primitive can't be cast to int");
         }
         if (i > -9999 && i < 9999) {
            return p;
         } else {
            LLVMRegisterType resReg = SSAVisitor.createNewRegister("i32");
            armCode.add(new ARMMoveCode(resReg, new LLVMPrimitiveType("i32", ":lower16:" + v), ARMMoveCode.Operator.MOVW, 2));
            armCode.add(new ARMMoveCode(resReg, new LLVMPrimitiveType("i32", ":upper16:" + v), ARMMoveCode.Operator.MOVT, 3));
            return resReg;
         }
      }
      System.out.println(t + " is not a valid type");
      return null;
   }

   protected LLVMType getBinaryOperationOperand(LLVMType t)
   {
      if (t instanceof LLVMRegisterType) return (LLVMRegisterType)t ;
      if (t instanceof LLVMPrimitiveType) {
         LLVMPrimitiveType p = (LLVMPrimitiveType)t;
         String v = p.getValueRep();
         if (v.equals("null"))
         {
            System.out.println("null in binary operation");
         }
         int i = 0;
         try {
            i = Integer.parseInt(v);
         } catch (Exception e) {
            System.out.println("primitive can't be cast to int");
         }
         if (i > -256 && i < 256) {
            return p;
         } else {
            LLVMRegisterType resReg = SSAVisitor.createNewRegister("i32");
            LLVMType opnd = getOperand(t);
            armCode.add(new ARMMoveCode(resReg, opnd, ARMMoveCode.Operator.MOV, 4));
            return resReg;
         }
      }
      System.out.println(t + " is not a valid type");
      return null;
   }

   public LLVMRegisterType getReg(LLVMType t)
   {
      if (t instanceof LLVMRegisterType) return (LLVMRegisterType)t;
      LLVMRegisterType resReg = SSAVisitor.createNewRegister("i32");
      if (t instanceof LLVMPrimitiveType) {
         LLVMPrimitiveType p = (LLVMPrimitiveType)t;
         String v = p.getValueRep();
         if (v.equals("null")) {
            System.out.println("null in binary operation");
         }
         int i = 0;
         try {
            i = Integer.parseInt(v);
         } catch (Exception e) {
            System.out.println("primitive can't be cast to int");
         }
         if (i < 65535) {
            armCode.add(new ARMMoveCode(resReg, t, ARMMoveCode.Operator.MOV, 5));
            return resReg;
         } else {
            armCode.add(new ARMMoveCode(resReg, new LLVMPrimitiveType("i32", ":lower16:" + v), ARMMoveCode.Operator.MOVW, 6));
            armCode.add(new ARMMoveCode(resReg, new LLVMPrimitiveType("i32", ":upper16:" + v), ARMMoveCode.Operator.MOVT, 7));
            return resReg;
         }
      }
      System.out.println(t + " is not a valid type");
      return null;
   }

   public List<ARMCode> generateArmCode()
   {
      mergeARMConversions();
      return this.armCode;
   }
}
