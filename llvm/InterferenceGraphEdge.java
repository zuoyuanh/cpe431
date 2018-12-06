package llvm;

public class InterferenceGraphEdge
{
   private LLVMRegisterType from;
   private LLVMRegisterType to;
   private InterferenceGraphEdge reverse;

   public InterferenceGraphEdge(LLVMRegisterType from, LLVMRegisterType to) {
      this.from = from;
      this.to = to;
   }

   public LLVMRegisterType getSourceVertex()
   {
      return this.from;
   }

   public LLVMRegisterType getTargetVertex()
   {
      return this.to;
   }

   public InterferenceGraphEdge getReverse()
   {
      return reverse;
   }

   public void setReverse(InterferenceGraphEdge reverse)
   {
      this.reverse = reverse;
   }

   public int hashCode()
   {
      return from.hashCode() * 1000 + to.hashCode();
   }

   public boolean equals(Object other)
   {
      if ((other == null) || (getClass() != other.getClass())) {
         return false;
      } else {
         return from.equals(((InterferenceGraphEdge)other).getSourceVertex()) && to.equals(((InterferenceGraphEdge)other).getTargetVertex());
      }
   }
}