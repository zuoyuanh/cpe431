package ast;

public class ConditionalStatement
   extends AbstractStatement
{
   private final Expression guard;
   private final Statement thenBlock;
   private final Statement elseBlock;

   public ConditionalStatement(int lineNum, Expression guard,
      Statement thenBlock, Statement elseBlock)
   {
      super(lineNum);
      this.guard = guard;
      this.thenBlock = thenBlock;
      this.elseBlock = elseBlock;
   }

   public Expression getGuard(){
      return this.guard;
   }
   public Statement getThenBlock(){
      return this.thenBlock;
   }
   public Statement getElseBlock(){
      return this.elseBlock;
   }
}
