package llvm;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;

public class LLVMBlockType implements LLVMType
{
   private String blockId;
   private Label label;
   private List<String> llvmCode;
   private ArrayList<LLVMBlockType> successors;
   private boolean closed;
   private ArrayList<LLVMBlockType> predecessors;
   private HashMap<String, LLVMType> varTable;
   private HashMap<String, LLVMPhiType> phiTable;
   private boolean sealed;
   private boolean returned;

   public LLVMBlockType(String blockId, Label l)
   {
      this.blockId = blockId;
      this.successors = new ArrayList<LLVMBlockType>();
      this.predecessors = new ArrayList<LLVMBlockType>();
      this.varTable = new HashMap<>();
      this.phiTable = new HashMap<>();
      this.llvmCode = new ArrayList<String>();
      this.closed = false;
      this.sealed = false;
      this.returned = false;
      this.label = l;
   }

   public LLVMBlockType(String blockId, boolean sealed, Label l)
   {
      this.blockId = blockId;
      this.successors = new ArrayList<LLVMBlockType>();
      this.predecessors = new ArrayList<LLVMBlockType>();
      this.varTable = new HashMap<>();
      this.phiTable = new HashMap<>();
      this.llvmCode = new ArrayList<String>();
      this.closed = false;
      this.sealed = sealed;
      this.returned = false;
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
      this.sealed = false;
      this.returned = false;
      this.label = l;
   }

   public static enum Label
   {
      THEN, ELSE, WHILE_LOOP, RETURN, ENTRY, EXIT, JOIN, WHILE_EXIT, PROGRAM
   }

   public void addSuccessor(LLVMBlockType block)
   {
      this.successors.add(block);
   }

   public void setSuccessors(ArrayList<LLVMBlockType> list)
   {
      this.successors = list;
   }
   public void addPredecessor(LLVMBlockType block)
   {
      this.predecessors.add(block);
   }
   public void setPredecessors(ArrayList<LLVMBlockType> list)
   {
      this.predecessors = list;
   }

   public ArrayList<LLVMBlockType> getPredecessors()
   {
      return predecessors;
   }
   public String getBlockId()
   {
      return blockId;
   }

   public ArrayList<LLVMBlockType> getSuccessors()
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
   public boolean isSealed()
   {
      return sealed;
   }
   public void seal()
   {
      sealed = true;
   }

   public HashMap<String, LLVMType> getVarTable()
   {
      return this.varTable;
   }
   public HashMap<String, LLVMPhiType> getPhiTable()
   {
      return this.phiTable;
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

   public boolean getReturned()
   {
      return returned;
   }

   public void setReturned(boolean returned)
   {
      this.returned = returned;
   }
}
