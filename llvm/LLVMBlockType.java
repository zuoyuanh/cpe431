package llvm;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;

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
   private Set<LocalNumberingExpression> genSet;
   private Set<LocalNumberingExpression> killSet;
   private Set<LocalNumberingExpression> availSet;
   private Set<LLVMRegisterType> armGenSet;
   private Set<LLVMRegisterType> armKillSet;
   private Set<LLVMRegisterType> liveOutSet;

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
      this.genSet = null;
      this.killSet = null;
      this.availSet = null;
      this.armGenSet = null;
      this.armKillSet = null;
      this.liveOutSet = null;
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
      this.genSet = null;
      this.killSet = null;
      this.availSet = null;
      this.armGenSet = null;
      this.armKillSet = null;
      this.liveOutSet = null;
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
      this.genSet = null;
      this.killSet = null;
      this.availSet = null;
      this.armGenSet = null;
      this.armKillSet = null;
      this.liveOutSet = null;
   }

   public static enum Label
   {
      THEN, ELSE, WHILE_LOOP, RETURN, ENTRY, EXIT, JOIN, WHILE_EXIT, PROGRAM
   }

   public Label getLabel()
   {
      return this.label;
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
         if (((LLVMRegisterType)target).getDef() == null) {
            llvmCode.add(0, code);
            return code;
         }
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

   public Set<LocalNumberingExpression> getGenSet()
   {
      return this.genSet;
   }

   public void newGenSet()
   {
      this.genSet = new HashSet<LocalNumberingExpression>();
   }

   public void addToGenSet(LocalNumberingExpression exp)
   {
      if (this.genSet != null) {
         this.genSet.add(exp);
      }
   }

   public Set<LocalNumberingExpression> getKillSet()
   {
      return this.killSet;
   }

   public void newKillSet()
   {
      this.killSet = new HashSet<LocalNumberingExpression>();
   }

   public void addToKillSet(LocalNumberingExpression exp)
   {
      if (this.killSet != null) {
         this.killSet.add(exp);
      }
   }

   public Set<LocalNumberingExpression> getAvailSet()
   {
      return this.availSet;
   }

   public void newAvailSet()
   {
      this.availSet = new HashSet<LocalNumberingExpression>();
   }

   public void setAvailSet(HashSet<LocalNumberingExpression> value)
   {
      this.availSet = value;
   }

   public void addToAvailSet(LocalNumberingExpression exp)
   {
      if (this.availSet != null) {
         this.availSet.add(exp);
      }
   }




   public Set<LLVMRegisterType> getArmGenSet()
   {
      return this.armGenSet;
   }

   public Set<LLVMRegisterType> newArmGenSet()
   {
      this.armGenSet = new HashSet<LLVMRegisterType>();
      return armGenSet;
   }

   public void addToArmGenSet(LLVMRegisterType reg)
   {
      if (this.armGenSet == null) {
         this.armGenSet = new HashSet<LLVMRegisterType>();   
      }
      this.armGenSet.add(reg);
   }

   public Set<LLVMRegisterType> getArmKillSet()
   {
      return this.armKillSet;
   }

   public Set<LLVMRegisterType> newArmKillSet()
   {
      this.armKillSet = new HashSet<LLVMRegisterType>();
      return armKillSet;
   }

   public void addToArmKillSet(LLVMRegisterType reg)
   {
      if (this.armKillSet == null) {
         this.armKillSet = new HashSet<LLVMRegisterType>();
      }
      this.armKillSet.add(reg);
   }

   public Set<LLVMRegisterType> getLiveOutSet()
   {
      return this.liveOutSet;
   }

   public void newLiveOutSet()
   {
      this.liveOutSet = new HashSet<LLVMRegisterType>();
   }

   public void setLiveOutSet(HashSet<LLVMRegisterType> value)
   {
      this.liveOutSet = value;
   }

   public void addToLiveOutSet(LLVMRegisterType reg)
   {
      if (this.liveOutSet == null) {
         this.liveOutSet = new HashSet<LLVMRegisterType>();
      }
      this.liveOutSet.add(reg);
   }
}
