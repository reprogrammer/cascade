package p;

import checkers.nullness.quals.*;

public class C5 {

	String m() {
	    @NonNull String foo1 = "foo";
	    String bar1 = null;
	    foo1 = bar1;
	    return foo1;
	}
	
}
