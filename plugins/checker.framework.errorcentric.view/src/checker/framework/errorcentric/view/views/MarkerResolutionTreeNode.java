package checker.framework.errorcentric.view.views;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.internal.corext.refactoring.nls.NLSMessages;

import checker.framework.change.propagator.ActionableMarkerResolution;
import checker.framework.change.propagator.ComparableMarker;
import checker.framework.errorcentric.view.Messages;
import checker.framework.quickfixes.descriptors.FixerDescriptor;

import static com.google.common.collect.Sets.newHashSet;

public class MarkerResolutionTreeNode extends TreeObject {

    private ActionableMarkerResolution resolution;

    private Job job;

    private volatile int fixedErrorsCount;

    private Set<ComparableMarker> unresolvableMarkers;

    public MarkerResolutionTreeNode(ActionableMarkerResolution resolution,
            TreeUpdater treeUpdater) {
        super(resolution.getLabel(), treeUpdater);
        this.resolution = resolution;
        this.unresolvableMarkers = new HashSet<>();
    }

    public ActionableMarkerResolution getResolution() {
        return resolution;
    }

    public List<FixerDescriptor> getParentFixerDescriptors() {
        LinkedList<FixerDescriptor> fixerDescriptors = new LinkedList<>();
        TreeObject parent = getParent();
        while (parent != null) {
            if (parent instanceof MarkerResolutionTreeNode) {
                fixerDescriptors.addLast(((MarkerResolutionTreeNode) parent)
                        .getResolution().getFixerDescriptor());
            }
            parent = parent.getParent();
        }
        return fixerDescriptors;
    }

    public void addErrorCountToLabel() {
        setName(getName() + " (" + fixedErrorsCount + ")"); //$NON-NLS-1$ //$NON-NLS-2$
    }

    public void computeChangeEffect() {
        String progressBarLabel = NLSMessages
                .bind(Messages.ErrorCentricView_compute_change_effect_progress_bar_label,
                        resolution.getLabel());
        job = new ChangeComputationJob(progressBarLabel, this);
        // We are avoiding using the project as the scheduling rule because it
        // conflicts with the rule that is used by the undoable operation's
        // checkpoint mechanism. Instead we are using our own synchronization
        // locks now.
        // IProject project = resolution.getShadowProject().getProject()
        // .getProject();
        // job.setRule(project);
        job.setPriority(Job.DECORATE);
        job.schedule();
    }

    public TreeObject[] getChildren() {
        try {
            job.join();
        } catch (InterruptedException e) {
            throw new RuntimeException();
        }
        return super.getChildren();
    }

    public TreeObject[] getExistingChildren() {
        return super.getChildren();
    }

    @Override
    public int getRank() {
        return getFixedErrorsCount();
    }

    public int getFixedErrorsCount() {
        return fixedErrorsCount;
    }

    public void setFixedErrorsCount(int fixedErrorsCount) {
        this.fixedErrorsCount = fixedErrorsCount;
    }

    public Set<ComparableMarker> getUnresolvableMarkers() {
        return unresolvableMarkers;
    }

    @Override
    public int hashCode() {
        // TODO(reprogrammer): Use HashCodeBuilder.
        return resolution.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        // TODO(reprogrammer): Use EqualsBuilder and ensure the correctness with
        // respect to the instanceof check.
        if (obj instanceof MarkerResolutionTreeNode) {
            MarkerResolutionTreeNode otherNode = (MarkerResolutionTreeNode) obj;
            return hasSameResolution(otherNode) && fixesSameErrors(otherNode);
        }
        return false;
    }

    public Set<ErrorTreeNode> getErrorsFixed() {
        return newHashSet(ErrorTreeNode.createTreeNodesFrom(
                newHashSet(resolution), new NoOpTreeUpdater(), false));
    }

    public boolean hasSameResolution(MarkerResolutionTreeNode other) {
        return resolution.equals(other.resolution);
    }

    public boolean fixesSameErrors(MarkerResolutionTreeNode other) {
        return getErrorsFixed().equals(other.getErrorsFixed());
    }

}
