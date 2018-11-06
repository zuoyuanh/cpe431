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
      return valueRep;
   }

   public String toString()
   {
      return valueRep;
   }
}
