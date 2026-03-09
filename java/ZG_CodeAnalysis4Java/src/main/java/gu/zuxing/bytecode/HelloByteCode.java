package gu.zuxing.bytecode;

public final class HelloByteCode {

    public static void main(String[] args) {
        HelloByteCode helloByteCode = new HelloByteCode();
        helloByteCode.sayHello();
    }

    private void sayHello() {
        String str = "Hello, ByteCode!";
        int x = 10;
        System.out.println("Hello, ByteCode!");
        System.out.println(str + x);
    }

    private int calculate() {
        int a = 100;
        int b = 200 + a;
        int c = 300;
        return (b) * c;
    }

    // 第一部分：常量相关
    private void constDemo() {
        // 1. aconst_null: 推送null引用
        Object nullObj = null;

        // 2. iconst系列: 推送int常量(-1~5)
        int m1 = -1;    // iconst_m1
        int zero = 0;   // iconst_0
        int one = 1;    // iconst_1
        int two = 2;    // iconst_2
        int three = 3;  // iconst_3
        int four = 4;   // iconst_4
        int five = 5;   // iconst_5

        // 3. lconst系列: 推送long常量(0、1)
        long longZero = 0L;  // lconst_0
        long longOne = 1L;   // lconst_1

        // 4. fconst系列: 推送float常量(0、1、2)
        float floatZero = 0.0f;  // fconst_0
        float floatOne = 1.0f;   // fconst_1
        float floatTwo = 2.0f;   // fconst_2

        // 5. dconst系列: 推送double常量(0、1)
        double doubleZero = 0.0d;  // dconst_0
        double doubleOne = 1.0d;   // dconst_1

        // 6. bipush: 推送单字节常量(-128~127)
        int byteConst1 = 100;   // bipush 100 (范围符合单字节)
        int byteConst2 = -120;  // bipush -120

        // 7. sipush: 推送短整型常量(-32768~32767)
        int shortConst1 = 20000;  // sipush 20000 (超出bipush范围)
        int shortConst2 = -30000; // sipush -30000

        // 8. ldc: 推送int/float/String常量(从常量池加载)
        int ldcInt = 40000;       // ldc (超出sipush范围的int)
        float ldcFloat = 3.14f;  // ldc (非0/1/2的float)
        String ldcStr = "Hello Bytecode"; // ldc (字符串常量)

        // 9. ldc_w: 推送int/float/String(宽索引，常量池索引>255时触发)
        // 注：ldc_w和ldc逻辑一致，仅索引宽度不同，代码层面无法直接区分，
        // 需常量池条目足够多时编译器自动用ldc_w，这里用一个长字符串模拟
        String ldcWStr = "This is a long string to make constant pool index larger than 255";

        // 10. ldc2_w: 推送long/double常量(从常量池加载，非0/1的long/double)
        long ldc2Long = 123456789L;    // ldc2_w (超出lconst_0/1的long)
        double ldc2Double = 3.1415926d;// ldc2_w (超出dconst_0/1的double)
    }

    private void storeLoadDemo() {
        // ===================== 1. 本地变量加载指令 (iload/lload/fload/dload/aload 系列) =====================
        // 基础类型本地变量（索引0-3，触发iload_0/iload_1等快捷指令）
        int intVar0 = 10;    // 本地变量表索引0
        int intVar1 = 20;    // 索引1
        int intVar2 = 30;    // 索引2
        int intVar3 = 40;    // 索引3
        int intVar4 = 50;    // 索引4（超出0-3，触发iload 4）

        long longVar0 = 100L; // 索引5
        long longVar1 = 200L; // 索引7（long占2个本地变量槽位）
        long longVar4 = 300L; // 索引10（超出0-3，触发lload 10）

        float floatVar0 = 1.1f; // 索引12
        float floatVar1 = 2.2f; // 索引13
        float floatVar4 = 3.3f; // 索引16（超出0-3，触发fload 16）

        double doubleVar0 = 1.1d; // 索引17
        double doubleVar1 = 2.2d; // 索引19（double占2个槽位）
        double doubleVar4 = 3.3d; // 索引22（超出0-3，触发dload 22）

        // 引用类型本地变量
        String strVar0 = "Hello"; // 索引24
        String strVar1 = "World"; // 索引25
        String strVar4 = "Java";  // 索引28（超出0-3，触发aload 28）

        // 使用变量（触发加载指令：将变量推到操作数栈）
        int intSum = intVar0 + intVar1 + intVar2 + intVar3 + intVar4; // 触发iload_0、iload_1、iload_2、iload_3、iload 4
        long longSum = longVar0 + longVar1 + longVar4; // 触发lload_0、lload_1、lload 10
        float floatSum = floatVar0 + floatVar1 + floatVar4; // 触发fload_0、fload_1、fload 16
        double doubleSum = doubleVar0 + doubleVar1 + doubleVar4; // 触发dload_0、dload_1、dload 22
        String strConcat = strVar0 + strVar1 + strVar4; // 触发aload_0、aload_1、aload 28

        // ===================== 2. 数组加载指令 (iaload/laload等) =====================
        // int数组
        /*
        *      127: iconst_3
     128: newarray       int
     130: dup
     131: iconst_0
     132: iconst_1
     133: iastore
     134: dup
     135: iconst_1
     136: iconst_2
     137: iastore
     138: dup
     139: iconst_2
     140: iconst_3
     141: iastore
     142: astore        31
     144: aload         31
     146: iconst_1
     147: iaload
     148: istore
        * */
        int[] intArray = {1, 2, 3};
        int intArrVal = intArray[1]; // 触发iaload

        // long数组
        long[] longArray = {10L, 20L, 30L};
        long longArrVal = longArray[0]; // 触发laload

        // float数组
        float[] floatArray = {1.1f, 2.2f};
        float floatArrVal = floatArray[1]; // 触发faload

        // double数组
        double[] doubleArray = {1.1d, 2.2d};
        double doubleArrVal = doubleArray[0]; // 触发daload

        // 引用类型数组
        String[] strArray = {"A", "B", "C"};
        String strArrVal = strArray[2]; // 触发aaload

        // byte数组（baload）
        byte[] byteArray = {10, 20, 30};
        byte byteArrVal = byteArray[1]; // 触发baload

        // char数组（caload）
        char[] charArray = {'a', 'b', 'c'};
        char charArrVal = charArray[0]; // 触发caload

        // short数组（saload）
        short[] shortArray = {100, 200, 300};
        short shortArrVal = shortArray[2]; // 触发saload
    }

