import java.util.List;
import ast.Function;
import ast.TypeDeclaration;
import ast.Declaration;
import ast.Program;
import ast.Type;
import ast.StructType;
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

   public static Table<Type> buildDeclarationsTable(
      List<Declaration> decls, 
      Table<Type> prev,
      Table<Table<Type>> types)
   {
      Table<Type> declsTable = new Table<Type>(prev, "identifiers");
      for (Declaration d : decls) {
         if (d.getType() instanceof StructType) {
            StructType t = (StructType)d.getType();
            if (!types.containsKey(t.getName())) {
               System.out.println("type " + t.getName() + " undeclared");
               continue;
            }
         }
         try {
            declsTable.insert(d.getName(), d.getType());
         } catch (DuplicatedIdentifierDeclarationException e) {
            System.out.println(e.getErrorMessage());
         }
      }
      return declsTable;
   }

   public static Table<Table<Type>> buildTypeDeclarationTable(
      List<TypeDeclaration> types
   )
   {
      Table<Table<Type>> typesTable = new Table<Table<Type>>(null, "type");
      for (TypeDeclaration t : types) {
         try {
            typesTable.insert(t.getName(), null);
            typesTable.overwrite(t.getName(), buildDeclarationsTable(t.getFields(), null, typesTable));
         } catch (DuplicatedIdentifierDeclarationException e) {
            System.out.println(e.getErrorMessage());
         }
      }
      return typesTable;
   }

   public static SymbolTable buildSymbolTable(Program p) {
      Table<Function> funcsTable = buildFunctionsTable(p.getFuncs());
      Table<Table<Type>> typesTable = buildTypeDeclarationTable(p.getTypes());
      Table<Type> declsTable = buildDeclarationsTable(p.getDecls(), null, typesTable);
      System.out.println("building symbolic table...");
      return new SymbolTable(funcsTable, declsTable, typesTable);
   }
}