package checker.framework.change.propagator;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.JavaCore;

import checker.framework.quickfixes.descriptors.CompilationUnitDescriptor;
import checker.framework.quickfixes.descriptors.CompilationUnitDescriptorFactory;

public class MarkerLocationDescriptorFactory {

	private final IMarker marker;

	private final CompilationUnitDescriptorFactory compilationUnitDescriptorFactory = new CompilationUnitDescriptorFactory();

	public MarkerLocationDescriptorFactory(IMarker marker) {
		this.marker = marker;
	}

	// Adapted from
	// http://help.eclipse.org/indigo/index.jsp?topic=%2Forg.eclipse.jdt.doc.isv%2Fporting%2F3.3%2Frecommended.html
	private ICompilationUnit toCompilationUnit(IResource resource) {
		return (ICompilationUnit) JavaCore.create(resource);
	}

	public MarkerLocationDescriptor get() {
		ICompilationUnit compilationUnit = toCompilationUnit(marker
				.getResource());
		CompilationUnitDescriptor compilationUnitDescriptor = compilationUnitDescriptorFactory
				.get(compilationUnit);
		String text = new CompilationUnitTextExtractor(compilationUnit)
				.getText();
		try {
			Integer beginIndex = (Integer) marker
					.getAttribute(IMarker.CHAR_START);
			Integer endIndex = (Integer) marker.getAttribute(IMarker.CHAR_END);
			if (beginIndex < 0) {
				beginIndex = 0;
			}
			if (endIndex < beginIndex) {
				endIndex = beginIndex;
			}
			return new MarkerLocationDescriptor(compilationUnitDescriptor,
					beginIndex, endIndex - beginIndex, enclosingLine(text,
							beginIndex, endIndex));
		} catch (CoreException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 
	 * @param text
	 * @param beginIndex
	 * @param endIndex
	 * @return the largest substring of text that contains [beginIndex,
	 *         endIndex) and doesn't include any new line characters.
	 */
	private String enclosingLine(String text, int beginIndex, int endIndex) {
		String separator = System.getProperty("line.separator");
		int indexOfNextSeparator = text.indexOf(separator, endIndex);
		if (indexOfNextSeparator == -1) {
			indexOfNextSeparator = endIndex;
		}
		int indexOfPreviousSeparator = text.substring(0, beginIndex - 1)
				.lastIndexOf(separator);
		if (indexOfPreviousSeparator == -1) {
			indexOfPreviousSeparator = beginIndex;
		} else {
			++indexOfPreviousSeparator;
		}
		return text.substring(indexOfPreviousSeparator, indexOfNextSeparator);
	}

}
