package staticChecker;

import ast.*;
import exceptions.DuplicatedIdentifierDeclarationException;

public class TypeVisitor implements AstVisitor<Type>{
   
   Table<Table<Type>> typesTable = new Table<Table<Type>>(null, "type");
   Table<Type> declsTable = new Table<Type>(null, "identifiers");
   Table<FunctionType> funcsTable = new Table<FunctionType>(null, "functions");


   public Type visit (Program program){
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
      /*
      typesTable = SymbolTableBuilder.buildTypeDeclarationTable(program.getTypes());
      declsTable = SymbolTableBuilder.buildDeclarationsTable(program.getDecls(), null, typesTable);
      funcsTable = SymbolTableBuilder.buildFunctionsTable(program.getFuncs());
      */
      System.out.println("visiting program");
      return new VoidType(); 
   }
   public Type visit (TypeDeclaration typeDecl){
      insertTypeDeclarationTable(typeDecl,  typesTable)
      System.out.println("visiting typeDecl");
      return new VoidType(); 
   }
   public Type visit (Declaration decl){
      insertDeclarationsTable(decl, declsTable);
      System.out.println("visiting Decl");
      return new VoidType(); 
   }
   public Type visit (Function func){
      insertFunctionsTable(func, funcsTable);
      Statement body = func.getBody();
      Type retType = this.visit(body);
      System.out.println("visiting typeDecl");
      return new VoidType(); 
      //compare ret type
   }

   public Type visit (BoolType boolType){
      System.out.println("visiting bool type");
      return boolType;
   }
   public Type visit (IntType intType){
      System.out.println("visiting int type");
      return intType;
   }
   public Type visit (StructType structType){
      System.out.println("visiting struct type");
      return structType;
   }
   public Type visit (VoidType voidType){
      System.out.println("visiting void type");
      return voidType;
   }

   //public Type visit (Statement statement);
   public Type visit (AssignmentStatement assignmentStatement){
      Lvalue target = assignmentStatement.getTarget();
      Expression source = assignmentStatement.getSource();
      Type targetType = this.visit(target);
      type sourceType = this.visit(source);
      checkCompatible(targetType, sourceType);
      System.out.println("visiting assign");
      return sourceType
   }

   public Type visit (BlockStatement blockStatement){
      List <Statement> statements = blockStatement.getStatements();
      for (Statement s : statements){
         this.visit(s);
      }
      System.out.println("visiting block");
      return new VoidType(); 
   }

   public Type visit (ConditionalStatement conditionalStatement)
   {
      Type guardType = conditionalStatement.getGuard();
      checkSameType(guardType.getClass(), boolType.class);
      this.visit(conditionalStatement.getThenBlock());
      this.visit(conditionalStatement.getElseBlock());
      return new VoidType(); 
   }
   public Type visit (DeleteStatement deleteStatement){
      Type st = this.visit(deleteStatement.getExpression());
      checkSameType(st.getClass(), StructType.class);
      //TODO remove struct from table
      return new VoidType(); 
   }
   public Type visit (InvocationStatement invocationStatement){
      return this.visit(invocationStatement.getExpression);
   }
   public Type visit (PrintLnStatement printLnStatement){
      Type res = this.visit(printLnStatement.getExpression());
      checkSameType(res.getClass(), IntType.class);
      return new IntType();
   }
   public Type visit (PrintStatement printStatement){
      Type res = this.visit(printStatement.getExpression());
      checkSameType(res.getClass(), IntType.class);
      return new IntType();
   }
   public Type visit (ReturnEmptyStatement returnEmptyStatement);
   public Type visit (ReturnStatement returnStatement);
   public Type visit (WhileStatement whileStatement);

   public Type visit (BinaryExpression binaryExpression);
   public Type visit (DotExpression dotExpression);
   public Type visit (FalseExpression falseExpression);
   public Type visit (IdentifierExpression identifierExpression);
   public Type visit (IntegerExpression integerExpression);
   public Type visit (InvocationExpression invocationExpression);
   public Type visit (NewExpression newExpression);
   public Type visit (NullExpression nullExpression);
   public Type visit (ReadExpression readExpression);
   public Type visit (TrueExpression trueExpression);
   public Type visit (UnaryExpression unaryExpression);

   public T visit (LvalueDot lvalueDot);
   public T visit (LvalueId lvalueId);


   public static Table<FunctionType> buildFunctionsTable(List<Function> funcs)
   {
      Table<FunctionType> funcsTable = new Table<FunctionType>(null, "functions");
      for (Function f : funcs) {
         try {
            funcsTable.insert(f.getName(), new FunctionType(f.getLineNum(), f.getName(), f.getParams(), f.getRetType()));
         } catch (DuplicatedIdentifierDeclarationException e) {
            System.out.println(e.getErrorMessage());
         }
      }
      return funcsTable;
   }
   public static void Table<FunctionType> insertFunctionsTable(Function func, Table<FunctionType> funcsTable)
   {
      try {
         funcsTable.insert(f.getName(), new FunctionType(f.getLineNum(), f.getName(), f.getParams(), f.getRetType()));
      } catch (DuplicatedIdentifierDeclarationException e) {
         System.out.println(e.getErrorMessage());
      }
      return;
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
   public static void  insertDeclarationsTable(Declaration decl, Table<Type> tbl)
   {
      /*
         if (d.getType() instanceof StructType) {
            StructType t = (StructType)d.getType();
            if (!types.containsKey(t.getName())) {
               System.out.println("type " + t.getName() + " undeclared");
               continue;
            }
         }
      */
         try {
            tbl.insert(decl.getName(), decl.getType());
         } catch (DuplicatedIdentifierDeclarationException e) {
            System.out.println(e.getErrorMessage());
         }
      return;
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
   public static Table<Table<Type>> insertTypeDeclarationTable(
      TypeDeclaration type, Table<Table<Type>> typesTable
   )
   {
         try {
            typesTable.insert(type.getName(), null);
            typesTable.overwrite(type.getName(), buildDeclarationsTable(t.getFields(), null, typesTable));
         } catch (DuplicatedIdentifierDeclarationException e) {
            System.out.println(e.getErrorMessage());
         }
      
      return typesTable;
   }

   public void checkCompatible(Type target, Type source){
      if (source instanceof VoidType) return;
      return checkSameType(target.getClass(), source.getClass());
   }
   public void checkSameType(Class c1, Class c2){
      if (c1.equals(c2)) return;
      System.out.println("incompatible types" + c1+ " and " + c2);
   }
}
