package staticChecker;
import java.util.List;
import java.util.ArrayList;
import ast.*;
import exceptions.*;

public class CFGGenerator {
  /* 
   static Table<Table<Type>> typesTable = new Table<Table<Type>>(null, "type");
   static Table<Type> declsTable = new Table<Type>(null, "identifiers");
   static Table<FunctionType> funcsTable = new Table<FunctionType>(null, "functions");
*/
   public static List<Block> blockList;

   public Block visit (Program program, Block current, Block exit){
      /*
      List<TypeDeclaration> types = program.getTypes();
      for (TypeDeclaration typeDecl : types){
         this.visit(typeDecl);
      }
      List<Declaration> decls = program.getDecls();
      for (Declaration decl : decls){
         this.visit(decl);
      }
      */
      Block progNode = new Block(Block.Label.PROGRAM);
      blockList = new ArrayList<Block>();
      blockList.add(progNode);
      List<Function> funcs = program.getFuncs();
      for (Function func : funcs){
         Block funcNode = this.visit(func, null, null);
         progNode.addSuccessor(funcNode); 
      }
      //System.out.println("visiting program");
      /*
      if (!funcsTable.containsKey("main")) {
         System.out.println("missing main function");
      }
      return new VoidType(); 
      */
      return progNode;
   }
   public Block visit (TypeDeclaration typeDecl, Block current, Block exit){
      //insertTypeDeclarationTable(typeDecl,  typesTable);
      return current; 
   }
   public Block visit (Declaration decl, Block cur, Block exit){
      //insertDeclarationsTable(decl, declsTable);
      //System.out.println("visiting Decl");
      return cur; 
   }
   public Block visit (Function func, Block cur, Block exit){
      /*
      insertFunctionsTable(func, funcsTable);
      newLocalTable();
      List<Declaration> params = func.getParams();
      List<Declaration> locals = func.getLocals();
      insertDecls( params, declsTable);
      insertDecls( locals, declsTable);
      */
      Statement body = func.getBody();
      /*
      Type retType = this.visit(body);
      deleteLocalTable();
      */
      Block entry = new Block(Block.Label.ENTRY);
      blockList.add(entry);
      Block exitBlock = new Block(Block.Label.EXIT);
      this.visit(body, entry, exitBlock);
      blockList.add(exitBlock);
      return entry;

   }
   public Block visit(Expression e, Block cur, Block exit){
      if (e instanceof TrueExpression) {
         return this.visit((TrueExpression)e, cur, exit);
      } else if (e instanceof FalseExpression) {
         return this.visit((FalseExpression)e, cur, exit);
      } else if (e instanceof IntegerExpression) {
         return this.visit((IntegerExpression)e, cur, exit);
      } else if (e instanceof ReadExpression) {
         return this.visit((ReadExpression)e, cur, exit);
      } else if (e instanceof IdentifierExpression) {
         return this.visit((IdentifierExpression)e, cur, exit);
      } else if (e instanceof DotExpression) {
         return this.visit((DotExpression)e, cur, exit);
      } else if (e instanceof UnaryExpression) {
         return this.visit((UnaryExpression)e, cur, exit);
      } else if (e instanceof NewExpression) {
         return this.visit((NewExpression)e, cur, exit);
      } else if (e instanceof BinaryExpression) {
         return this.visit((BinaryExpression)e, cur, exit);
      } else if (e instanceof InvocationExpression) {
         return this.visit((InvocationExpression)e, cur, exit);
      } else if (e instanceof NullExpression) {
         return this.visit((NullExpression)e, cur, exit);
      }
      return null;
   }

