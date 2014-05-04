package checker.framework.change.propagator;

import checker.framework.quickfixes.descriptors.CompilationUnitDescriptor;

public class MarkerLocationDescriptor {

    private final CompilationUnitDescriptor compilationUnitDescriptor;

    private final int codeSnippetOffsetRelativeToSurroundingCodeSnippet;

    private final int codeSnippetLength;

    private final String codeSnippet;

    private final int surroundingCodeSnippetOffset;

    private final int surroundingCodeSnippetLength;

    private final String surroundingCodeSnippet;

    public MarkerLocationDescriptor(
            CompilationUnitDescriptor compilationUnitDescriptor,
            int codeSnippetOffsetRelativeToSurroundingCodeSnippet,
            int codeSnippetLength, String codeSnippet,
            String surroundingCodeSnippet, int surroundingCodeSnippetOffset,
            int surroundingCodeSnippetLength) {
        this.compilationUnitDescriptor = compilationUnitDescriptor;
        this.codeSnippetOffsetRelativeToSurroundingCodeSnippet = codeSnippetOffsetRelativeToSurroundingCodeSnippet;
        this.codeSnippetLength = codeSnippetLength;
        this.codeSnippet = codeSnippet;
        this.surroundingCodeSnippet = surroundingCodeSnippet;
        this.surroundingCodeSnippetOffset = surroundingCodeSnippetOffset;
        this.surroundingCodeSnippetLength = surroundingCodeSnippetLength;
    }

    CompilationUnitDescriptor getCompilationUnitDescriptor() {
        return compilationUnitDescriptor;
    }

    int getCodeSnippetOffsetRelativeToSurroundingCodeSnippet() {
        return codeSnippetOffsetRelativeToSurroundingCodeSnippet;
    }

    int getCodeSnippetLength() {
        return codeSnippetLength;
    }

    int getSurroundingCodeSnippetOffset() {
        return surroundingCodeSnippetOffset;
    }

    int getSurroundingCodeSnippetLength() {
        return surroundingCodeSnippetLength;
    }

    String getCodeSnippet() {
        return codeSnippet;
    }

    String getSurroundingCodeSnippet() {
        return surroundingCodeSnippet;
    }

}
