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

基本原则： 操作栈

| Opcode | 解释 | 样例 | 源码 |
| --- | --- | --- | --- |