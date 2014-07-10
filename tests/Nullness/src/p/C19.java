package p;

import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * Test that the Cascasde overrides only the qualifiers of the return type of
 * the methods not the annotations of a method declaration.
 */
public class C19 {

	Object c19m1() {
		return null;
	}
	
	@SuppressWarnings("rawtypes")
	String c19m2() {
		return null;
	}

	@NonNull
	Integer c19m3() {
		return null;
	}

	@NonNull
	@SuppressWarnings("rawtypes")
	Byte c19m4() {
		return null;
	}

	@SuppressWarnings("rawtypes")
	@NonNull
	Object c19m5() {
		return null;
	}

}
