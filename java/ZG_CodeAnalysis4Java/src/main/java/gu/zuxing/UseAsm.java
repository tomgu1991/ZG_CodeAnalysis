package gu.zuxing;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;

public class UseAsm {
    static class MethodCollectorVisitor extends org.objectweb.asm.ClassVisitor {
        public MethodCollectorVisitor() {
            super(Opcodes.ASM9);
        }

        @Override
        public org.objectweb.asm.MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
            System.out.println("Method: " + name + ", Descriptor: " + descriptor);
            return super.visitMethod(access, name, descriptor, signature, exceptions);
        }
    }

    private static final String FILE_PATH =
            "sample_project/target/classes/com/example/TestCase1.class";
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
