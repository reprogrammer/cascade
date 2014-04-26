package p;

public class C18 {

    void c18m1() {
        C17 c17 = new C17();
        // The tool should propose a quick fix for the error in the following
        // statement even though field "objects" is declared in some other
        // compilation unit (issue #54).
        c17.objects[0] = null;
    }

}
