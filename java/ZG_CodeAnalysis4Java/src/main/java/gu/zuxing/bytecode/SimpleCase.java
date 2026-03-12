package gu.zuxing.bytecode;

public class SimpleCase {
    public int ifStmt (int x, int y) {
        if (x == 0 && y == 1) {
            return 0;
        } else if (y == 0) {
            return 1;
        }

        if (y > 5) {
            return x + 6;
        }

        int z = x + y;
        return z / y;
    }
}
