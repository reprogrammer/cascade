package p;

public class C7 {

    void m1(C8 c) {
        c.f.g = null;
    }

    C8 m2() {
        return new C8();
    }

    void m3() {
        m2().f = null;
    }

}

class C8 {
    C9 f;
}

class C9 {
    Object g;
}