package net.phyloviz.pubmlst.wizard;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URL;
import java.util.Vector;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import net.phyloviz.pubmlst.soap.PubmlstSOAP;

public final class PubMLSTVisualPanel1 extends JPanel {

	private DefaultComboBoxModel datasetListModel;
	private Vector vDatabases;
	private int iSelected;
	private String sPubMLSTDB;
	private PubmlstSOAP soapClient;

	/** Creates new form PubMLSTVisualPanel1 */
	public PubMLSTVisualPanel1() {

		datasetListModel = new DefaultComboBoxModel();

		initComponents();
		jComboBox1.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent ae) {
				JComboBox cb = (JComboBox) ae.getSource();
				int i = cb.getSelectedIndex();
				if (i >= 0) {
					Vector vElem = (Vector) vDatabases.get(i);
					sPubMLSTDB = (String) vElem.get(0);
				}
			}
		});


		updateKeyList(true);

		try {
			URL url = PubMLSTVisualPanel1.class.getResource("PubMLSTVisualPanel1.html");
			jEditorPane1.setEditorKit(new HTMLEditorKit());
			jEditorPane1.setPage(url);
			Font font = UIManager.getFont("Label.font");
			String bodyRule = "body { font-family: " + font.getFamily() + "; "
					+ "font-size: " + font.getSize() + "pt; width: " + jEditorPane1.getSize().width + "px;}";
			((HTMLDocument) jEditorPane1.getDocument()).getStyleSheet().addRule(bodyRule);
		} catch (IOException e) {
			// Do nothing...
			System.err.println(e.getMessage());
		}
	}

	private PubmlstSOAP getSOAPClient() {
		if (soapClient == null) {
			soapClient = new PubmlstSOAP();
		}
		return soapClient;
	}

	private void updateKeyList(boolean fetch) {
		datasetListModel.removeAllElements();
		if (fetch) {
			vDatabases = getSOAPClient().getDatabaseList();
		}
		if (vDatabases == null || vDatabases.isEmpty()) {
			datasetListModel.addElement(org.openide.util.NbBundle.getMessage(
					PubMLSTVisualPanel1.class, "PubmlstSOAP.offline"));
			jComboBox1.setEnabled(false);
		} else {
			for (int i = 0; i < vDatabases.size(); i++) {
				Vector db = (Vector) vDatabases.get(i);
				datasetListModel.addElement(db.get(1));
			}
			jComboBox1.setEnabled(true);
		}
	}

	@Override
	public String getName() {
		return "Database";
	}

	public String getDatasetName() {
		return jTextField1.getText();
	}

	public String getSelectedDBShort() {
		return sPubMLSTDB;
	}

	public String getSelectedDBFull() {
		int i = jComboBox1.getSelectedIndex();
		if (i < 0) {
			return "";
		}
		Vector v = (Vector) vDatabases.get(jComboBox1.getSelectedIndex());
		return (String) v.get(1);
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
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jTextField1 = new javax.swing.JTextField();
        jPanel1 = new javax.swing.JPanel();
        jComboBox1 = new javax.swing.JComboBox();
        jButton1 = new javax.swing.JButton();
        jEditorPane1 = new javax.swing.JEditorPane();

        setLayout(new java.awt.BorderLayout());

        jPanel2.setLayout(new java.awt.BorderLayout());

        jPanel3.setLayout(new java.awt.GridLayout(2, 0, 0, 8));

        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, org.openide.util.NbBundle.getMessage(PubMLSTVisualPanel1.class, "PubMLSTVisualPanel1.jLabel1.text")); // NOI18N
        jLabel1.setBorder(javax.swing.BorderFactory.createEmptyBorder(2, 12, 2, 8));
        jPanel3.add(jLabel1);

        org.openide.awt.Mnemonics.setLocalizedText(jLabel2, org.openide.util.NbBundle.getMessage(PubMLSTVisualPanel1.class, "PubMLSTVisualPanel1.jLabel2.text")); // NOI18N
        jLabel2.setBorder(javax.swing.BorderFactory.createEmptyBorder(2, 12, 2, 8));
        jPanel3.add(jLabel2);

        jPanel2.add(jPanel3, java.awt.BorderLayout.WEST);

        jPanel4.setLayout(new java.awt.GridLayout(2, 0, 0, 8));

        jTextField1.setText(org.openide.util.NbBundle.getMessage(PubMLSTVisualPanel1.class, "PubMLSTVisualPanel1.jTextField1.text")); // NOI18N
        jPanel4.add(jTextField1);

        jPanel1.setLayout(new java.awt.BorderLayout());

        jComboBox1.setModel(datasetListModel);
        jPanel1.add(jComboBox1, java.awt.BorderLayout.CENTER);

        org.openide.awt.Mnemonics.setLocalizedText(jButton1, org.openide.util.NbBundle.getMessage(PubMLSTVisualPanel1.class, "PubMLSTVisualPanel1.jButton1.text")); // NOI18N
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        jPanel1.add(jButton1, java.awt.BorderLayout.EAST);

        jPanel4.add(jPanel1);

        jPanel2.add(jPanel4, java.awt.BorderLayout.CENTER);

        add(jPanel2, java.awt.BorderLayout.PAGE_START);

        jEditorPane1.setBackground(jPanel2.getBackground());
        jEditorPane1.setBorder(javax.swing.BorderFactory.createEmptyBorder(8, 12, 8, 12));
        jEditorPane1.setEditable(false);
        jEditorPane1.setMaximumSize(new java.awt.Dimension(200, 200));
        add(jEditorPane1, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
	updateKeyList(true);
}//GEN-LAST:event_jButton1ActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JComboBox jComboBox1;
    private javax.swing.JEditorPane jEditorPane1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JTextField jTextField1;
    // End of variables declaration//GEN-END:variables
}
