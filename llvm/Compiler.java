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
   private static ARMBinaryOperationCode resetStackPointerToFpCode;
   private static ARMBinaryOperationCode resetStackPointerToSavedRegsCode;
   private static Map<String, String> originalGlobalVariablesMap = new HashMap<String, String>();
   
   private static void initializeLocalVariableMap()
   {
      currentOffset = 8;
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
      resetStackPointerToFpCode = null;
      resetStackPointerToSavedRegsCode = null;
   }

   public static void putLocalVariable(String id)
   {
      localVariablesMap.put(id, currentOffset);
      currentOffset += 8;
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

   public static ARMBinaryOperationCode createResetStackPointerToFpCode()
   {
      resetStackPointerToFpCode = new ARMBinaryOperationCode(ARMCode.fp, new LLVMPrimitiveType("i32", "4"), ARMCode.sp, ARMBinaryOperationCode.Operator.SUB);
      resetStackPointerToFpCode.disable();
      return resetStackPointerToFpCode;
   }

   public static ARMBinaryOperationCode getResetStackPointerToFpCode()
   {
      return resetStackPointerToFpCode;
   }

   public static ARMBinaryOperationCode createResetStackPointerToSavedRegsCode()
   {
      resetStackPointerToSavedRegsCode = new ARMBinaryOperationCode(ARMCode.sp, new LLVMPrimitiveType("i32", "4"), ARMCode.sp, ARMBinaryOperationCode.Operator.ADD);
      resetStackPointerToSavedRegsCode.disable();
      return resetStackPointerToSavedRegsCode;
   }

   public static ARMBinaryOperationCode getResetStackPointerToSavedRegsCode()
   {
      return resetStackPointerToSavedRegsCode;
   }

   public static Map<String, String> getOriginalGlobalVariablesMap()
   {
      return originalGlobalVariablesMap;
   }

   public static void putIntoOriginalGlobalVariablesMap(String key, String value)
   {
      originalGlobalVariablesMap.put(key, value);
   }
}