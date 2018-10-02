package staticChecker;

import ast.*;
import java.util.List;
import java.util.ArrayList;

public class ReturnVisitor implements AstVisitor<Type>
{
   private Table<Table<Type>> typesTable = new Table<Table<Type>>(null, "type");
   private Table<Type> declsTable = new Table<Type>(null, "identifiers");
   private Table<FunctionType> funcsTable = new Table<FunctionType>(null, "functions");

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
      System.out.println("visiting program");
      return new VoidType(); 
   }

   public Type visit(TypeDeclaration typeDecl)
   {
      insertTypeDeclarationTable(typeDecl,  typesTable)
      System.out.println("visiting typeDecl");
      return new VoidType(); 
   }

   public Type visit(Declaration decl)
   {
      insertDeclarationsTable(decl, declsTable);
      System.out.println("visiting Decl");
      return new VoidType(); 
   }

   public Type visit(Function func)
   {
      insertFunctionsTable(func, funcsTable);
      Statement body = func.getBody();
      Type retType = this.visit(body);
      System.out.println("visiting typeDecl");
      return new VoidType(); 
      //compare ret type
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

   // TODO

   public Type visit(BlockStatement s)
   {
      List <Statement> statements = s.getStatements();
      List<Type> conditionTypeTracker = new ArrayList<Type>();
      for (Statement s : statements) {
         Type returnType = this.visit(s);
         if (returnType instanceof ReturnType) {
            return returnType;
         } else if (returnType instanceof InconsistantReturnType) {
            if ()
         }
      }
      return new VoidType(); 
   }

   public Type visit(ConditionalStatement s)
   {
      Type thenType = this.visit(s.getThenBlock());
      Type elseType = this.visit(s.getElseBlock());
      if (thenType.class.equals(elseType.class)) {
         return thenType;
      }
      return new InconsistantReturnType(thenType, elseType); 
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
      return VoidType();
   }

   public Type visit(UnaryExpression e)
   {
      return this.visit(e.getOperand());
   }

   public Type visit(NewExpression e)
   {
      return VoidType();
   }

   public Type visit(BinaryExpression e)
   {
      Type leftType = this.visit(e.getLeft());
      Type rightType = this.visit(e.getRight());
      BinaryExpression.Operator operator = e.getOperator();
      switch (operator) {
      case BinaryExpression.Operator.TIMES:
      case BinaryExpression.Operator.DIVIDE:
      case BinaryExpression.Operator.PLUS:
      case BinaryExpression.Operator.MINUS:
         return leftType;
      case BinaryExpression.Operator.LT:
      case BinaryExpression.Operator.GT:
      case BinaryExpression.Operator.LE:
      case BinaryExpression.Operator.GE:
      case BinaryExpression.Operator.EQ:
      case BinaryExpression.Operator.NE:
      case BinaryExpression.Operator.AND:
      case BinaryExpression.Operator.OR:
         return new BoolType();
      default:
         return new VoidType();
      }
   }
}