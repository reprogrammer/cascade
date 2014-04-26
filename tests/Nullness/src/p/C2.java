package p;

class C2 {

    void m2(Object o) {
    }

    void m1(Object o) {
        m2(o);
    }

    void m2() {
        Object o = null;
        m1(o);
    }

    void c2m3(Object c2o1) {

    }

    void c2m4(Object c2o2) {
        c2m3(c2o2);
    }
}
