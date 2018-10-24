package llvm;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;

public class LLVMBlockType implements LLVMType
{
   private String blockId;
   private Label label;
   private List<String> llvmCode;
   private List<LLVMBlockType> successors;
   private boolean closed;
   private List<LLVMBlockType> predecessors;
   private HashMap<String, LLVMType> varTable;
   private HashMap<String, ArrayList<LLVMType>> phiTable;
   private boolean sealed;

   public LLVMBlockType(String blockId, Label l)
   {
      this.blockId = blockId;
      this.successors = new ArrayList<LLVMBlockType>();
      this.predecessors = new ArrayList<LLVMBlockType>();
      this.varTable = new HashMap<>();
      this.phiTable = new HashMap<>();
      this.llvmCode = new ArrayList<String>();
      this.closed = false;
      this.label = l;
   }

   public LLVMBlockType(String blockId, List<String> llvmCode, boolean closed, Label l)
   {
      this.blockId = blockId;
      this.successors = new ArrayList<LLVMBlockType>();
      this.predecessors = new ArrayList<LLVMBlockType>();
      this.varTable = new HashMap<>();
      this.phiTable = new HashMap<>();
      this.llvmCode = llvmCode;
      this.closed = closed;
      this.label = l;
   }

   public static enum Label
   {
      THEN, ELSE, WHILE_LOOP, RETURN, ENTRY, EXIT, JOIN, WHILE_EXIT,PROGRAM
   }

   public void addSuccessor(LLVMBlockType block)
   {
      this.successors.add(block);
   }

   public void setSuccessors(List<LLVMBlockType> list)
   {
      this.successors = list;
   }
   public void addPredecessor(LLVMBlockType block)
   {
      this.predecessors.add(block);
   }
   public void setPredecessors(List<LLVMBlockType> list)
   {
      this.predecessors = list;
   }

   public List<LLVMBlockType> getPredecessors()
   {
      return predecessors;
   }
   public String getBlockId()
   {
      return blockId;
   }

   public List<LLVMBlockType> getSuccessors()
   {
      return successors;
   }

   public List<String> getLLVMCode()
   {
      return llvmCode;
   }

   public boolean isClosed()
   {
      return closed;
   }

   public void close()
   {
      closed = true;
   }

   public HashMap<String, LLVMType> getVarTable(){
      return this.varTable;
   }

   public void add(String code)
   {
      if (code.substring(0, 2).equals("br") || code.substring(0, 3).equals("ret")) {
         this.closed = true;
      }
      llvmCode.add(code);
   }

   public void setLabel(Label l)
   {
      this.label = l;
   }

   public boolean getSealed()
   {
      return sealed;
   }

   public void setSealed(boolean sealed)
   {
      this.sealed = sealed;
   }
   
   public String toString()
   {
      switch (this.label)
      {
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
