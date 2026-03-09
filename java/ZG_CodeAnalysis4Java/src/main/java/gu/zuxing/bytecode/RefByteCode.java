package gu.zuxing.bytecode;

import java.util.function.Consumer;

// 定义接口（触发invokeinterface指令）
interface MyInterface {
    void sayHello(String msg);
}

class RefByteCode {
    // 静态变量（触发getstatic/putstatic）
    private static String staticField = "初始静态值";
    // 实例变量（触发getfield/putfield）
    private String instanceField = "初始实例值";

    // 私有方法（触发invokespecial）
    private void privateMethod() {
        System.out.println("调用私有方法");
    }

    // 静态方法（触发invokestatic）
    public static void staticMethod() {
        System.out.println("调用静态方法");
    }

    // 构造方法（触发invokespecial：调用父类构造）
    public RefByteCode() {
        super(); // 调用Object构造方法，触发invokespecial
        this.privateMethod(); // 调用私有方法，触发invokespecial
    }

    // 实例方法（触发invokevirtual）
    public void instanceMethod() {
        System.out.println("调用实例方法");
    }

    public static void main(String[] args) {
        // ========== 1. getstatic/putstatic（静态域读写） ==========
        // putstatic：给静态变量赋值
        RefByteCode.staticField = "修改后的静态值";
        // getstatic：获取静态变量并打印（配合invokevirtual）
        System.out.println("静态变量值：" + RefByteCode.staticField);

        // ========== 2. getfield/putfield（实例域读写） ==========
        RefByteCode obj = new RefByteCode(); // new：创建对象（触发new）
        // putfield：给实例变量赋值
        obj.instanceField = "修改后的实例值";
        // getfield：获取实例变量并打印
        System.out.println("实例变量值：" + obj.instanceField);

        // ========== 3. 方法调用指令 ==========
        obj.instanceMethod(); // invokevirtual：调用实例方法
        RefByteCode.staticMethod(); // invokestatic：调用静态方法

        // invokeinterface：调用接口方法
        MyInterface myInterface = new MyInterface() {
            @Override
            public void sayHello(String msg) {
                System.out.println("接口方法：" + msg);
            }
        };
        myInterface.sayHello("Hello Interface");

        // invokedynamic：Lambda表达式触发（Java 8+）
        Consumer<String> consumer = (msg) -> System.out.println("动态方法：" + msg);
        consumer.accept("Hello Dynamic");

        // ========== 4. 数组相关指令 ==========
        // newarray：创建基本类型数组（int[]）
        int[] intArray = new int[5];
        // anewarray：创建引用类型数组（String[]）
        String[] strArray = new String[3];
        // arraylength：获取数组长度
        int intArrLen = intArray.length;
        int strArrLen = strArray.length;
        System.out.println("int数组长度：" + intArrLen + "，String数组长度：" + strArrLen);

        // ========== 5. 异常/类型检查指令 ==========
        // instanceof：检查对象类型
        Object testObj = "测试字符串";
        boolean isString = testObj instanceof String; // 触发instanceof，压入1
        boolean isInteger = testObj instanceof Integer; // 触发instanceof，压入0
        System.out.println("是否是String：" + isString + "，是否是Integer：" + isInteger);

        // checkcast：类型转换检查
        try {
            String str = (String) testObj; // 合法转换，checkcast通过
            System.out.println("类型转换成功：" + str);

            Integer num = (Integer) testObj; // 非法转换，checkcast抛出ClassCastException
        } catch (ClassCastException e) {
            // athrow：抛出异常（catch捕获，底层由athrow触发）
            System.out.println("类型转换失败：" + e.getMessage());
        }

        // 手动抛出异常（触发athrow）
        try {
            throw new RuntimeException("手动抛出异常");
        } catch (RuntimeException e) {
            System.out.println("捕获异常：" + e.getMessage());
        }

        // ========== 6. 同步指令（monitorenter/monitorexit） ==========
        Object lock = new Object();
        synchronized (lock) { // 触发monitorenter（获取锁）
            System.out.println("进入同步块，持有锁");
        } // 触发monitorexit（释放锁）
    }
}