package ast;

public class LvalueDot
   implements Lvalue
{
   private final int lineNum;
   private final Expression left;
   private final String id;

   public LvalueDot(int lineNum, Expression left, String id)
   {
      this.lineNum = lineNum;
      this.left = left;
      this.id = id;
   }
   public Expression getLeft(){
      return this.left;
   }
   public String getId(){
      return this.id;
   }
}
