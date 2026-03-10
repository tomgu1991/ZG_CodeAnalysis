# JVM字节码记录

## 类型
```
字符	Java类型	说明
B	byte	
C	char	
D	double	
F	float	
I	int	
J	long	因为 L 被对象占用了，取 J
S	short	
Z	boolean	因为 B 被 byte 占用了，取 Z
L   表示对象，L + 全限定名 + ;
数组 double[][] → [[D
签名  (参数类型)返回值类型
        Object find(long id, String name)
        (JLjava/lang/String;)Ljava/lang/Object;
```

## Opcode
https://strongduanmu.com/blog/opcode-mnemonics-by-opcode.html

参看：src/main/java/gu/zuxing/bytecode/HeelByteCode.java

总结：
1. 基本原则： 同栈打交道。计算/读取/存储数据，都是通过栈来完成的。
2. 类型：
   * 常量：iconst_0~5【直接常量】, bipush【byte范围】, sipush【short范围】,ldc相关【更大范围的常量】
   * load/store：0-3是快捷，剩下的都是普通的load/store指令，参数是局部变量表的索引
     * [i/l/f/d/a/] + [load/store] + [0-3快捷]：一个值加载到栈顶
     * [ia/la/fa/da/aa/ba/ca/sa] + [load/store]：将对应类型的数组指定索引的值加载到栈顶
       * store，会对栈进行3次取值（值，索引，数组的引用）；是从栈取，存到本地变量
       * load是两个（索引、数组的引用）；是从本地变量取，存到栈
   * 栈操作：
     * pop/pop2：弹出栈顶值（pop2会弹出两个值，或者一个占两个位置的值）
     * dup/dup2/：复制栈顶
     * dup_x1/dup_x2/dup2_x1/dup2_x2：复制栈顶，并将复制的值插入到指定位置
     * swap：交换栈顶的两个值
   * 算术运算：进行运算后结果存到栈顶
     * [i/l/f/d] + [add/sub/mul/div/rem/shl/ushr/and/or/xor]:都是从栈取两个值
     * [i/l/f/d] + neg：计算负值
     * innc: iinc index const：局部变量表的index位置的值增加const。直接修改本地变量表中的 int 变量值，全程不碰操作数栈
   * 转换：将栈顶的值转换成指定类型后存到栈顶
     * [i/l/f/d] + [2i/2l/2f/2d]：从栈取一个值，转换成指定类型后存到栈顶
     * [i] + [2b/2c/2s]：从栈取一个 int 值，转换成 byte/char/short 后存到栈顶
   * 比较：
     * lcmp/fcmpl/fcmpg/dcmpl/dcmpg：比较两个值，结果存到栈顶，-1/0/1分别表示小于/等于/大于
     * if_icmp[eq/ne/lt/ge/gt/le]：比较两个 int 值，根据结果跳转
     * if[eq/ne/lt/ge/gt/le]：比较栈顶值与0，根据结果跳转
     * if_acmp[eq/ne]：比较两个对象引用，根据结果跳转
   * 控制流：
     * goto：无条件跳转
     * jsr/ret：跳转到子例程，执行完后返回
     * tableswitch/lookupswitch：根据栈顶值进行多路分支跳转
     * return/ireturn/lreturn/freturn/dreturn/areturn：从方法返回，返回值存到栈顶
   * 引用：
     * 域操作指令：getstatic/putstatic 操作类静态变量，getfield/putfield 操作对象实例变量；
     * 方法调用指令：按方法类型分invokevirtual（实例）、invokestatic（静态）、invokespecial（私有 / 构造）、invokeinterface（接口）、invokedynamic（动态）；
     * 数组指令：newarray（基本类型数组）、anewarray（引用类型数组）、arraylength（获取长度）；
     * 类型 / 异常指令：instanceof（类型判断）、checkcast（类型转换检查）、athrow（抛异常）；
     * 同步指令：monitorenter/monitorexit 由synchronized块触发，实现对象锁的获取 / 释放。
     * 注意：
       * 所有的异常处理都是通过异常表来实现的，而不是通过特定的字节码指令。所以try-catch要看异常表！！！
       * invokeinterface: 运行时知道（要看具体的栈的对象是什么），后面是类型+参数slot的个数。一般是ref+参数
       * nvokedynamic 后的数字，不是「目标方法的参数 slot 数」，而是执行「引导方法」时，需要从操作数栈消耗的 slot 数。
   * 扩展
     * wide：扩展指令，修改后续指令的参数宽度，支持更大的局部变量表索引和常量值
     * multianewarray：创建多维数组，参数是类型和维度
     * ifnull/ifnonnull：比较栈顶对象引用与null，根据结果跳转
     * goto_w/jsr_w：无条件跳转，参数是一个32位的偏移量，适用于更远的跳转
     * breakpoint：调试指令，触发调试器断点
     * impdep1/impdep2：保留指令，供特定平台使用，JVM规范未定义其行为

## 一些例子
```shell
LOCALVARIABLE this Lgu/zuxing/bytecode/HelloByteCode; L0 L1 0
LOCALVARIABLE <变量名> <变量类型描述符> <作用域起始标签> <作用域结束标签> <本地变量表索引>
```