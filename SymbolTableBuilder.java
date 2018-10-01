import java.util.List;
import ast.Function;
import ast.TypeDeclaration;
import ast.Declaration;
import ast.Program;
import exceptions.DuplicatedIdentifierDeclarationException;

public class SymbolTableBuilder
{
   public static Table<Function> buildFunctionsTable(List<Function> funcs)
   {
      Table<Function> funcsTable = new Table<Function>(null, "functions");
      for (Function f : funcs) {
         try {
            funcsTable.insert(f.getName(), f);
         } catch (DuplicatedIdentifierDeclarationException e) {
            System.out.println(e.getErrorMessage());
         }
      }
      return funcsTable;
   }

   public static Table<Declaration> buildDeclarationsTable(
      List<Declaration> decls, 
      Table<Declaration> prev,
      Table<TypeDeclaration> types)
   {
      Table<Declaration> declsTable = new Table<Declaration>(prev, "identifiers");
      for (Declaration d : decls) {
         if (d.type instanceof StructType) {
            StructType t = (StructType)d.type;
            if (types.contains(t)) {
               
            }
         }
         try {
            declsTable.insert(d.getName(), d);
         } catch (DuplicatedIdentifierDeclarationException e) {
            System.out.println(e.getErrorMessage());
         }
      }
      return declsTable;
   }

   public static Table<TypeDeclaration> buildTypeDeclarationTable(
      List<TypeDeclaration> types
   )
   {
      Table<TypeDeclaration> typesTable = new Table<TypeDeclaration>(null, "type");
      for (TypeDeclaration t : types) {
         try {
            typesTable.insert(t.getName(), t);
         } catch (DuplicatedIdentifierDeclarationException e) {
            System.out.println(e.getErrorMessage());
         }
      }
      return typesTable;
   }

   public static SymbolTable buildSymbolTable(Program p) {
      Table<Function> funcsTable = buildFunctionsTable(p.getFuncs());
      Table<Declaration> declsTable = buildDeclarationsTable(p.getDecls(), null);
      Table<TypeDeclaration> typesTable = buildTypeDeclarationTable(p.getTypes());
      System.out.println("building symbolic table...");
      return new SymbolTable(funcsTable, declsTable, typesTable);
   }
}