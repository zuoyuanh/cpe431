package llvm;

import ast.Type;
import ast.IntType;
import ast.VoidType;
import ast.BoolType;

public class LLVMPrimitiveType implements LLVMType
{
   private String typeRep;
   private String valueRep;

   public LLVMPrimitiveType(String typeRep, String valueRep)
   {
      this.typeRep = typeRep;
      this.valueRep = valueRep;
   }

   public String getTypeRep()
   {
      return typeRep;
   }

   public String getValueRep()
   {
      if (SSAVisitor.generateARM) {
         if (valueRep != null && valueRep.equals("null")) {
            return "0";
         }
      }
      return valueRep;
   }

   public void setTypeRep(String typeRep)
   {
      this.typeRep = typeRep;
   }

   public String toString()
   {
      if (SSAVisitor.generateARM) {
         if (valueRep != null && valueRep.equals("null")) {
            return "0";
         }
      }
      return valueRep;
   }
}
