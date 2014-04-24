package checker.framework.errorcentric.view;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
    private static final String BUNDLE_NAME = "checker.framework.errorcentric.view.messages"; //$NON-NLS-1$
    public static String ErrorCentricView_refresh_icon;
    public static String ErrorCentricView_refresh_text;
    public static String ErrorCentricView_refresh_tool_tip;
    public static String ErrorCentricView_resolution_application_job_name;
    static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages() {
    }
}
