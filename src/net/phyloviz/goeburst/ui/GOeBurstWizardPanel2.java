package net.phyloviz.goeburst.ui;

import java.awt.Component;
import javax.swing.event.ChangeListener;
import net.phyloviz.core.data.Profile;
import net.phyloviz.core.data.TypingData;
import net.phyloviz.goeburst.algorithm.AbstractDistance;
import org.openide.WizardDescriptor;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.ImageUtilities;

public class GOeBurstWizardPanel2 implements WizardDescriptor.Panel {

	/**
	 * The visual component that displays this panel. If you need to access the
	 * component from this class, just use getComponent().
	 */
	private GOeBurstVisualPanel2 component;

	public GOeBurstWizardPanel2(Node node) {
		super();
	}


	@Override
	public Component getComponent() {
		if (component == null) {
			component = new GOeBurstVisualPanel2();
		}
		return component;
	}

	@Override
	public HelpCtx getHelp() {
		return HelpCtx.DEFAULT_HELP;
	}

	@Override
	public boolean isValid() {
		return true;
	}

	@Override
	public final void addChangeListener(ChangeListener l) {
	}

	@Override
	public final void removeChangeListener(ChangeListener l) {
	}

	@SuppressWarnings("unchecked")
	@Override
	public void readSettings(Object settings) {
		((WizardDescriptor) settings).putProperty("WizardPanel_image", ImageUtilities.loadImage("net/phyloviz/goeburst/GOeBurstImage.png", true));
		Node node = (Node) ((WizardDescriptor) settings).getProperty("node");
		AbstractDistance ad = (AbstractDistance) ((WizardDescriptor) settings).getProperty("distance");
		TypingData<? extends Profile> td = node.getLookup().lookup(TypingData.class);
		component.setParameters(1, ad.maximum(td), 1);
	}

	@Override
	public void storeSettings(Object settings) {
		((WizardDescriptor) settings).putProperty("level", ((GOeBurstVisualPanel2) getComponent()).getLevel());
	}
}
