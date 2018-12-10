package llvm;

import java.util.Map;
import java.util.Set;
import java.util.List;
import java.util.HashMap;
import java.util.HashSet;
import java.util.ArrayList;

public class Compiler
{
   private static int currentOffset;
   private static Set<ARMRegister> allocatedARMRegister;
   private static Map<String, Integer> localVariablesMap;
   private static ARMPushPopCode popCalleeSavedRegisterCode;
   private static ARMBinaryOperationCode resetStackPointerCode;
   
   private static void initializeLocalVariableMap()
   {
      currentOffset = -8;
      localVariablesMap = new HashMap<String, Integer>();
   }

   private static void initializeAllocatedARMRegister()
   {
      allocatedARMRegister = new HashSet<ARMRegister>();
   }

   public static void resetFunction()
   {
      initializeLocalVariableMap();
      initializeAllocatedARMRegister();
      popCalleeSavedRegisterCode = null;
      resetStackPointerCode = null;
   }

   public static void putLocalVariable(String id)
   {
      localVariablesMap.put(id, currentOffset);
      currentOffset -= 8;
   }

   public static int getLocalVariableOffset(String id)
   {
      if (localVariablesMap.containsKey(id)) {
         return localVariablesMap.get(id);
      }
      return -1;
   }

   public static int getLocalVariableStackSize()
   {
      return localVariablesMap.keySet().size() * 8;
   }

   public static void printLocalVariablesMap()
   {
      System.out.println("# Local Var Map: " + localVariablesMap);
   }

   public static void addAllocatedARMRegister(ARMRegister r)
   {
      allocatedARMRegister.add(r);
   }

   public static List<LLVMRegisterType> getCalleeSavedRegisters()
   {
      allocatedARMRegister.removeAll(ARMCode.argRegsSet);
      return new ArrayList<LLVMRegisterType>(allocatedARMRegister);
   }

   public static ARMPushPopCode createPopCalleeSavedRegisterCode()
   {
      popCalleeSavedRegisterCode = new ARMPushPopCode(new ArrayList<LLVMRegisterType>(), ARMPushPopCode.Operator.POP);
      return popCalleeSavedRegisterCode;
   }

   public static ARMPushPopCode getPopCalleeSavedRegisterCode()
   {
      return popCalleeSavedRegisterCode;
   }

   public static ARMBinaryOperationCode createResetStackPointerCode()
   {
      resetStackPointerCode = new ARMBinaryOperationCode(ARMCode.fp, new LLVMPrimitiveType("i32", "4"), ARMCode.sp, ARMBinaryOperationCode.Operator.SUB);
      resetStackPointerCode.disable();
      return resetStackPointerCode;
   }

   public static ARMBinaryOperationCode getResetStackPointerCode()
   {
      return resetStackPointerCode;
   }
}