package staticChecker;

import ast.*;

public class TypeVisitor implements AstVisitor<Type>{
   public Type visit (Program program){
      Table<Table<Type>> typesTable = SymbolTableBuilder.buildTypeDeclarationTable(program.getTypes());
      Table<Type> declsTable = SymbolTableBuilder.buildDeclarationsTable(program.getDecls(), null, typesTable);
      Table<FunctionType> funcsTable = SymbolTableBuilder.buildFunctionsTable(program.getFuncs());
      System.out.println("visiting program");
      return new VoidType(); 
   }

}
