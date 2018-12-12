package llvm;

import ast.Program;
import java.io.File;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.Map;
import java.util.Set;
import java.util.List;
import java.util.HashMap;
import java.util.HashSet;
import java.util.ArrayList;

public class Compiler
{
   public static final int PARAM_REG_NUMS = 4;

   public static boolean generateARM = true;

   private static File output;
   private static BufferedWriter bufferedWriter;

   private static int currentOffset;
   private static int registerCounter = 0;
   private static Set<ARMRegister> allocatedARMRegister;
   private static Map<String, Integer> localVariablesMap;
   private static ARMPushPopCode popCalleeSavedRegisterCode;
   private static ARMBinaryOperationCode resetStackPointerToFpCode;
   private static ARMBinaryOperationCode resetStackPointerToSavedRegsCode;
   private static Map<String, String> originalGlobalVariablesMap = new HashMap<String, String>();
   private static Map<String, String> globalVariablesMap = null;

   public static void start(String filename, Program program, boolean outputLLVM)
   {
      if (outputLLVM) {
         resetCompiler();
         generateARM = false;
         String llvmOutputFileName = filename.substring(0, filename.lastIndexOf('.')) + ".ll";
         setOutput(llvmOutputFileName);
         SSAVisitor visitor = new SSAVisitor();
         visitor.visit(program);
         closeOutput();
      }
      resetCompiler();
      generateARM = true;
      String ssaOutputFileName = filename.substring(0, filename.lastIndexOf('.')) + ".s";
      setOutput(ssaOutputFileName);
      SSAVisitor visitor = new SSAVisitor();
      visitor.visit(program);
      closeOutput();
   }

   private static void setOutput(String outputFileName)
   {
      output = new File(outputFileName);
      try {
         if (output != null) {
            bufferedWriter = new BufferedWriter(new FileWriter(output));
         }
      } catch (Exception e) {
         System.out.println("cannot write to file");
      }
   }

   private static void closeOutput()
   {
      try {
         bufferedWriter.close();
      } catch (Exception e) {
      }
   }

   private static void resetCompiler()
   {
      registerCounter = 0;
   }

   public static LLVMRegisterType createNewRegister(String type)
   {
      String regId = "u" + Integer.toString(registerCounter++);
      return new LLVMRegisterType(type, regId);
   }

   public static String nextNewRegisterId()
   {
      return "u" + Integer.toString(registerCounter++);
   }

   public static LLVMRegisterType createNewPhiDefRegister(String type)
   {
      String regId = "_phi_u" + Integer.toString(registerCounter++);
      return new LLVMRegisterType(type, regId);
   }
   
   private static void initializeLocalVariableMap()
   {
      currentOffset = 4;
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
      currentOffset += 4;
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
      return localVariablesMap.keySet().size() * 4;
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

   public static Map<String, String> getGlobalVariablesMap()
   {
      return globalVariablesMap;
   }

   public static void setGlobalVariablesMap(Map<String, String> map)
   {
      globalVariablesMap = map;
   }

   public static void printStringToFile(String s)
   {
      if (output != null) {
         try {
            bufferedWriter.write(s);
         } catch (Exception e) {
            e.printStackTrace();
            try {
               bufferedWriter.close();
            } catch (Exception exc) {
            }
         }
      }
   }
}