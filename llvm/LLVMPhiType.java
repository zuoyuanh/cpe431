package llvm;
import java.util.ArrayList;
public class LLVMPhiType implements LLVMType {
   private ArrayList<LLVMType> phiOperands;
   private LLVMBlockType block;
   public LLVMPhiType(LLVMBlockType b){
      block = b;
      phiOperands =  new ArrayList<LLVMType>();
   }
   public ArrayList<LLVMType> getPhiOperands(){
      return this.phiOperands;
   }
   public void addPhiOperand(LLVMType op){
      phiOperands.add(op);
   }
   public LLVMBlockType getBlock(){
      return this.block;
   }
}
