package staticChecker;

import ast.*;

public class TypeVisitor implements AstVisitor<Type>{
   public Type visit (Program program){
      System.out.println("visiting program");
      return new VoidType(); 
   }


}
