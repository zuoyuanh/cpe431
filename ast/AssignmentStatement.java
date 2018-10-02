package ast;

public class AssignmentStatement
   extends AbstractStatement
{
   private final Lvalue target;
   private final Expression source;

   public AssignmentStatement(int lineNum, Lvalue target, Expression source)
   {
      super(lineNum);
      this.target = target;
      this.source = source;
   }
   public Lvalue getTarget() {
      return this.target;
   }
   public Expression getSource() {
      return this.source;
   }
}
