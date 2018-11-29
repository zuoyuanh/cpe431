package llvm;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;

public class LLVMBlockType implements LLVMType
{
   private String blockId;
   private Label label;
   private List<LLVMCode> llvmCode;
   private List<ARMCode> armCode;
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
      this.llvmCode = new ArrayList<LLVMCode>();
      this.armCode = new ArrayList<ARMCode>();
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
      this.llvmCode = new ArrayList<LLVMCode>();
      this.armCode = new ArrayList<ARMCode>();
      this.closed = false;
      this.sealed = sealed;
      this.returned = false;
      this.label = l;
   }

   public LLVMBlockType(String blockId, List<LLVMCode> llvmCode, boolean closed, Label l)
   {
      this.blockId = blockId;
      this.successors = new ArrayList<LLVMBlockType>();
      this.predecessors = new ArrayList<LLVMBlockType>();
      this.varTable = new HashMap<>();
      this.phiTable = new HashMap<>();
      this.llvmCode = llvmCode;
      this.armCode = new ArrayList<ARMCode>();
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
      if (SSAVisitor.generateARM) {
         return "." + blockId;
      }
      return blockId;
   }

   public ArrayList<LLVMBlockType> getSuccessors()
   {
      return successors;
   }

   public List<LLVMCode> getLLVMCode()
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

   public void add(LLVMCode code)
   {
      if (code.isRedirectInstruction()) {
         this.closed = true;
      }
      llvmCode.add(code);
      code.setBlock(this);
   }
   
   public void addARMCode(ARMCode code)
   {
      this.armCode.add(code);
   }

   public void addARMCode(List<ARMCode> codeList)
   {
      for (ARMCode code : codeList) {
         this.armCode.add(code);
      }
   }

   public LLVMCode addPhiRegisterDef(LLVMRegisterType phiDefRegister, LLVMType target)
   {
      LLVMCode code = new LLVMPhiDefCode(phiDefRegister, target);
      if (target instanceof LLVMRegisterType) {
         ((LLVMRegisterType)target).addUse(code);
         for (int i=0; i<llvmCode.size(); i++) {
            if (llvmCode.get(i).getDef() != null && llvmCode.get(i).getDef().equals(target)) {
               llvmCode.add(i + 1, code);
               break;
            }
         }
      } else if (target instanceof LLVMPrimitiveType) {
         this.llvmCode.add(0, code);
      }
      return code;
   }

   public List<ARMCode> getARMCode()
   {
      return this.armCode;
   }

   public void addToFront(LLVMCode code)
   {
      if (code.isRedirectInstruction()) {
         this.closed = true;
      }
      llvmCode.add(0, code);
   }

   public void addToARMFront(ARMCode code)
   {
      armCode.add(0, code);
   }

   public void addToARMFront(List<ARMCode> codeList)
   {
      List<ARMCode> resultList = new ArrayList<ARMCode>();
      for (ARMCode code : codeList) {
         resultList.add(code);
      }
      for (ARMCode code : armCode) {
         resultList.add(code);
      }
      armCode = resultList;
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

   public boolean isEntry()
   {
      return this.label == Label.ENTRY;
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

   public String getTypeRep()
   {
      return "";
   }
}
