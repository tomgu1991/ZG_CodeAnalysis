package gu.zuxing.bytecode;

import java.util.function.Consumer;

/**
 * 包含绝大多数核心Java字节码指令的示例程序
 * 可通过 javap -c -v -l 指令反编译查看字节码
 */
public class ByteCodeAllInstDemo {
    // 静态字段（触发getstatic/putstatic）
    private static int staticField = 100;
    // 实例字段（触发getfield/putfield）
    private String instanceField = "initial";

    // 1. 基础指令 + 算术指令 + 局部变量操作（iload/istore/iadd等）
    public int basicArithmeticOps(int a, long b, float c, double d) {
        // 局部变量操作（ILOAD/ISTORE/LLOAD/DLOAD等）
        int x = a + 5;          // ILOAD, BIPUSH, IADD, ISTORE
        long y = b - 10L;       // LLOAD, LDC, LSUB, LSTORE
        float z = c * 2.5f;     // FLOAD, LDC, FMUL, FSTORE
        double w = d / 3.0;     // DLOAD, LDC, DDIV, DSTORE

        // 类型转换指令（I2L/F2D等）
        long x2long = (long) x; // ILOAD, I2L, LSTORE
        double z2double = (double) z; // FLOAD, F2D, DSTORE

        // 比较指令（IF_ICMPGT）
        if (x > 0) {
            x += staticField;   // GETSTATIC, IADD
        }

        return x;               // ILOAD, IRETURN
    }

    // 2. 字段操作指令（getstatic/putstatic/getfield/putfield）
    public void fieldOps() {
        // 静态字段操作
        staticField = 200;      // BIPUSH, PUTSTATIC
        int sf = staticField;   // GETSTATIC, ISTORE

        // 实例字段操作
        this.instanceField = "modified"; // LDC, PUTFIELD
        String ifStr = this.instanceField; // GETFIELD, ASTORE
    }

    // 3. 方法调用指令（invokevirtual/invokestatic/invokespecial/invokeinterface/invokedynamic）
    public void methodInvokeOps() {
        // invokevirtual：调用实例方法
        this.instanceMethod();  // ALOAD_0, INVOKEVIRTUAL

        // invokestatic：调用静态方法
        staticMethod();         // INVOKESTATIC

        // invokespecial：调用私有方法/父类构造
        this.privateMethod();   // ALOAD_0, INVOKESPECIAL

        // invokeinterface：调用接口方法
        MyInterface myInterface = new MyInterfaceImpl();
        myInterface.sayHello("interface"); // ALOAD, LDC, INVOKEINTERFACE

        // invokedynamic：Lambda表达式（JDK8+）
        Consumer<String> consumer = (msg) -> System.out.println("dynamic: " + msg);
        consumer.accept("lambda"); // ALOAD, LDC, INVOKEINTERFACE
    }

    // 私有方法（供invokespecial调用）
    private void privateMethod() {
        System.out.println("private method");
    }

    // 实例方法（供invokevirtual调用）
    public void instanceMethod() {
        System.out.println("instance method");
    }

    // 静态方法（供invokestatic调用）
    public static void staticMethod() {
        System.out.println("static method");
    }

    // 4. 数组操作指令（newarray/anewarray/arraylength/iaload/iastore等）
    public void arrayOps() {
        // newarray：基本类型数组
        int[] intArray = new int[5]; // BIPUSH, NEWARRAY
        intArray[0] = 10;            // ALOAD, ICONST_0, BIPUSH, IASTORE
        int val = intArray[0];       // ALOAD, ICONST_0, IALOAD, ISTORE

        // anewarray：引用类型数组
        String[] strArray = new String[3]; // ICONST_3, ANEWARRAY
        strArray[1] = "array";             // ALOAD, ICONST_1, LDC, AASTORE
        String strVal = strArray[1];       // ALOAD, ICONST_1, AALOAD, ASTORE

        // arraylength：获取数组长度
        int intLen = intArray.length;      // ALOAD, ARRAYLENGTH, ISTORE
        int strLen = strArray.length;      // ALOAD, ARRAYLENGTH, ISTORE
    }

    // 5. 异常处理指令（new/athrow/checkcast/instanceof）
    public void exceptionOps() {
        Object obj = "test string";

        // instanceof：类型检查
        boolean isString = obj instanceof String; // ALOAD, INSTANCEOF, ISTORE
        boolean isInteger = obj instanceof Integer; // ALOAD, INSTANCEOF, ISTORE

        // checkcast：类型转换检查
        try {
            String str = (String) obj; // ALOAD, CHECKCAST, ASTORE
            Integer num = (Integer) obj; // ALOAD, CHECKCAST（失败触发athrow）
        } catch (ClassCastException e) {
            // athrow：异常抛出（由checkcast失败触发）
            System.out.println("cast failed: " + e.getMessage());
        }

        // 手动抛出异常（new/athrow）
        try {
            throw new RuntimeException("manual exception"); // NEW, DUP, LDC, INVOKESPECIAL, ATHROW
        } catch (RuntimeException e) {
            System.out.println("catch exception: " + e.getMessage());
        }
    }

    // 6. 同步指令（monitorenter/monitorexit）
    public void syncOps() {
        Object lock = new Object(); // NEW, DUP, INVOKESPECIAL, ASTORE

        synchronized (lock) {       // ALOAD, MONITORENTER
            System.out.println("in sync block");
        }                           // MONITOREXIT
    }

    // 7. 跳转指令（goto/ifeq/ifne/tableswitch）
    public int jumpOps(int num) {
        // ifeq/ifne：条件跳转
        if (num == 0) {            // ILOAD, IFNE
            return 1;              // ICONST_1, IRETURN
        } else if (num == 1) {     // ILOAD, IF_ICMPNE
            return 2;              // ICONST_2, IRETURN
        }

        // tableswitch：表跳转
        int result;
        switch (num) {
            case 0: result = 0; break;
            case 1: result = 1; break;
            case 2: result = 2; break;
            default: result = -1;
        } // TABLESWITCH 指令

        switch (num) {
            case 0: result++; break;
            case 5: result += 5; break;
            case 9: result += 9; break;
            default: result += 100;
        }

        // goto：无条件跳转（循环中）
        int count = 0;
        while (true) {            // GOTO
            count++;
            if (count > 3) {      // ILOAD, BIPUSH, IF_ICMPLE
                break;
            }
        }

        return result;
    }

    // 内部接口（供invokeinterface调用）
    interface MyInterface {
        void sayHello(String msg);
    }

    // 接口实现类
    static class MyInterfaceImpl implements MyInterface {
        @Override
        public void sayHello(String msg) {
            System.out.println("hello: " + msg);
        }
    }

    // 主方法（触发所有指令）
    public static void main(String[] args) {
        ByteCodeAllInstDemo demo = new ByteCodeAllInstDemo(); // NEW, DUP, INVOKESPECIAL, ASTORE

        demo.basicArithmeticOps(1, 2L, 3.0f, 4.0);
        demo.fieldOps();
        demo.methodInvokeOps();
        demo.arrayOps();
        demo.exceptionOps();
        demo.syncOps();
        demo.jumpOps(2);
    }
}