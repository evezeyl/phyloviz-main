package net.phyloviz.goeburst.ui;

import java.util.Collection;
import java.util.Iterator;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JPanel;
import net.phyloviz.core.data.Profile;
import net.phyloviz.core.data.TypingData;
import net.phyloviz.goeburst.algorithm.AbstractDistance;
import org.openide.util.Lookup;

public final class GOeBurstVisualPanel1 extends JPanel {

	private DefaultComboBoxModel typeListModel;

	/** Creates new form GOeBurstVisualPanel1 */
	public GOeBurstVisualPanel1(TypingData<? extends Profile> td) {

		typeListModel = new DefaultComboBoxModel();
		Collection<? extends AbstractDistance> result = Lookup.getDefault().lookupAll(AbstractDistance.class);
		Iterator<? extends AbstractDistance> ir = result.iterator();
		while (ir.hasNext()) {
			AbstractDistance ad = ir.next();
			if (ad.maximum(td) > 0)
				typeListModel.addElement(ad);
		}

		initComponents();
	}

	@Override
	public String getName() {
		return "Distance";
	}

	public AbstractDistance getDistance() {
		return (AbstractDistance) jComboBox1.getSelectedItem();
	}

	/** This method is called from within the constructor to
	 * initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is
	 * always regenerated by the Form Editor.
	 */
	        // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
        private void initComponents() {

                jPanel2 = new javax.swing.JPanel();
                jPanel3 = new javax.swing.JPanel();
                jLabel2 = new javax.swing.JLabel();
                jPanel4 = new javax.swing.JPanel();
                jComboBox1 = new javax.swing.JComboBox();
                jPanel5 = new javax.swing.JPanel();
                jPanel1 = new javax.swing.JPanel();
                jLabel3 = new javax.swing.JLabel();

                setLayout(new java.awt.BorderLayout());

                jPanel2.setLayout(new java.awt.BorderLayout());

                jPanel3.setLayout(new java.awt.GridLayout(1, 0, 0, 8));

                org.openide.awt.Mnemonics.setLocalizedText(jLabel2, org.openide.util.NbBundle.getMessage(GOeBurstVisualPanel1.class, "GOeBurstVisualPanel1.jLabel2.text")); // NOI18N
                jLabel2.setBorder(javax.swing.BorderFactory.createEmptyBorder(2, 12, 2, 8));
                jPanel3.add(jLabel2);

                jPanel2.add(jPanel3, java.awt.BorderLayout.WEST);

                jPanel4.setLayout(new java.awt.GridLayout(1, 0, 0, 8));

                jComboBox1.setModel(typeListModel);
                jPanel4.add(jComboBox1);

                jPanel2.add(jPanel4, java.awt.BorderLayout.CENTER);
                jPanel2.add(jPanel5, java.awt.BorderLayout.EAST);

                jPanel1.setLayout(new java.awt.BorderLayout());

                org.openide.awt.Mnemonics.setLocalizedText(jLabel3, org.openide.util.NbBundle.getMessage(GOeBurstVisualPanel1.class, "GOeBurstVisualPanel1.jLabel3.text")); // NOI18N
                jLabel3.setBorder(javax.swing.BorderFactory.createEmptyBorder(8, 12, 8, 12));
                jPanel1.add(jLabel3, java.awt.BorderLayout.CENTER);

                jPanel2.add(jPanel1, java.awt.BorderLayout.SOUTH);

                add(jPanel2, java.awt.BorderLayout.PAGE_START);
        }// </editor-fold>//GEN-END:initComponents
	        // Variables declaration - do not modify//GEN-BEGIN:variables
        private javax.swing.JComboBox jComboBox1;
        private javax.swing.JLabel jLabel2;
        private javax.swing.JLabel jLabel3;
        private javax.swing.JPanel jPanel1;
        private javax.swing.JPanel jPanel2;
        private javax.swing.JPanel jPanel3;
        private javax.swing.JPanel jPanel4;
        private javax.swing.JPanel jPanel5;
        // End of variables declaration//GEN-END:variables
}
