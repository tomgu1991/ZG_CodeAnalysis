package com.example;

import java.io.FileReader;
import java.io.IOException;

/**
 * TestCase1 - 包含多个 bug 的测试类
 * 用于演示代码分析工具的检测能力
 */
public class TestCase1 {

    /**
     * Bug 1: 潜在的空指针异常
     */
    public void bug1NullPointerException() {
        String str = null;
        int length = str.length();  // NullPointerException
        System.out.println("Length: " + length);
    }

    /**
     * Bug 2: 资源泄露（文件没有关闭）
     */
    public void bug2ResourceLeak() {
        try {
            FileReader reader = new FileReader("test.txt");
            // 读取文件但忘记关闭
            int data = reader.read();
            System.out.println("Read: " + data);
            // 缺少 reader.close()
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Bug 3: 整数溢出
     */
    public void bug3IntegerOverflow() {
        int maxInt = Integer.MAX_VALUE;
        int result = maxInt + 1;  // 整数溢出
        System.out.println("Result: " + result);
    }

    /**
     * Bug 4: 未使用的变量
     */
    public void bug4UnusedVariable() {
        int unusedVar = 100;  // 声明但未使用
        String name = "test";
        System.out.println(name);
    }

    /**
     * Bug 5: 不合理的条件判断
     */
    public void bug5UnreasonableCondition() {
        int value = 10;
        if (value > 100 && value < 5) {  // 永远不会成立
            System.out.println("This will never execute");
        }
    }

    /**
     * Bug 6: 比较字符串应该使用 equals 而不是 ==
     */
    public void bug6StringComparison() {
        String str1 = new String("hello");
        String str2 = new String("hello");

        if (str1 == str2) {  // 错误：应该使用 equals()
            System.out.println("Strings are equal");
        } else {
            System.out.println("Strings are not equal");
        }
    }

    /**
     * Bug 7: 异常处理过于宽泛
     */
    public void bug7BroadExceptionHandling() {
        try {
            int result = 10 / 0;
            System.out.println(result);
        } catch (Exception e) {  // 不应该捕获通用的 Exception
            System.out.println("Error occurred");
        }
    }

    /**
     * Bug 8: 浮点数精度问题
     */
    public void bug8FloatingPointPrecision() {
        double a = 0.1;
        double b = 0.2;
        double sum = a + b;

        if (sum == 0.3) {  // 浮点数精度问题，这个比较不可靠
            System.out.println("Sum is 0.3");
        } else {
            System.out.println("Sum is not 0.3, but " + sum);
        }
    }

    /**
     * Bug 9: 没有初始化的字段
     */
    private String uninitializedField;  // 未初始化

    public void bug9UninitializedField() {
        System.out.println(uninitializedField.length());  // 可能的空指针异常
    }

    /**
     * Bug 10: 循环中无法打破的条件
     */
    public void bug10InfiniteLoop() {
        int i = 0;
        while (i >= 0) {  // 可能导致死循环
            i++;
            if (i > 1000000) break;  // 虽然有break，但逻辑不清晰
        }
        System.out.println("Loop ended");
    }

    public static void main(String[] args) {
        TestCase1 test = new TestCase1();
        System.out.println("TestCase1 initialized (all bugs present)");
        // 取消下面的注释来运行各个有 bug 的方法
        // test.bug1NullPointerException();
        // test.bug2ResourceLeak();
        // test.bug3IntegerOverflow();
    }
}
