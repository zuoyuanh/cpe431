package ast;

import java.util.HashMap;
import exceptions.IdentifierNotFoundException;

public class SymbolTable
{
   public Table<Table<ast.Type>> typesTable;
   public Table<ast.Type> declsTable;
   public Table<ast.Function> funcsTable;

   public SymbolTable(
      Table<ast.Function> funcsTable,
      Table<ast.Type> declsTable,
      Table<Table<ast.Type>> typesTable
   ) {
      this.funcsTable = funcsTable;
      this.declsTable = declsTable;
      this.typesTable = typesTable;
   }
}