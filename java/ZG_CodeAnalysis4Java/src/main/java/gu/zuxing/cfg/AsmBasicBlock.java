package gu.zuxing.cfg;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LineNumberNode;

import java.util.ArrayList;
import java.util.List;

public class AsmBasicBlock {
    // 块的唯一标识，通常用起始指令的 Offset
    public int id;
    public int line;
    public LabelNode label;

    // 该块包含的指令列表 (ASM 的 AbstractInsnNode)
    public List<AbstractInsnNode> instructions = new ArrayList<>();

    // 出口边：该块指向的后续块
    public List<AsmBasicBlock> successors = new ArrayList<>();

    // 入口边：哪些块指向了当前块 (用于反向分析)
    public List<AsmBasicBlock> predecessors = new ArrayList<>();

    // 异常处理：该块如果抛出异常，会跳向哪个 Handler 块
    public List<AsmBasicBlock> exceptionHandlers = new ArrayList<>();

    public AsmBasicBlock(int id, LabelNode label) {
        this.id = id;
        this.label = label;
    }

    public void addInstruction(AbstractInsnNode insn) {
        if (insn instanceof LineNumberNode lineNumberNode) {
            line = lineNumberNode.line;
        }
        instructions.add(insn);
    }

    public void addSuccessor(AsmBasicBlock block) {
        successors.add(block);
        block.predecessors.add(this);
    }

    public void addExceptionHandler(AsmBasicBlock handler) {
        exceptionHandlers.add(handler);
    }

    public void dumpBlock(boolean bbDtail) {
        System.out.println("\nBasic Block ID: " + id + "; Line: " + line);
//        System.out.println("Label: " + (label != null ? label.getLabel() : "null"));
        System.out.println("Successors-size: " + successors.size());
        for (AsmBasicBlock block : successors) {
            System.out.println("  -> Block ID: " + block.id + "; Line: " + block.line);
        }
        if (!bbDtail) {
            return;
        }
        System.out.println("Instructions:");
        for (AbstractInsnNode insn : instructions) {
            System.out.println("  " + insn.getOpcode());
        }
        System.out.println("Predecessors: " + predecessors.size());
        System.out.println("Exception Handlers: " + exceptionHandlers.size());
    }
}