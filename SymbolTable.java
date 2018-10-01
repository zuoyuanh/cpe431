import java.util.HashMap;
import exceptions.IdentifierNotFoundException;

public class SymbolTable
{
   public Table<ast.TypeDeclaration> typesTable;
   public Table<ast.Declaration> declsTable;
   public Table<ast.Function> funcsTable;

   public SymbolTable(
      Table<ast.Function> funcsTable,
      Table<ast.Declaration> declsTable,
      Table<ast.TypeDeclaration> typesTable
   ) {
      this.funcsTable = funcsTable;
      this.declsTable = declsTable;
      this.typesTable = typesTable;
   }
}