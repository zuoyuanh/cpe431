package llvm;

import java.util.Set;

public class InterferenceGraphVertexEdgesTuple
{
   private LLVMRegisterType vertex;
   private Set<InterferenceGraphEdge> edges;

   public InterferenceGraphVertexEdgesTuple(LLVMRegisterType vertex, Set<InterferenceGraphEdge> edges)
   {
      this.vertex = vertex;
      this.edges = edges;
   }

   public LLVMRegisterType getVertex()
   {
      return this.vertex;
   }

   public Set<InterferenceGraphEdge> getEdges()
   {
      return this.edges;
   }
}