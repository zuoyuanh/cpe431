import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

import java.io.*;
import javax.json.JsonValue;
import staticChecker.*;
// import llvm.StackLLVMVisitor;
import llvm.SSAVisitor;
public class MiniCompiler
{
   private static boolean printStackLLVMProgram = false;
   private static boolean printLLVMProgram = false;

   public static void main(String[] args)
   {
      parseParameters(args);

      CommonTokenStream tokens = new CommonTokenStream(createLexer());
      MiniParser parser = new MiniParser(tokens);
      ParseTree tree = parser.program();

      if (parser.getNumberOfSyntaxErrors() == 0)
      {
         /*
            This visitor will create a JSON representation of the AST.
            This is primarily intended to allow use of languages other
            than Java.  The parser can thusly be used to generate JSON
            and the next phase of the compiler can read the JSON to build
            a language-specific AST representation.
         */
         
         MiniToJsonVisitor jsonVisitor = new MiniToJsonVisitor();
         JsonValue json = jsonVisitor.visit(tree);
         System.out.println(json);
         
         /*
            This visitor will build an object representation of the AST
            in Java using the provided classes.
         */
         MiniToAstProgramVisitor programVisitor =
            new MiniToAstProgramVisitor();
         ast.Program program = programVisitor.visit(tree);
         //TypeVisitor typeVisitor = new TypeVisitor();
         //typeVisitor.visit(program);
         
         String llvmOutputFileName = _inputFile.substring(0, _inputFile.lastIndexOf('.')) + ".ll";
         File f = null;

         if (printLLVMProgram) {
            f = new File(llvmOutputFileName);
            System.out.println("--- Generating LLVM Code ---");
         }

         SSAVisitor ssaLLVMVisitor = new SSAVisitor(f);
         ssaLLVMVisitor.visit(program);

         /* f = null;
         if (printStackLLVMProgram) {
            f = new File(llvmOutputFileName);
            System.out.println("--- Generating Stack LLVM Code ---");
         }
         StackLLVMVisitor llvmVisitor = new StackLLVMVisitor(f);
         llvmVisitor.visit(program);
         System.out.println("\n--- Showing CFG ---");
         for (llvm.LLVMBlockType b : llvmVisitor.getGlobalBlockList()){
            System.out.println("block: "+ b.toString());
            for (llvm.LLVMBlockType s : b.getSuccessors())
               System.out.println("successor: "+ s.toString());
         } */


         /* System.out.println("--- Generate CFG ---");
         CFGGenerator cfg = new CFGGenerator();
         cfg.visit(program, null, null);
         for (Block b : cfg.blockList){
            System.out.println("block: "+ b.toString());
            for (Block s : b.getSuccessors())
               System.out.println("successor: "+ s.toString());
         } */

         System.out.println();
         /*
         System.out.println("--- Return Check ---");
         ReturnVisitor returnVisitor = new ReturnVisitor();
         returnVisitor.visit(program);
         */

      }
   }

   private static String _inputFile = null;

   private static void parseParameters(String [] args)
   {
      for (int i = 0; i < args.length; i++)
      {
         if (args[i].charAt(0) == '-')
         {
            if (args[i].equals("-llvm")) {
               printLLVMProgram = true;
            } else if (args[i].equals("-stack")) {
               printStackLLVMProgram = true;
            } else {
               System.err.println("unexpected option: " + args[i]);
               System.exit(1);
            }
         }
         else if (_inputFile != null)
         {
            System.err.println("too many files specified");
            System.exit(1);
         }
         else
         {
            _inputFile = args[i];
         }
      }
   }

   private static void error(String msg)
   {
      System.err.println(msg);
      System.exit(1);
   }

   private static MiniLexer createLexer()
   {
      try
      {
         CharStream input;
         if (_inputFile == null)
         {
            input = CharStreams.fromStream(System.in);
         }
         else
         {
            input = CharStreams.fromFileName(_inputFile);
         }
         return new MiniLexer(input);
      }
      catch (java.io.IOException e)
      {
         System.err.println("file not found: " + _inputFile);
         System.exit(1);
         return null;
      }
   }
}
