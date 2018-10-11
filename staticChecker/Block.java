package staticChecker;
import java.util.List;
import java.util.ArrayList;

public class Block{
   private final Label label;
   private List<Block> successors;

   public Block(Label label){
      this.label = label;
      this.successors = new ArrayList<Block>();
   }

   public static enum Label{
      THEN, ELSE, WHILE_LOOP, RETURN, ENTRY, EXIT, JOIN, WHILE_EXIT,PROGRAM
   }

   public Label getLabel(){
      return this.label;
   }

   public void addSuccessor(Block block){
      this.successors.add(block);
   }

   public void setSuccessors(List<Block> list){
      this.successors = list;
   }

   public List<Block> getSuccessors(){
      return this.successors;
   }

   public String toString (){
      switch (this.label){
         case THEN: 
            return "Then";
         case ELSE: 
            return "Else";
         case WHILE_LOOP:
            return "While loop";
         case RETURN: 
            return "Return";
         case ENTRY: 
            return "Entry";
         case EXIT: 
            return "Exit";
         case JOIN: 
            return "Join";
         case WHILE_EXIT: 
            return "While exit";
         case PROGRAM:
            return "Program";
         default: 
            return null;
      }
   }
}


