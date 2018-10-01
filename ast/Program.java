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

   public Type visit (TypeVisitor visitor){
      Table<Table<Type>> typesTable = SymbolTableBuilder.buildTypeDeclarationTable(types);
      Table<Type> declsTable = SymbolTableBuilder.buildDeclarationsTable(decls, null, typesTable);
      return visitor.visit(this);
   }
}
