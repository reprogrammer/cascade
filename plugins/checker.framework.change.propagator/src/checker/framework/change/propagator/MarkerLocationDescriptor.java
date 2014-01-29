package checker.framework.change.propagator;

import checker.framework.quickfixes.descriptors.CompilationUnitDescriptor;

public class MarkerLocationDescriptor {

	private final CompilationUnitDescriptor compilationUnitDescriptor;

	private final int initialOffset;

	private final int initialLength;

	private final String codeSnippet;

	public MarkerLocationDescriptor(
			CompilationUnitDescriptor compilationUnitDescriptor,
			int initialOffset, int initialLength, String codeSnippet) {
		this.compilationUnitDescriptor = compilationUnitDescriptor;
		this.initialOffset = initialOffset;
		this.initialLength = initialLength;
		this.codeSnippet = codeSnippet;
	}

	CompilationUnitDescriptor getCompilationUnitDescriptor() {
		return compilationUnitDescriptor;
	}

	int getInitialOffset() {
		return initialOffset;
	}

	int getInitialLength() {
		return initialLength;
	}

	String getCodeSnippet() {
		return codeSnippet;
	}

}