    private int cmpControlDemo() {
        // ========== 1. 数值比较指令（lcmp/fcmpl/fcmpg/dcmpl/dcmpg） ==========
        long longA = 100L, longB = 200L;
        float floatA = 3.14f, floatB = Float.NaN; // 包含NaN测试fcmpl/fcmpg
        double doubleA = 6.28d, doubleB = Double.NaN;

        // lcmp：比较两个long，结果压栈（1/-1/0）
        int longCompare = (longA > longB) ? 1 : (longA < longB) ? -1 : 0;

        // fcmpl：float比较（含NaN时返回-1）
        int floatCompL = (floatA == floatB) ? 0 : (floatA > floatB) ? 1 : -1;
        // fcmpg：float比较（含NaN时返回1）
        int floatCompG = (floatB == floatA) ? 0 : (floatB > floatA) ? 1 : -1;

        // dcmpl：double比较（含NaN时返回-1）
        int doubleCompL = (doubleA == doubleB) ? 0 : (doubleA > doubleB) ? 1 : -1;
        // dcmpg：double比较（含NaN时返回1）
        int doubleCompG = (doubleB == doubleA) ? 0 : (doubleB > doubleA) ? 1 : -1;

        // ========== 2. int条件跳转指令（ifeq/ifne/iflt/ifge/ifgt/ifle） ==========
        int flag = 5;
        if (flag == 0) { // ifeq：栈顶int=0时跳转
            System.out.println("flag等于0");
        }
        if (flag != 0) { // ifne：栈顶int≠0时跳转
            System.out.println("flag不等于0");
        }
        if (flag < 0) { // iflt：栈顶int<0时跳转
            System.out.println("flag小于0");
        }
        if (flag >= 0) { // ifge：栈顶int≥0时跳转
            System.out.println("flag大于等于0");
        }
        if (flag > 0) { // ifgt：栈顶int>0时跳转
            System.out.println("flag大于0");
        }
        if (flag <= 0) { // ifle：栈顶int≤0时跳转
            System.out.println("flag小于等于0");
        }

        // ========== 3. int比较跳转指令（if_icmpeq/if_icmpne/if_icmplt/if_icmpge/if_icmpgt/if_icmple） ==========
        int num1 = 10, num2 = 20;
        if (num1 == num2) { // if_icmpeq：两int相等时跳转
            System.out.println("num1等于num2");
        }
        if (num1 != num2) { // if_icmpne：两int不等时跳转
            System.out.println("num1不等于num2");
        }
        if (num1 < num2) { // if_icmplt：num1<num2时跳转
            System.out.println("num1小于num2");
        }
        if (num1 >= num2) { // if_icmpge：num1≥num2时跳转
            System.out.println("num1大于等于num2");
        }
        if (num1 > num2) { // if_icmpgt：num1>num2时跳转
            System.out.println("num1大于num2");
        }
        if (num1 <= num2) { // if_icmple：num1≤num2时跳转
            System.out.println("num1小于等于num2");
        }

        // ========== 4. 引用比较跳转指令（if_acmpeq/if_acmpne） ==========
        String str1 = "Java";
        String str2 = new String("Java");
        if (str1 == str2) { // if_acmpeq：两引用相等时跳转（注意：是引用比较，非内容）
            System.out.println("str1和str2引用相等");
        }
        if (str1 != str2) { // if_acmpne：两引用不等时跳转
            System.out.println("str1和str2引用不等");
        }

        // ========== 5. 无条件跳转（goto） ==========
        // int count = 0;
        // loop: // 标签，配合goto
        // while (true) {
        //     count++;
        //     if (count > 3) {
        //         goto loop; // Java代码无goto，但编译器会为循环生成goto指令
        //     }
        //     break;
        // }

        // ========== 6. switch跳转（tableswitch：连续case；lookupswitch：不连续case） ==========
        // tableswitch：case值连续（1/2/3）
        int switch1 = 2;
        switch (switch1) {
            case 1:
                System.out.println("switch1=1");
                break;
            case 2:
                System.out.println("switch1=2");
                break;
            case 3:
                System.out.println("switch1=3");
                break;
            default:
                System.out.println("switch1默认");
        }

        // lookupswitch：case值不连续（1/5/9）
        int switch2 = 5;
        switch (switch2) {
            case 1:
                System.out.println("switch2=1");
                break;
            case 5:
                System.out.println("switch2=5");
                break;
            case 9:
                System.out.println("switch2=9");
                break;
            default:
                System.out.println("switch2默认");
        }

        // ========== 7. 不同类型返回指令（调用各返回函数） ==========
        // System.out.println("返回int：" + returnInt());
        // System.out.println("返回long：" + returnLong());
        // System.out.println("返回float：" + returnFloat());
        // System.out.println("返回double：" + returnDouble());
        // System.out.println("返回引用：" + returnReference());
        return 1;
    }
}
