package checker.framework.change.propagator;

import org.eclipse.jdt.core.ICompilationUnit;

public class MarkerLocation {

    private final ICompilationUnit compilationUnit;

    private final int offset;

    private final int length;

    public MarkerLocation(ICompilationUnit compilationUnit, int offset,
            int length) {
        this.compilationUnit = compilationUnit;
        this.offset = offset;
        this.length = length;
    }

    public ICompilationUnit getCompilationUnit() {
        return compilationUnit;
    }

    public int getOffset() {
        return offset;
    }

    public int getLength() {
        return length;
    }

}
