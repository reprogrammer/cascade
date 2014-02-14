package checker.framework.separated.view.views;

import com.google.common.base.Optional;

import checker.framework.separated.view.views.list.SeparatedErrorsView;
import checker.framework.separated.view.views.tree.SeparatedChangesView;

public class Views {
    private static Optional<SeparatedErrorsView> errorsView;
    private static Optional<SeparatedChangesView> changesView;

    public static Optional<SeparatedErrorsView> getErrorsView() {
        return errorsView;
    }

    public static void setErrorsView(SeparatedErrorsView errorsView) {
        Views.errorsView = Optional.fromNullable(errorsView);
    }

    public static Optional<SeparatedChangesView> getChangesView() {
        return changesView;
    }

    public static void setChangesView(SeparatedChangesView changesView) {
        Views.changesView = Optional.fromNullable(changesView);
    }

}
