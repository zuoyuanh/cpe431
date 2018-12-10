package llvm;

import java.util.List;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Set;
import java.util.Map;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Collections;
import java.util.Comparator;

public class InterferenceGraph
{
   private Set<LLVMRegisterType> vertices;
   private Set<InterferenceGraphEdge> edges;
   private Map<LLVMRegisterType, Set<InterferenceGraphEdge>> adj;

   public InterferenceGraph()
   {
      this.vertices = new HashSet<LLVMRegisterType>();
      this.edges = new HashSet<InterferenceGraphEdge>();
      this.adj = new HashMap<LLVMRegisterType, Set<InterferenceGraphEdge>>();
   }

   public void addEdge(LLVMRegisterType from, LLVMRegisterType to)
   {
      if (ARMCode.systemRegsSet.contains(from) || ARMCode.systemRegsSet.contains(to)) {
         return;
      }
      if (!vertices.contains(from)) {
         vertices.add(from);
      }
      if (!vertices.contains(to)) {
         vertices.add(to);
      }
      if (from.equals(to)) {
         return;
      }
      InterferenceGraphEdge edge1 = new InterferenceGraphEdge(from, to);
      InterferenceGraphEdge edge2 = new InterferenceGraphEdge(to, from);
      edges.add(edge1);
      edges.add(edge2);
      edge1.setReverse(edge2);
      edge2.setReverse(edge1);
      if (adj.containsKey(from)) {
         adj.get(from).add(edge1);
      } else {
         Set<InterferenceGraphEdge> l = new HashSet<InterferenceGraphEdge>();
         l.add(edge1);
         adj.put(from, l);
      }
      if (adj.containsKey(to)) {
         adj.get(to).add(edge2);
      } else {
         Set<InterferenceGraphEdge> l = new HashSet<InterferenceGraphEdge>();
         l.add(edge2);
         adj.put(to, l);
      }
   }

   public Set<LLVMRegisterType> getNeighbors(LLVMRegisterType vertex)
   {
      Set<LLVMRegisterType> neighbors = new HashSet<LLVMRegisterType>();
      for (InterferenceGraphEdge e : adj.get(vertex)) {
         neighbors.add(e.getTargetVertex());
      }
      return neighbors;
   }

   private InterferenceGraphVertexEdgesTuple removeVertex(LLVMRegisterType v)
   {
      if (vertices.contains(v)) {
         vertices.remove(v);
         Set<InterferenceGraphEdge> adjList = adj.get(v);
         if (adjList != null) {
            for (InterferenceGraphEdge e : adjList) {
               edges.remove(e);
               LLVMRegisterType reversedSource = e.getTargetVertex();
               adj.get(reversedSource).remove(e.getReverse());
               edges.remove(e.getReverse());
            }
         }
         adj.remove(v);
         return new InterferenceGraphVertexEdgesTuple(v, adjList);
      }
      return null;
   }

   private LinkedList<InterferenceGraphVertexEdgesTuple> destruct()
   {
      LinkedList<InterferenceGraphVertexEdgesTuple> stack = new LinkedList<InterferenceGraphVertexEdgesTuple>();
      List<LLVMRegisterType> verticesList = new ArrayList<LLVMRegisterType>(this.vertices);
      Collections.sort(verticesList, new Comparator<LLVMRegisterType>() {
         @Override
         public int compare(LLVMRegisterType o1, LLVMRegisterType o2) {
            int constrants1 = -1;
            int constrants2 = -1;
            if (adj.get(o1) != null) {
               constrants1 = adj.get(o1).size();
            }
            if (adj.get(o2) != null) {
               constrants2 = adj.get(o2).size();
            }
            return constrants2 - constrants1;
         }
      });
      for (LLVMRegisterType vertex : verticesList) {
         stack.push(removeVertex(vertex));
      }
      return stack;
   }

   private ARMRegister nextAvailableARMRegister(Set<ARMRegister> allocated, LLVMRegisterType v)
   {
      for (ARMRegister r : ARMCode.availableRegs) {
         if (!allocated.contains(r)) {
            return r;
         }
      }
      Compiler.putLocalVariable(v.getId());
      return null;
   }

   private void rebuild()
   {
      InterferenceGraph newGraph = new InterferenceGraph();
      LinkedList<InterferenceGraphVertexEdgesTuple> stack = destruct();
      while (!stack.isEmpty()) {
         InterferenceGraphVertexEdgesTuple tuple = stack.pop();
         LLVMRegisterType vertex = tuple.getVertex();
         Set<ARMRegister> allocated = new HashSet<ARMRegister>();
         Set<InterferenceGraphEdge> edges = tuple.getEdges();
         if (edges == null || edges.size() == 0) {
            if (vertex instanceof ARMRegister) {
               vertex.allocateARMRegister((ARMRegister)vertex);
               Compiler.addAllocatedARMRegister((ARMRegister)vertex);
            } else {
               ARMRegister r = ARMCode.availableRegs.get(0);
               vertex.allocateARMRegister(r);
               Compiler.addAllocatedARMRegister(r);
            }
            continue;
         }
         for (InterferenceGraphEdge e : edges) {
            newGraph.addEdge(vertex, e.getTargetVertex());
         }
         for (LLVMRegisterType v : newGraph.getNeighbors(vertex)) {
            ARMRegister targetAllocatedReg = v.getAllocatedARMRegister();
            if (v instanceof ARMRegister) {
               allocated.add((ARMRegister)v);
            } else if (targetAllocatedReg != null) {
               allocated.add(targetAllocatedReg);
            }
         }
         ARMRegister allocation = null;
         if (vertex instanceof ARMRegister) {
            allocation = (ARMRegister)vertex;    
         } else {
            allocation = nextAvailableARMRegister(allocated, vertex);
         }
         if (allocation != null) {
            vertex.allocateARMRegister(allocation);
            Compiler.addAllocatedARMRegister(allocation);
         }
      }
   }

   public void allocateRegister()
   {
      rebuild();
   }

   public String toString()
   {
      String result = "";
      result += "----------- Interference Graph -----------\n";
      for (LLVMRegisterType vertex : vertices) {
         if (adj.get(vertex) == null) {
            result += "! " + vertex.toString() + "\n";
            continue;
         }
         result += "* " + vertex.toString() + ": \n";
         for (InterferenceGraphEdge e : adj.get(vertex)) {
            result += "     " + e.getSourceVertex().toString() + " -> " + e.getTargetVertex().toString() + "\n";
         }
         result += "\n";
      }
      result += "-------------- End of Record -------------";
      return result;
   }
}