package staticChecker;

import ast.*; 

public interface AstVisitor<T>{
   public T visit (Program program);
   public T visit (TypeDeclaration typeDecl);
   public T visit (Declaration decl);
   public T visit (Function func);

   public T visit (BoolType boolType);
   public T visit (IntType intType);
   public T visit (StructType structType);
   public T visit (VoidType voidType);

   public T visit (Statement statement);
   public T visit (AssignmentStatement assignmentStatement);
   public T visit (BlockStatement blockStatement);
   public T visit (ConditionalStatement conditionalStatement);
   public T visit (DeleteStatement deleteStatement);
   public T visit (InvocationStatement invocationStatement);
   public T visit (PrintLnStatement printLnStatement);
   public T visit (PrintStatement printStatement);
   public T visit (ReturnEmptyStatement returnEmptyStatement);
   public T visit (ReturnStatement returnStatement);
   public T visit (WhileStatement whileStatement);

   public T visit (BinaryExpression binaryExpression);
   public T visit (DotExpression dotExpression);
   public T visit (FalseExpression falseExpression);
   public T visit (IdentifierExpression identifierExpression);
   public T visit (IntegerExpression integerExpression);
   public T visit (InvocationExpression invocationExpression);
   public T visit (NewExpression newExpression);
   public T visit (NullExpression nullExpression);
   public T visit (ReadExpression readExpression);
   public T visit (TrueExpression trueExpression);
   public T visit (UnaryExpression unaryExpression);

   public T visit (LvalueDot lvalueDot);
   public T visit (LvalueId lvalueId);
   //public T visit ();
}
