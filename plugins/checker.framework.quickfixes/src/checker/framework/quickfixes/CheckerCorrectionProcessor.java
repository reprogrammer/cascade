package checker.framework.quickfixes;

import static com.google.common.collect.Iterables.transform;
import static com.google.common.collect.Sets.newHashSet;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.jdt.ui.text.java.IJavaCompletionProposal;

import checker.framework.quickfixes.descriptors.FixerDescriptor;
import checker.framework.quickfixes.descriptors.FixerDescriptorFactory;
import checker.framework.quickfixes.methodparameterfixer.MethodParameterFixerDescriptorFactory;
import checker.framework.quickfixes.methodreceiverfixer.MethodReceiverFixerDescriptorFactory;
import checker.framework.quickfixes.methodreturnfixer.MethodReturnFixerDescriptorFactory;
import checker.framework.quickfixes.variabledeclarationfixer.VariableDeclarationFixerDescriptorFactory;

import com.google.common.base.Function;

public class CheckerCorrectionProcessor {

    public static void collectCorrections(final MarkerContext context,
            Collection<IJavaCompletionProposal> proposals) {
        Set<FixerDescriptorFactory<? extends FixerDescriptor>> factories = createFactories(context);
        for (FixerDescriptorFactory<? extends FixerDescriptor> factory : factories) {
            proposals.addAll(createProposalsFromFixerDescriptors(context,
                    factory.get()));
        }
    }

    private static HashSet<IJavaCompletionProposal> createProposalsFromFixerDescriptors(
            final MarkerContext context,
            Set<? extends FixerDescriptor> fixerDescriptors) {
        return newHashSet(transform(fixerDescriptors,
                new Function<FixerDescriptor, IJavaCompletionProposal>() {
                    @Override
                    public IJavaCompletionProposal apply(
                            FixerDescriptor descriptor) {
                        return descriptor.createProposalFactory(context)
                                .createProposal();
                    }
                }));
    }

    public static void collectFixerDescriptors(MarkerContext context,
            Collection<FixerDescriptor> fixerDescriptors) {
        Set<FixerDescriptorFactory<? extends FixerDescriptor>> factories = createFactories(context);
        for (FixerDescriptorFactory<? extends FixerDescriptor> factory : factories) {
            fixerDescriptors.addAll(factory.get());
        }
    }

    private static Set<FixerDescriptorFactory<? extends FixerDescriptor>> createFactories(
            MarkerContext context) {
        Set<FixerDescriptorFactory<? extends FixerDescriptor>> factories = new HashSet<>();
        factories.add(new MethodReturnFixerDescriptorFactory(context));
        factories.add(new VariableDeclarationFixerDescriptorFactory(context));
        factories.add(new MethodParameterFixerDescriptorFactory(context));
        factories.add(new MethodReceiverFixerDescriptorFactory(context));
        return factories;
    }

}
