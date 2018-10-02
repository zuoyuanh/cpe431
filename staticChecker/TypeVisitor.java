package staticChecker;
import java.util.List;
import ast.*;
import exceptions.*;

public class TypeVisitor implements AstVisitor<Type>{
   
   static Table<Table<Type>> typesTable = new Table<Table<Type>>(null, "type");
   static Table<Type> declsTable = new Table<Type>(null, "identifiers");
   static Table<FunctionType> funcsTable = new Table<FunctionType>(null, "functions");


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

      if (!funcsTable.containsKey("main")) {
         System.out.println("missing main function");
      }
      return new VoidType(); 
   }
   public Type visit (TypeDeclaration typeDecl){
      insertTypeDeclarationTable(typeDecl,  typesTable);
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
      newLocalTable();
      List<Declaration> params = func.getParams();
      List<Declaration> locals = func.getLocals();
      insertDecls( params, declsTable);
      insertDecls( locals, declsTable);
      Statement body = func.getBody();
      Type retType = this.visit(body);
      System.out.println("visiting func");
      deleteLocalTable();
      return new VoidType(); 
      //compare ret type
   }
   public Type visit(Expression e){
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
      } else if (e instanceof BinaryExpression) {
         return this.visit((BinaryExpression)e);
      } else if (e instanceof InvocationExpression) {
         return this.visit((InvocationExpression)e);
      } else if (e instanceof NullExpression) {
         return this.visit((NullExpression)e);
      }
      return null;
   }

   public Type visit(Statement s)
   {
      if (s instanceof BlockStatement) {
         return this.visit((BlockStatement)s);
      } else if (s instanceof ConditionalStatement) {
         return this.visit((ConditionalStatement)s);
      } else if (s instanceof PrintLnStatement){
         return this.visit((PrintLnStatement)s); 
      } else if (s instanceof PrintStatement){
         return this.visit((PrintStatement)s);
      } else if (s instanceof DeleteStatement){
         return this.visit((DeleteStatement)s);
      } else if (s instanceof ReturnEmptyStatement){
         return this.visit((ReturnEmptyStatement)s);
      } else if (s instanceof AssignmentStatement) {
         return this.visit((AssignmentStatement)s);
      } else if (s instanceof WhileStatement) {
         return this.visit((WhileStatement)s);
      } else if (s instanceof ReturnStatement) {
         return this.visit((ReturnStatement)s);
      } else if (s instanceof InvocationStatement) {
         return this.visit((InvocationStatement)s);
      }
      return null;
   }
   public Type visit(Lvalue lvalue){
      if (lvalue instanceof LvalueId){
         return this.visit((LvalueId)lvalue);
      }
      if (lvalue instanceof LvalueDot){
         return this.visit((LvalueDot)lvalue);
      }
      return null;
   }

   public Type visit (Type type){
      System.out.println("visiting type");
      return type;
   }

   //public Type visit (Statement statement);
   public Type visit (AssignmentStatement assignmentStatement){
      Lvalue target = assignmentStatement.getTarget();
      Expression source = assignmentStatement.getSource();
      Type targetType = this.visit(target);
      Type sourceType = this.visit(source);
      checkCompatible(targetType, sourceType);
      System.out.println("visiting assign");
      return sourceType;
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
      Type guardType = this.visit(conditionalStatement.getGuard());
      if (guardType == null) {
         return null;
      }
      checkSameType(guardType.getClass(), BoolType.class);
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
      return this.visit(invocationStatement.getExpression());
   }
   public Type visit (PrintLnStatement printLnStatement){
      Type res = this.visit(printLnStatement.getExpression());
      if (res != null) {
         checkSameType(res.getClass(), IntType.class);
      }
      return new IntType();
   }
   public Type visit (PrintStatement printStatement){
      Type res = this.visit(printStatement.getExpression());
      checkSameType(res.getClass(), IntType.class);
      return new IntType();
   }
   public Type visit (ReturnEmptyStatement returnEmptyStatement){
      return new VoidType(); 
   }
   public Type visit (ReturnStatement returnStatement){
      System.out.println("visiting return");
      return this.visit(returnStatement.getExpression());
   }
   public Type visit (WhileStatement whileStatement){
      Type guardType = this.visit(whileStatement.getGuard());
      checkSameType(guardType.getClass(), BoolType.class);
      return this.visit(whileStatement.getBody());
   }

   public Type visit (BinaryExpression binaryExpression){
      Type leftType = this.visit(binaryExpression.getLeft());
      Type rightType = this.visit(binaryExpression.getRight());
      if (leftType == null || rightType == null) {
         return null;
      }
      checkSameType(leftType.getClass(), rightType.getClass());
      BinaryExpression.Operator op = binaryExpression.getOperator();
      switch (op){
         case TIMES: 
            checkSameType(leftType.getClass(),IntType.class);
            return new IntType();       
         case DIVIDE:
            checkSameType(leftType.getClass(),IntType.class);
            return new IntType();       
         case PLUS:
            checkSameType(leftType.getClass(),IntType.class);
            return new IntType();       
         case MINUS:
            checkSameType(leftType.getClass(),IntType.class);
            return new IntType();       
         case LT:
            checkSameType(leftType.getClass(),IntType.class);
            return new BoolType();  
         case LE:
            checkSameType(leftType.getClass(),IntType.class);
            return new BoolType();      
         case GT:
            checkSameType(leftType.getClass(),IntType.class);
            return new BoolType();       
         case GE:
            checkSameType(leftType.getClass(),IntType.class);
            return new BoolType();       
         case EQ:
            if (!(leftType instanceof StructType))
               checkSameType(leftType.getClass(),IntType.class);
            return new BoolType();       
         case NE:
            if (!(leftType instanceof StructType ))
               checkSameType(leftType.getClass(),IntType.class);
            return new BoolType();       
         case AND:
            checkSameType(leftType.getClass(), BoolType.class);
            return new BoolType();       
         case OR:
            checkSameType(leftType.getClass(),BoolType.class);
            return new BoolType(); 
         default:
            return null;
      }
   }


   public Type visit (DotExpression dotExpression){
      Type s = this.visit(dotExpression.getLeft());
      if (s instanceof StructType) {
         checkSameType(s.getClass(), StructType.class);
         String id = dotExpression.getId();
         try {
            Type t = (typesTable.get(((StructType)s).getName()) ).get(id);
            return t;
         } catch (IdentifierNotFoundException e ){
            System.out.println("identifier '" + id + "' not found");
         }
      } else {
         System.out.println("dot operation not allowed on non-struct type instance");
      }
      return null;
   }
   public Type visit (FalseExpression falseExpression){
      return new BoolType();
   }
   public Type visit (IdentifierExpression identifierExpression){
      try {
         Type t = declsTable.get(identifierExpression.getId());
         return t;
      } catch (IdentifierNotFoundException e ){
         System.out.println("Identifier not found");
         return null;
      }
   }
   public Type visit (IntegerExpression integerExpression){
      return new IntType();
   }
   public Type visit (InvocationExpression invocationExpression){
      String funcName = invocationExpression.getName();
      FunctionType funcType = null;
      try {
         funcType = funcsTable.get(funcName);
      } catch (IdentifierNotFoundException e ){
         System.out.println("Identifier not found");
         return null;
      }
      List<Declaration> params = funcType.getParams();
      List<Expression> args = invocationExpression.getArguments();
      matchTypes(params, args);
      return funcType.getRetType();
   }
   public Type visit (NewExpression newExpression){
      String id = newExpression.getId();
      checkTypeTable(id, typesTable);
      return new StructType(-1, id);
   }
   public Type visit (NullExpression nullExpression){
      return new NullType();
   }
   public Type visit (ReadExpression readExpression){
      return new IntType();
   }
   public Type visit (TrueExpression trueExpression){
      return new BoolType();
   }
   public Type visit (UnaryExpression unaryExpression){
      Type operandType = this.visit(unaryExpression.getOperand());
      UnaryExpression.Operator op = unaryExpression.getOperator();
      switch (op){
         case NOT: 
            checkSameType(operandType.getClass(), BoolType.class);
            return new BoolType();
         case MINUS:
            checkSameType(operandType.getClass(), IntType.class);
            return new IntType();
         default: 
            return null;
      }
   }

   public Type visit (LvalueDot lvalueDot){
      Type s = this.visit(lvalueDot.getLeft());
      checkSameType(s.getClass(), StructType.class);
      String id = lvalueDot.getId();
      try {
         Type t = (typesTable.get( ((StructType)s) .getName()) ).get(id);
         return t;
      } catch (IdentifierNotFoundException e ){
         System.out.println("Identifier not found");
         return null;
      }
   }
   public Type visit (LvalueId lvalueId){
      try {
         Type t = declsTable.get(lvalueId.getId());
         return t;
      } catch (IdentifierNotFoundException e ){
         System.out.println("Identifier not found");
         return null;
      }
   }

