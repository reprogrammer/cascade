package p;

// This tests type qualifier inference for the SingleVariableDeclaration case.
// It propagates @Nullable from a method to another method it overrides.
abstract class C13 {
	abstract void c13m1(Object c13o1);
}

class C14 extends C13 {
	@Override
	void c13m1(Object c14o1) {
	}
}

class C15 {
	C13 c15f1;

	void c16m2() {
		c15f1.c13m1(null);
	}
}