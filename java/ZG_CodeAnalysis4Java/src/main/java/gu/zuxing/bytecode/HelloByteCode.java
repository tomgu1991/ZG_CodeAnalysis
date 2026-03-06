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
}
