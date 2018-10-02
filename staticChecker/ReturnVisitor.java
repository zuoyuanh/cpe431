package staticChecker;

import ast.*;
import java.util.List;
import java.util.ArrayList;
import exceptions.DuplicatedIdentifierDeclarationException;

public class ReturnVisitor implements AstVisitor<Type>
{
   private static Table<Table<Type>> typesTable = new Table<Table<Type>>(null, "type");
   private static Table<Type> declsTable = new Table<Type>(null, "identifiers");
   private static Table<FunctionType> funcsTable = new Table<FunctionType>(null, "functions");

   public Type visit(Program program)
   {
      List<TypeDeclaration> types = program.getTypes();
      for (TypeDeclaration typeDecl : types){
         this.visit(typeDecl);
      }
      List<Declaration> decls = program.getDecls();
      for (Declaration decl : decls){
         this.visit(decl);
      }
      List<Function> funcs = program.getFuncs();
      for (Function func : funcs){
         this.visit(func);
      }
      return new VoidType(); 
   }

   public Type visit(TypeDeclaration typeDecl)
   {
      insertTypeDeclarationTable(typeDecl, typesTable);
      return new VoidType(); 
   }

   public Type visit(Declaration decl)
   {
      insertDeclarationsTable(decl, declsTable);
      return new VoidType(); 
   }

   public Type visit(Function func)
   {
      insertFunctionsTable(func, funcsTable);
      newLocalTable();
      List<Declaration> params = func.getParams();
      List<Declaration> locals = func.getLocals();
      insertDecls(params, declsTable);
      insertDecls(locals, declsTable);
      Statement body = func.getBody();
      Type retType = this.visit(body);
      deleteLocalTable();

      // TODO
      if (retType instanceof ReturnType) {

      }
      return new VoidType(); 
   }

   public Type visit(Type t)
   {
      return t;
   }

   // Statements

   public Type visit(Statement s)
   {
      if (s instanceof BlockStatement) {
         return this.visit((BlockStatement)s);
      } else if (s instanceof ConditionalStatement) {
         return this.visit((ConditionalStatement)s);
      } else if ((s instanceof PrintLnStatement) 
              || (s instanceof PrintStatement)
              || (s instanceof DeleteStatement)
              || (s instanceof ReturnEmptyStatement)) {
         return new VoidType();
      } else if (s instanceof AssignmentStatement) {
         return this.visit((AssignmentStatement)s);
      } else if (s instanceof WhileStatement) {
         return this.visit((WhileStatement)s);
      } else if (s instanceof ReturnStatement) {
         return this.visit((ReturnStatement)s);
      }
      return new VoidType();
   }

   public Type visit(BlockStatement s)
   {
      List <Statement> statements = s.getStatements();
      InconsistantReturnType conditionTypeTracker = new InconsistantReturnType();
      for (Statement st : statements) {
         Type returnType = this.visit(st);
         if (returnType instanceof ReturnType) {
            if (conditionTypeTracker.size() > 0) {
               boolean typeFound = false;
               InconsistantReturnType finalTypeTracker = new InconsistantReturnType();
               List<Type> types = finalTypeTracker.getTypes();
               for (Type t : types) {
                  if (t instanceof VoidType) {
                     continue;
                  } else  {
                     finalTypeTracker.add(t);
                     if (t.getClass() == returnType.getClass()) {
                        typeFound = true;
                     }
                  }
               }
               if (!typeFound) {
                  finalTypeTracker.add(returnType);
               }
               return finalTypeTracker;
            }
            return returnType;
         } else if (returnType instanceof InconsistantReturnType) {
            List<Type> types = ((InconsistantReturnType)returnType).getTypes();
            for (Type t : types) {
               conditionTypeTracker.add(t);
            }
         }
      }
      return new VoidType(); 
   }

   public Type visit(ConditionalStatement s)
   {
      Type thenType = this.visit(s.getThenBlock());
      Type elseType = this.visit(s.getElseBlock());
      if (thenType.getClass() == elseType.getClass()) {
         return thenType;
      }
      InconsistantReturnType irt = new InconsistantReturnType();
      irt.add(thenType);
      irt.add(elseType);
      return irt;
   }

   public Type visit(AssignmentStatement s)
   {
      return this.visit(s.getTarget());
   }

   public Type visit(WhileStatement s)
   {
      return new VoidType();
   }

   public Type visit(ReturnStatement s)
   {
      return new ReturnType(s.getLineNum(), this.visit(s.getExpression()));
   }

   // Expressions

