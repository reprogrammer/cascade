package p;

import org.checkerframework.checker.nullness.qual.NonNull;

public class C5 {

    String m() {
        @NonNull
        String foo1 = "foo";
        String bar1 = null;
        foo1 = bar1;
        return foo1;
    }

}
