package llvm;

import ast.*; 

public interface LLVMVisitor<T, B>{
   public T visit(Program program);
   public T visit(TypeDeclaration typeDecl);
   public T visit(Declaration decl);
   public T visit(Function func);

   public T visit(Type type);

   public T visit(Statement statement, B block);
   public T visit(AssignmentStatement assignmentStatement, B block);
   public T visit(BlockStatement blockStatement, B block);
   public T visit(ConditionalStatement conditionalStatement, B block);
   public T visit(DeleteStatement deleteStatement, B block);
   public T visit(InvocationStatement invocationStatement, B block);
   public T visit(PrintLnStatement printLnStatement, B block);
   public T visit(PrintStatement printStatement, B block);
   public T visit(ReturnEmptyStatement returnEmptyStatement, B block);
   public T visit(ReturnStatement returnStatement, B block);
   public T visit(WhileStatement whileStatement, B block);

   public T visit(Expression expression, B block);
   public T visit(BinaryExpression binaryExpression, B block);
   public T visit(DotExpression dotExpression, B block);
   public T visit(IdentifierExpression identifierExpression, B block);
   public T visit(IntegerExpression integerExpression);
   public T visit(TrueExpression trueExpression);
   public T visit(FalseExpression falseExpression);
   public T visit(InvocationExpression invocationExpression, B block);
   public T visit(NewExpression newExpression, B block);
   public T visit(NullExpression nullExpression);
   public T visit(ReadExpression readExpression, B block);
   public T visit(UnaryExpression unaryExpression, B block);

   public T visit(Lvalue lvalue, B block);
   public T visit(LvalueDot lvalueDot, B block);
   public T visit(LvalueId lvalueId, B block);
}