   public Type visit(Expression e)
   {
      if (e instanceof TrueExpression) {
         return this.visit((TrueExpression)e);
      } else if (e instanceof FalseExpression) {
         return this.visit((FalseExpression)e);
      } else if (e instanceof IntegerExpression) {
         return this.visit((IntegerExpression)e);
      } else if (e instanceof ReadExpression) {
         return this.visit((ReadExpression)e);
      } else if (e instanceof IdentifierExpression) {
         return this.visit((IdentifierExpression)e);
      } else if (e instanceof DotExpression) {
         return this.visit((DotExpression)e);
      } else if (e instanceof UnaryExpression) {
         return this.visit((UnaryExpression)e);
      } else if (e instanceof NewExpression) {
         return this.visit((NewExpression)e);
      }
      return new VoidType();
   }

   public Type visit(TrueExpression e)
   {
      return new BoolType();
   }

   public Type visit(FalseExpression e)
   {
      return new BoolType();
   }

   public Type visit(IntegerExpression e)
   {
      return new IntType();
   }

   public Type visit(ReadExpression e)
   {
      return new IntType();
   }

   public Type visit(IdentifierExpression e)
   {
      String id = e.getId();
      return declsTable.get(id);
   }

   public Type visit(DotExpression e)
   {
      Type t = this.visit(e.getLeft());
      if (t instanceof StructType) {
         Table<Type> table = typesTable.get(((StructType)t).getName());
         return table.get(e.getId());
      }
      return new VoidType();
   }

   public Type visit(UnaryExpression e)
   {
      return this.visit(e.getOperand());
   }

   public Type visit(NewExpression e)
   {
      return new VoidType();
   }

   public Type visit(BinaryExpression e)
   {
      Type leftType = this.visit(e.getLeft());
      Type rightType = this.visit(e.getRight());
      BinaryExpression.Operator operator = e.getOperator();
      switch (operator) {
      case TIMES: case DIVIDE: 
      case PLUS: case MINUS:
         return leftType;
      case LT: case GT: case LE:
      case GE: case EQ: case NE:
      case AND: case OR:
         return new BoolType();
      default:
         return new VoidType();
      }
   }

   public Type visit(LvalueDot lvalueDot)
   {
      /* Type s = this.visit(lvalueDot.getLeft());
      // checkSameType(s.getClass(), StructType.class);
      String id = lvalueDot.getId();
      try {
         Type t = (typesTable.get( ((StructType)s) .getName()) ).get(id);
         return t;
      } catch (IdentifierNotFoundException e ){
         System.out.println("Identifier not found");
         return null;
      } */
      return new VoidType();
   }

   public Type visit(LvalueId lvalueId)
   {
      /* try {
         Type t = declsTable.get(lvalueId.getId());
         return t;
      } catch (IdentifierNotFoundException e ){
         System.out.println("Identifier not found");
         return null;
      } */
      return new VoidType();
   }

   public Type visit(Lvalue lvalue)
   {
      /* if (lvalue instanceof LvalueId){
         return this.visit((LvalueId)lvalue);
      }
      if (lvalue instanceof LvalueDot){
         return this.visit((LvalueDot)lvalue);
      }
      return null; */
      return new VoidType();
   }


   private static Table<Table<Type>> insertTypeDeclarationTable(
      TypeDeclaration type, Table<Table<Type>> typesTable
   )
   {
      try {
         typesTable.insert(type.getName(), null);
         typesTable.overwrite(type.getName(), buildDeclarationsTable(type.getFields(), null, typesTable));
      } catch (DuplicatedIdentifierDeclarationException e) {
         System.out.println(e.getErrorMessage());
      }
      return typesTable;
   }

   private static void insertDeclarationsTable(Declaration decl, Table<Type> tbl)
   {
      if (decl.getType() instanceof StructType) {
        checkTypeTable(((StructType)decl.getType()).getName(), typesTable);
      }
      try {
         tbl.insert(decl.getName(), decl.getType());
      } catch (DuplicatedIdentifierDeclarationException e) {
         System.out.println(e.getErrorMessage());
      }
      return;
   }

   private static void checkTypeTable(String key, Table<Table<Type>> tbl){
      if (!tbl.containsKey(key)){
         System.out.println("struct name " + key + " undeclared");
      }
      return;
   }

   private static Table<Type> buildDeclarationsTable(
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

   private static void insertFunctionsTable(Function f, Table<FunctionType> funcsTable)
   {
      try {
         funcsTable.insert(f.getName(), new FunctionType(f.getLineNum(), f.getName(), f.getParams(), f.getRetType()));
      } catch (DuplicatedIdentifierDeclarationException e) {
         System.out.println(e.getErrorMessage());
      }
      return;
   }

   private void insertDecls(List<Declaration> decls, Table<Type> tbl)
   {
      for (Declaration decl : decls){
         insertDeclarationsTable(decl, tbl);
      }
      return;
   }

   private void newLocalTable()
   {
      Table<Type> localTable = new Table<Type>(declsTable, "identifiers");
      declsTable = localTable;
   }

   private void deleteLocalTable()
   {
      if (declsTable == null) {
         System.out.println("something is wrong..");
      }
      declsTable = declsTable.prev;  
      return;
   }
}