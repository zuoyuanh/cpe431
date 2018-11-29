package llvm;

import java.util.List;
import java.util.ArrayList;
import ast.Declaration;

public class LLVMCallCode extends LLVMCode
{
   private String name;
   private List<LLVMType> args;
   private List<Declaration> params;
   private LLVMType resultReg;
   private boolean isVoid;

   public LLVMCallCode(String name, List<Declaration> params, List<LLVMType> args, LLVMType resultReg)
   {
      super();
      this.name = name;
      this.args = args;
      this.params = params;
      this.resultReg = resultReg;
      this.isVoid = false;
   }

   public LLVMCallCode(String name, List<Declaration> params, List<LLVMType> args)
   {
      super();
      this.name = name;
      this.args = args;
      this.params = params;
      this.isVoid = true;
      this.resultReg = null;
   }

   private String getCallsArgsRep()
   {
      String callArgsRep = "(";
      for (int i=0; i<params.size(); i++) {
         Declaration param = params.get(i);
         LLVMType arg = args.get(i);
         String paramTypeRep = getTypeLLVMRepresentation(param.getType());
         LLVMType opnd = getOperand(arg, paramTypeRep);
         callArgsRep += opnd.getTypeRep() + " " + opnd + ", ";
      }
      if (callArgsRep.length() > 2 && callArgsRep.charAt(callArgsRep.length()-2) == ',') {
         callArgsRep = callArgsRep.substring(0, callArgsRep.length()-2);
      }
      return callArgsRep + ")";
   }

   public String toString()
   {
      String callsArgsRep = getCallsArgsRep();
      if (!isVoid) {
         return getConversions() + "\t" + resultReg + " = call " + resultReg.getTypeRep() + " @" + name.trim() + callsArgsRep + "\n";
      }
      return getConversions() + "\tcall void @" + name.trim() + callsArgsRep + "\n";
   }

   public void replaceRegister(LLVMType oldVal, LLVMType newVal)
   {
      for (int i=0; i<args.size(); i++) {
         if (args.get(i).equals(oldVal)) {
            args.set(i, newVal);
            if (newVal instanceof LLVMRegisterType) {
               ((LLVMRegisterType)newVal).addUse(this);
            }
         }
      }
   }

   public List<LLVMRegisterType> dependenciesList()
   {
      List<LLVMRegisterType> results = new ArrayList<LLVMRegisterType>();
      for (LLVMType arg : args) {
         if (arg instanceof LLVMRegisterType) {
            results.add((LLVMRegisterType)arg);
         }
      } 
      return results;
   }

   public LLVMType getDef()
   {
      return resultReg;
   }

   public List<ARMCode> generateArmCode()
   {
      List<LLVMRegisterType> pushList = new ArrayList<LLVMRegisterType>();
      for (int i=args.size()-1; i>=0; i--) {
         if (i < SSAVisitor.PARAM_REG_NUMS) {
            armCode.add(new ARMMoveCode(ARMCode.argRegs[i], args.get(i), ARMMoveCode.Operator.MOV));
         } else {
            LLVMType argType = args.get(i);
            if (argType instanceof LLVMPrimitiveType) {
               LLVMRegisterType tmpReg = SSAVisitor.createNewRegister(((LLVMPrimitiveType)argType).getTypeRep());
               armCode.add(new ARMMoveCode(tmpReg, argType, ARMMoveCode.Operator.MOV));
               pushList.add(tmpReg);
            } else {
               pushList.add((LLVMRegisterType)argType);
            }
         }
      }
      if (pushList.size() > 0) {
         armCode.add(new ARMPushPopCode(pushList, ARMPushPopCode.Operator.PUSH));
      }
      armCode.add(new ARMBranchCode(name, ARMBranchCode.Operator.BL));
      if (!isVoid && (resultReg != null)) {
         armCode.add(new ARMMoveCode((LLVMRegisterType)resultReg, ARMCode.argRegs[0], ARMMoveCode.Operator.MOV));
      }
      return armCode;
   } 
}