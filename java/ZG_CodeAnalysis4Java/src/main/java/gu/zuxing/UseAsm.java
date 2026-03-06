package gu.zuxing;

import org.objectweb.asm.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UseAsm {
    // 数据载体
    static class CallSite {
        final String owner, name, desc;
        final int opcode;
        final Integer line; // 可为 null
        CallSite(String owner, String name, String desc, int opcode, Integer line) {
            this.owner = owner; this.name = name; this.desc = desc; this.opcode = opcode; this.line = line;
        }
    }

    // 在 ClassVisitor 内部或外部的 Map 存储
    public static Map<String, List<CallSite>> callsByMethod = new HashMap<>();

    static class MethodCollectorVisitor extends org.objectweb.asm.ClassVisitor {
        public MethodCollectorVisitor() {
            super(Opcodes.ASM9);
        }

        // visitMethod 返回的 MethodVisitor 核心部分
        @Override
        public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
            final String methodKey = name + descriptor; // 或 owner + "#" + name + descriptor
            System.out.println("Method: " + name + ", Descriptor: " + descriptor);
            return new MethodVisitor(Opcodes.ASM9) {
                Integer currentLine = null;
                @Override
                public void visitLineNumber(int line, Label start) {
                    currentLine = line;
                    super.visitLineNumber(line, start);
                }
                @Override
                public void visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface) {
                    callsByMethod.computeIfAbsent(methodKey, k -> new ArrayList<>())
                            .add(new CallSite(owner, name, descriptor, opcode, currentLine));
                    super.visitMethodInsn(opcode, owner, name, descriptor, isInterface);
                }
                @Override
                public void visitInvokeDynamicInsn(String name, String descriptor, Handle bsm, Object... bsmArgs) {
                    // invokedynamic: name + descriptor + bootstrap info
                    callsByMethod.computeIfAbsent(methodKey, k -> new ArrayList<>())
                            .add(new CallSite(null /*owner*/, name, descriptor, Opcodes.INVOKEDYNAMIC, currentLine));
                    super.visitInvokeDynamicInsn(name, descriptor, bsm, bsmArgs);
                }
            };
        }

    }

    private static final String FILE_PATH =
//            "sample_project/target/classes/com/example/TestCase1.class";
            "target/classes/gu/zuxing/bytecode/HelloByteCode.class";
    public static void main(String[] args) {
        System.out.println("Hello, ASM!");
        byte[] classBytes = loadClassBytes(FILE_PATH);
        if (classBytes != null) {
            System.out.println("Class file loaded successfully, size: " + classBytes.length + " bytes");
            // 这里可以添加使用 ASM 进行字节码分析或修改的示例代码
            MethodCollectorVisitor methodCollector = new MethodCollectorVisitor();
            ClassReader cr = new ClassReader(classBytes);
            cr.accept(methodCollector, 0);
            System.out.println("Finish visiting.");
            for (Map.Entry<String, List<CallSite>> entry : callsByMethod.entrySet()) {
                System.out.println("Method: " + entry.getKey());
                for (CallSite call : entry.getValue()) {
                    System.out.println("  Call: " + call.owner + "." + call.name + call.desc + " at line " + call.line);
                }
            }

        } else {
            System.out.println("Failed to load class file.");
        }
    }

    public static byte[] loadClassBytes(String fileName) {
        try {
            return java.nio.file.Files.readAllBytes(java.nio.file.Paths.get(fileName));
        } catch (java.io.IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
