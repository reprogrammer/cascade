package p;

import org.checkerframework.checker.nullness.qual.Nullable;

public class C6 {

    @Nullable
    Object[] a = new Object[] {};

    @Nullable
    Object[][] b = new Object[][] {};

    Object[][] c = new Object[][] {};

    Object[][] d = new Object[][] {};

    void m1() {
        a[0] = null;
        b[0][0] = null;
        c = b;
        d[0] = b;
        m2()[0] = (@Nullable Object[]) a;
    }

    Object[][] m2() {
        return new Object[][] {};
    }

}
