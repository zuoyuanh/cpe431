package staticChecker;

import ast.*; 

public interface AstVisitor<T>{
   public T visit (Program program);
   //public <T> visit (Assign assign);
}
