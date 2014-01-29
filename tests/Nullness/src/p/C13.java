package p;

// This tests type qualifier inference for the SingleVariableDeclaration case.
// It propagates @Nullable from a method to another method it overrides.
public class C13 {
	abstract class C14 {
		abstract void c14m1(Object c14o1);
	}

	class C15 extends C14 {
		@Override
		void c14m1(Object c15o1) {
		}
	}

	class C16 {
		C14 c16f1;

		void c16m2() {
			c16f1.c14m1(null);
		}
	}
}