   public Block visit(Statement s, Block cur, Block exit)
   {
      if (s instanceof BlockStatement) {
         return this.visit((BlockStatement)s, cur, exit);
      } else if (s instanceof ConditionalStatement) {
         return this.visit((ConditionalStatement)s, cur, exit);
      } else if (s instanceof PrintLnStatement){
         return this.visit((PrintLnStatement)s, cur, exit); 
      } else if (s instanceof PrintStatement){
         return this.visit((PrintStatement)s, cur, exit);
      } else if (s instanceof DeleteStatement){
         return this.visit((DeleteStatement)s, cur, exit);
      } else if (s instanceof ReturnEmptyStatement){
         return this.visit((ReturnEmptyStatement)s, cur, exit);
      } else if (s instanceof AssignmentStatement) {
         return this.visit((AssignmentStatement)s, cur, exit);
      } else if (s instanceof WhileStatement) {
         return this.visit((WhileStatement)s, cur, exit);
      } else if (s instanceof ReturnStatement) {
         return this.visit((ReturnStatement)s, cur, exit);
      } else if (s instanceof InvocationStatement) {
         return this.visit((InvocationStatement)s, cur, exit);
      }
      return null;
   }
   public Block visit(Lvalue lvalue, Block cur, Block exit){
      if (lvalue instanceof LvalueId){
         return this.visit((LvalueId)lvalue, cur, exit);
      }
      if (lvalue instanceof LvalueDot){
         return this.visit((LvalueDot)lvalue, cur, exit);
      }
      return null;
   }

   public Block visit (Type type, Block cur, Block exit){
      return cur;
   }

   public Block visit (AssignmentStatement assignmentStatement, Block cur, Block exit){
      
      //Lvalue target = assignmentStatement.getTarget();
      Expression source = assignmentStatement.getSource();
      //Block targetBlock = this.visit(target,cur, exit);
      Block sourceBlock = this.visit(source, cur, exit);
      /*
      checkCompatible(targetType, sourceType);
      return sourceType;
      */
      return sourceBlock;
   }

   public Block visit (BlockStatement blockStatement, Block cur, Block exit){
      List <Statement> statements = blockStatement.getStatements();
      for (Statement s : statements){
         cur = this.visit(s, cur, exit);
      }
      return cur;
   }

   public Block visit (ConditionalStatement conditionalStatement, Block cur, Block exit)
   {
      cur = this.visit(conditionalStatement.getGuard(), cur, exit);
      /*
      if (guardType == null) {
         return null;
      }
      */
      Statement thenBlock = conditionalStatement.getThenBlock();
      Block afterThen = null;
      if (thenBlock!=null){
         Block thenNode = new Block(Block.Label.THEN);
         blockList.add(thenNode);
         cur.addSuccessor(thenNode);
         afterThen = this.visit(thenBlock, thenNode, exit);
      }
      Statement elseBlock = conditionalStatement.getElseBlock();
      Block afterElse = null;
      if (elseBlock!=null && (((AbstractStatement)elseBlock).getLineNum() != -1)){//create a else block if not empty
         Block elseNode = new Block(Block.Label.ELSE);
         blockList.add(elseNode);
         cur.addSuccessor(elseNode);
         afterElse = this.visit(elseBlock, elseNode, exit);
      }
      
      if (afterThen != null || afterElse != null){
         Block joinNode = new Block(Block.Label.JOIN);
         blockList.add(joinNode);
         if (afterThen !=null) afterThen.addSuccessor(joinNode);
         if (afterElse != null) afterElse.addSuccessor(joinNode);
         return joinNode;
      }
      return null; //both branches return 
   }
   public Block visit (DeleteStatement deleteStatement, Block cur, Block exit){
      Block st = this.visit(deleteStatement.getExpression(), cur, exit);
      return st; 
   }
   public Block visit (InvocationStatement invocationStatement, Block cur, Block exit){
      return this.visit(invocationStatement.getExpression(), cur, exit);
   }
   public Block visit (PrintLnStatement printLnStatement, Block cur, Block exit){
      Block res = this.visit(printLnStatement.getExpression(), cur, exit);
      return res;
   }
   public Block visit (PrintStatement printStatement, Block cur, Block exit){
      Block res = this.visit(printStatement.getExpression(), cur, exit);
      return res;
   }
   public Block visit (ReturnEmptyStatement returnEmptyStatement, Block cur, Block exit){
      Block retBlock = new Block(Block.Label.RETURN);
      blockList.add(retBlock);
      retBlock.addSuccessor(exit);
      cur.addSuccessor(retBlock);
      return null; 
   }
   public Block visit (ReturnStatement returnStatement, Block cur, Block exit){
      
      Block retBlock = new Block(Block.Label.RETURN);
      blockList.add(retBlock);
      visit(returnStatement.getExpression(), retBlock, exit); //add to return block code
      retBlock.addSuccessor(exit);
      cur.addSuccessor(retBlock);
      return null; 
   }
   public Block visit (WhileStatement whileStatement, Block cur, Block exit){
      Block guard = this.visit(whileStatement.getGuard(), cur, exit);
      Block whileExit = new Block(Block.Label.WHILE_EXIT);
      Block whileLoop = new Block(Block.Label.WHILE_LOOP);
      blockList.add(whileExit);
      blockList.add(whileLoop);
      whileLoop = this.visit(whileStatement.getBody(), whileLoop, exit);
      whileLoop.addSuccessor(whileLoop);
      whileLoop.addSuccessor(whileExit);
      guard.addSuccessor(whileLoop);
      guard.addSuccessor(whileExit);
      return whileExit;
   }

