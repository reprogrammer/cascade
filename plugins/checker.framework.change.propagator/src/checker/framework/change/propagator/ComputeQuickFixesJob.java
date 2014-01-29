package checker.framework.change.propagator;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import checkers.eclipse.actions.CheckerWorker;
import checkers.eclipse.util.MutexSchedulingRule;

public class ComputeQuickFixesJob extends Job {

	public static final String NULLNESS_CHECKER = "checkers.nullness.NullnessChecker";

	public static final String JAVARI_CHECKER = "checkers.javari.NullnessChecker";

	private ShadowProject shadowProject;

	private CheckerID checkerID;

	public ComputeQuickFixesJob(String name, ShadowProject shadowProject,
			CheckerID checkerID) {
		super(name);
		this.shadowProject = shadowProject;
		this.checkerID = checkerID;
	}

	private String[] getShadowSourceFiles() {
		return shadowProject.getSourceFiles().toArray(new String[] {});
	}

	private CheckerWorker getCheckerWorker() {
		CheckerWorker checkerJob = new CheckerWorker(
				shadowProject.getProject(), getShadowSourceFiles(),
				checkerID.getId());
		checkerJob.setUser(true);
		checkerJob.setPriority(Job.BUILD);
		checkerJob.setRule(new MutexSchedulingRule());
		return checkerJob;
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {
		CheckerWorker checkerWorker = getCheckerWorker();
		checkerWorker.schedule();
		try {
			checkerWorker.join();
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
		return Status.OK_STATUS;
	}

}
