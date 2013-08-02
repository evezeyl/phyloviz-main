package net.phyloviz.synthdata.wizard;

import java.awt.Component;
import java.io.IOException;
import javax.swing.event.ChangeListener;
import net.phyloviz.core.data.AbstractProfile;
import net.phyloviz.core.data.DataSet;
import net.phyloviz.core.data.DataSetTracker;
import net.phyloviz.core.data.TypingData;
import net.phyloviz.core.util.TypingFactory;
import net.phyloviz.mlst.MLSTypingFactory;
import org.openide.WizardDescriptor;
import org.openide.WizardValidationException;
import org.openide.awt.StatusDisplayer;
import org.openide.util.HelpCtx;
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;

public class GenDatasetWizardPanel3 implements WizardDescriptor.ValidatingPanel {

	/**
	 * The visual component that displays this panel. If you need to access the
	 * component from this class, just use getComponent().
	 */
	private GenDatasetVisualPanel3 component;
	private String dataSetName;
	private DataSet ds;

	// Get the visual component for the panel. In this template, the component
	// is kept separate. This can be more efficient: if the wizard is created
	// but never displayed, or not all panels are displayed, it is better to
	// create only those which really need to be visible.
	@Override
	public Component getComponent() {
		if (component == null) {
			component = new GenDatasetVisualPanel3();
		}
		return component;
	}

	@Override
	public HelpCtx getHelp() {
		// Show no Help button for this panel:
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

	// You can use a settings object to keep track of state. Normally the
	// settings object will be the WizardDescriptor, so you can use
	// WizardDescriptor.getProperty & putProperty to store information entered
	// by the user.
	@Override
	public void readSettings(Object settings) {
		((WizardDescriptor) settings).putProperty("WizardPanel_image", ImageUtilities.loadImage("net/phyloviz/synthdata/GenDataset-logo.png", true));
		dataSetName = (String) ((WizardDescriptor) settings).getProperty("name");
		((GenDatasetVisualPanel3) getComponent()).initSimulator(
				(String) ((WizardDescriptor) settings).getProperty("gensize"),
				(String) ((WizardDescriptor) settings).getProperty("gensampled"),
				(String) ((WizardDescriptor) settings).getProperty("popsize"),
				(String) ((WizardDescriptor) settings).getProperty("pfsize"),
				(String) ((WizardDescriptor) settings).getProperty("mut"),
				(String) ((WizardDescriptor) settings).getProperty("rec"),
				(String) ((WizardDescriptor) settings).getProperty("seed"));
	}

	@Override
	public void storeSettings(Object settings) {
	}

	@Override
	public void validate() throws WizardValidationException {
		if (!component.isSimulationFinished()) {
			throw new WizardValidationException(null, "You must complete a whole simulation", null);
		}
		ds = new DataSet(dataSetName);
		TypingData<? extends AbstractProfile> td;
		try {
			MLSTypingFactory tf = new MLSTypingFactory();
			td = tf.loadData(((GenDatasetVisualPanel3) getComponent()).getDataset());
			td.setDescription(tf.toString());
		} catch (IOException ex) {
			throw new WizardValidationException(null, "Fatal error: " + ex.getMessage(), null);
		}
		ds.add(td);
		Lookup.getDefault().lookup(DataSetTracker.class).add(ds);
		StatusDisplayer.getDefault().setStatusText("Synthetic dataset loaded.");
	}
}