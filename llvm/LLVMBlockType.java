package llvm;

import java.util.List;
import java.util.ArrayList;

public class LLVMBlockType implements LLVMType
{
   private String blockId;
   private List<String> llvmCode;
   private boolean closed;

   public LLVMBlockType(String blockId)
   {
      this.blockId = blockId;
      this.llvmCode = new ArrayList<String>();
      this.closed = false;
   }

   public LLVMBlockType(String blockId, List<String> llvmCode, boolean closed)
   {
      this.blockId = blockId;
      this.llvmCode = llvmCode;
      this.closed = closed;
   }

   public String getBlockId()
   {
      return blockId;
   }

   public List<String> getLLVMCode()
   {
      return llvmCode;
   }

   public boolean isClosed()
   {
      return closed;
   }

   public void add(String code)
   {
      if (code.substring(0, 2).equals("br") || code.substring(0, 3).equals("ret")) {
         this.closed = true;
      }
      llvmCode.add(code);
   }
   
}