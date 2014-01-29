package p;

import checkers.nullness.quals.NonNull;
import checkers.nullness.quals.Nullable;

public class C11 {

	@Nullable
	Object c11m1() {
		return null;
	}

	@Nullable
	Object c11m2() {
		return null;
	}

	@Nullable
	Object c11m3() {
		return null;
	}

	void c11m4(Object c11o1) {
	}

	Object c11m5() {
		@NonNull
		Object c11o2 = c11m1();
		c11m4(c11m2());
		return c11m3();
	}

	void c11m6() {
		C12 c12 = new C12();
		c12.c11f = new C11();
		@NonNull
		Object c11o3 = c12.c11f.c11m1();
	}

}

class C12 {
	C11 c11f;
}