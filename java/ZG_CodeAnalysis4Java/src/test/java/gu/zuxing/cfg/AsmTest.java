package gu.zuxing.cfg;

import org.junit.jupiter.api.Test;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class AsmTest {

    @Test
    public void testAsmMethodCFG() throws IOException {
        // Load the class file bytes
        String classFilePath = "target/classes/gu/zuxing/bytecode/SimpleCase.class";
        byte[] classBytes = Files.readAllBytes(Paths.get(classFilePath));

        // Parse the class using ASM
        ClassReader classReader = new ClassReader(classBytes);
        ClassNode classNode = new ClassNode();
        classReader.accept(classNode, ClassReader.EXPAND_FRAMES);
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
        classNode.accept(cw);

        // Iterate through methods and build CFG for each
        List<MethodNode> methods = classNode.methods;
        for (MethodNode method : methods) {
            AsmMethodCFG methodCFG = new AsmMethodCFG(method);

            // Dump the CFG for debugging purposes
            System.out.println("\n\n");
            methodCFG.dumpCFG(true, false);

            // Perform some basic assertions
            assertNotNull(methodCFG.entryBlock, "Entry block should not be null");
            assertFalse(methodCFG.allBasicBlocks.isEmpty(), "Basic blocks should not be empty");
        }
    }
}
