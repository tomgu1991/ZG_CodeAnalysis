package gu.zuxing.cfg;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AsmMethodCFG {
    public MethodNode method;

    public String name;
    public String descriptor;
    public int maxStack;
    public int maxLocals;
    public List<LocalVariableNode> localVariables;
    private List<TryCatchBlockNode> tryCatchBlocks;
    public List<AsmBasicBlock> allBasicBlocks = new ArrayList<>();
    public Map<LabelNode, AsmBasicBlock> labelToBasicBlock = new HashMap<>();
    public AsmBasicBlock entryBlock = null; // 方法入口块，我们用localvariable表

    public AsmMethodCFG(MethodNode method) {
        this.method = method;
        initProperties();
        buildCFG();
    }

    private void initProperties() {
        // 1. 记录方法的基本信息（名称、描述符、访问权限等）
        this.name = method.name;
        this.descriptor = method.desc;
        maxStack = method.maxStack;
        maxLocals = method.maxLocals;
        // 2. 记录localVariables、tryCatchBlocks等相关信息
        localVariables = method.localVariables;
        tryCatchBlocks = method.tryCatchBlocks;
    }

    private void buildCFG() {
        // 1. 划分基本块：遍历 method.instructions，识别基本块边界（标签、跳转指令等）
        buildAllBasicBlocks();
        // 2. 构建基本块之间的边：根据跳转指令和顺序执行关系连接基本块
        // TODO： 处理edge
        linkBasicBlocks();
        // 3. 处理异常边：根据 method.tryCatchBlocks 添加异常处理边
    }

    private void linkBasicBlocks() {
        // 1. 遍历所有基本块，分析每个块的最后一条指令
        // 2. 根据指令类型（跳转、条件跳转、返回等）来确定后续块，并建立边连接
        for (int i = 0; i < allBasicBlocks.size(); i++) {
            AsmBasicBlock block = allBasicBlocks.get(i);
            if (block.instructions.isEmpty()) {
                continue;
            }
            // 注意这里面要处理块内的每条指令，因为可能存在块内跳转（如switch）或者异常边
            // 比如：if (x > 0 && y < 0)
            for (AbstractInsnNode insn : block.instructions) {
                // 根据指令类型添加后续块
                if (insn instanceof JumpInsnNode jumpInsn) {
                    // 跳转指令：添加目标块
                    AsmBasicBlock targetBlock = labelToBasicBlock.get(jumpInsn.label);
                    if (targetBlock != null) {
                        block.addSuccessor(targetBlock);
                    }
                    // 如果是条件跳转，还需要添加fall-through块（如果不是返回）
                    if (jumpInsn.getOpcode() != Opcodes.GOTO) {
                        // 条件跳转：添加下一个块
                        if (insn == block.instructions.getLast() && i + 1 < allBasicBlocks.size()) {
                            AsmBasicBlock nextBlock = allBasicBlocks.get(i + 1);
                            block.addSuccessor(nextBlock);
                        }
                    }
                } else if (insn.getOpcode() >= Opcodes.IRETURN && insn.getOpcode() <= Opcodes.RETURN) {
                    // 返回指令：不添加后续块

                } else if (insn instanceof TableSwitchInsnNode tableSwitchInsn) {
                    // switch指令：添加所有case块和default块
                    for (LabelNode label : tableSwitchInsn.labels) {
                        AsmBasicBlock targetBlock = labelToBasicBlock.get(label);
                        if (targetBlock != null) {
                            block.addSuccessor(targetBlock);
                        }
                    }
                    AsmBasicBlock defaultBlock = labelToBasicBlock.get(tableSwitchInsn.dflt);
                    if (defaultBlock != null) {
                        block.addSuccessor(defaultBlock);
                    }
                } else if (insn instanceof LookupSwitchInsnNode lookupSwitchInsnNode) {
                    for (LabelNode label : lookupSwitchInsnNode.labels) {
                        AsmBasicBlock targetBlock = labelToBasicBlock.get(label);
                        if (targetBlock != null) {
                            block.addSuccessor(targetBlock);
                        }
                    }
                } else {
                    // 顺序执行：添加下一个块
                    if (insn == block.instructions.getLast() && i + 1 < allBasicBlocks.size()) {
                        AsmBasicBlock nextBlock = allBasicBlocks.get(i + 1);
                        block.addSuccessor(nextBlock);
                    }
                }
            }

        }
    }

    private void buildAllBasicBlocks() {
        // 1. 遍历 method.instructions，识别label来构建所有的基本块
        // 2. 每遇到一个新的 label，就创建一个新的 AsmBasicBlock，并将其加入 allBasicBlocks 列表
        AsmBasicBlock currentBlock = null;
        for (AbstractInsnNode insn : method.instructions.toArray()) {
            if (insn instanceof LabelNode labelNode) {
                AsmBasicBlock basicBlock = new AsmBasicBlock(allBasicBlocks.size(), labelNode);
                allBasicBlocks.add(basicBlock);
                labelToBasicBlock.put(labelNode, basicBlock);
                currentBlock = basicBlock;
            } else {
                assert currentBlock != null;
                currentBlock.addInstruction(insn);
            }
        }
        entryBlock = allBasicBlocks.getFirst();
    }

    public void dumpCFG(boolean cfgDetail, boolean bbDetail) {
        // 1. 打印方法的基本信息（名称、描述符、访问权限等）
        System.out.println("Method Name: " + name);
        System.out.println("Descriptor: " + descriptor);
        System.out.println("Max Stack: " + maxStack);
        System.out.println("Max Locals: " + maxLocals);
        // 2. 打印每个基本块的信息（块ID、包含的指令）
        System.out.println("Basic Blocks: " + allBasicBlocks.size());
        if (!cfgDetail) {
            return;
        }
        for (AsmBasicBlock basicBlock : allBasicBlocks) {
            basicBlock.dumpBlock(bbDetail);
        }

    }
}