/*
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
   }*/
   public static void  insertFunctionsTable(Function f, Table<FunctionType> funcsTable)
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
   public void insertDecls(List<Declaration> decls, Table<Type> tbl){
      for (Declaration decl : decls){
         insertDeclarationsTable(decl, tbl);
      }
      return;
   }
   public static void checkTypeTable(String key, Table<Table<Type>> tbl){
      if (!tbl.containsKey(key)){
         System.out.println("struct name " + key + " undeclared");
      }
      return;
   }
   /*
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
   }*/
   public static Table<Table<Type>> insertTypeDeclarationTable(
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

   public void checkCompatible(Type target, Type source){
      if (source instanceof VoidType) return;
      checkSameType(target.getClass(), source.getClass());
      return;
   }
   public void checkSameType(Class c1, Class c2){
      if (c1.equals(c2)) return;
      if ((c1==NullType.class && c2==StructType.class) || (c2==NullType.class && c1==StructType.class)) return;
      System.out.println("incompatible types" + c1+ " and " + c2);
   }

   public void newLocalTable(){
      Table<Type> localTable = new Table<Type>(declsTable, "identifiers");
      declsTable = localTable;
   }
   public void deleteLocalTable(){
      if (declsTable==null) {
         System.out.println("something is wrong..");
      }
      declsTable = declsTable.prev;  
      return;
   }
   public void matchTypes(List<Declaration> params, List<Expression> args){
      if (params.size() != args.size()){
         System.out.println("the number of params and args don't match");
         return;
      }
      for (int i = 0; i< params.size(); i++){ 
         Declaration param  = params.get(i);
         Expression arg = args.get(i);
         Type argType = this.visit(arg);
         checkSameType(param.getType().getClass(), argType.getClass());
      }
      return;
   }
}
