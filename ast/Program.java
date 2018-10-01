package ast;

import java.util.List;
import staticChecker.*;

public class Program
{
   private final List<TypeDeclaration> types;
   private final List<Declaration> decls;
   private final List<Function> funcs;

   public Program(List<TypeDeclaration> types, List<Declaration> decls,
      List<Function> funcs)
   {
      this.types = types;
      this.decls = decls;
      this.funcs = funcs;
   }

   public List<TypeDeclaration> getTypes()
   {
      return types;
   }

   public List<Declaration> getDecls()
   {
      return decls;
   }

   public List<Function> getFuncs()
   {
      return funcs;
   }

   public Type visit (TypeVisitor visitor){
      return visitor.visit(this);
   }
}