   public Block visit (BinaryExpression binaryExpression, Block cur, Block exit){
      Block leftType = this.visit(binaryExpression.getLeft(), cur, exit);
      Block rightType = this.visit(binaryExpression.getRight(), cur, exit);
      if (leftType == null || rightType == null) {
         return null;
      }
      return cur;
      /*
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
      */
   }


   public Block visit (DotExpression dotExpression, Block cur, Block exit){
      Block s = this.visit(dotExpression.getLeft(), cur, exit);
      /*
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
      */
      return cur;
   }
   public Block visit (FalseExpression falseExpression, Block cur, Block exit){
      return cur;
   }
   public Block visit (IdentifierExpression identifierExpression, Block cur, Block exit){
      /*
      try {
         Type t = declsTable.get(identifierExpression.getId());
         return t;
      } catch (IdentifierNotFoundException e ){
         System.out.println("Identifier not found");
         return null;
      }
      */
      return cur;
   }
   public Block visit (IntegerExpression integerExpression, Block cur, Block exit){
      return cur;
   }
   public Block visit (InvocationExpression invocationExpression, Block cur, Block exit){
      /*
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
      */
      return cur;
   }
   public Block visit (NewExpression newExpression, Block cur, Block exit){
      /*
      String id = newExpression.getId();
      checkTypeTable(id, typesTable);
      return new StructType(-1, id);
      */
      return cur;
   }
   public Block visit (NullExpression nullExpression, Block cur, Block exit){
      return cur;
   }
   public Block visit (ReadExpression readExpression, Block cur, Block exit){
      return cur;
   }
   public Block visit (TrueExpression trueExpression, Block cur, Block exit){
      return cur;
   }
   public Block visit (UnaryExpression unaryExpression, Block cur, Block exit){
      cur = this.visit(unaryExpression.getOperand(), cur, exit);
      /*
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
      */
      return cur;
   }

   public Block visit (LvalueDot lvalueDot, Block cur, Block exit){
      cur = this.visit(lvalueDot.getLeft(), cur, exit);
      /*
      checkSameType(s.getClass(), StructType.class);
      String id = lvalueDot.getId();
      try {
         Type t = (typesTable.get( ((StructType)s) .getName()) ).get(id);
         return t;
      } catch (IdentifierNotFoundException e ){
         System.out.println("Identifier not found");
         return null;
      }
      */
      return cur;
   }
   public Block visit (LvalueId lvalueId, Block cur, Block exit){
      /*
      try {
         Type t = declsTable.get(lvalueId.getId());
         return t;
      } catch (IdentifierNotFoundException e ){
         System.out.println("Identifier not found");
         return null;
      }
      */
      return cur;
   }
/*
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
   */
}
