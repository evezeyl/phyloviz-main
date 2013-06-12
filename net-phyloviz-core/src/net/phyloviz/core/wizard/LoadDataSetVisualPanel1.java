/*-
 * Copyright (c) 2011, PHYLOViZ Team <phyloviz@gmail.com>
 * All rights reserved.
 * 
 * This file is part of PHYLOViZ <http://www.phyloviz.net>.
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the
 * Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * for more details.
 * 
 * You should have received a copy of the GNU General Public License along
 * with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Linking this library statically or dynamically with other modules is
 * making a combined work based on this library.  Thus, the terms and
 * conditions of the GNU General Public License cover the whole combination.
 * 
 * As a special exception, the copyright holders of this library give you
 * permission to link this library with independent modules to produce an
 * executable, regardless of the license terms of these independent modules,
 * and to copy and distribute the resulting executable under terms of your
 * choice, provided that you also meet, for each linked independent module,
 * the terms and conditions of the license of that module.  An independent
 * module is a module which is not derived from or based on this library.
 * If you modify this library, you may extend this exception to your version
 * of the library, but you are not obligated to do so.  If you do not wish
 * to do so, delete this exception statement from your version.
 */

package net.phyloviz.core.wizard;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.text.html.HTMLDocument;
import net.phyloviz.core.util.TypingFactory;
import org.openide.util.Lookup;

public final class LoadDataSetVisualPanel1 extends JPanel {

	private DefaultComboBoxModel typeListModel;

	/** Creates new form LoadDataSetVisualPanel1 */
	public LoadDataSetVisualPanel1() {

		typeListModel = new DefaultComboBoxModel();
		LinkedList<TypingFactory> result = new LinkedList<TypingFactory>(Lookup.getDefault().lookupAll(TypingFactory.class));
		Collections.sort(result, new Comparator<TypingFactory>() {

			@Override
			public int compare(TypingFactory o1, TypingFactory o2) {
				String op1 = org.openide.util.NbBundle.getMessage(o1.getClass(), "TypingFactory.priority") ;
				String op2 = org.openide.util.NbBundle.getMessage(o2.getClass(), "TypingFactory.priority") ;
				int r = 0;
				if (op1 != null && op2 != null)
					r = op1.compareTo(op2);
				
				return (r == 0) ? o1.toString().compareTo(o2.toString()) : r;
			}
			
		});
		Iterator<? extends TypingFactory> ir = result.iterator();
		while (ir.hasNext()) {
			typeListModel.addElement(ir.next());
		}

		initComponents();
		
		URL url = result.getFirst().getDescription();
		if (url == null)
			url = LoadDataSetVisualPanel1.class.getResource("LoadDataSetVisualPanel1.html");

		try {
			jEditorPane1.setPage(url);
		} catch (IOException e) {
			// Do nothing...
			System.err.println(e.getMessage());
		}

		Font font = UIManager.getFont("Label.font");
		String bodyRule = "body { font-family: " + font.getFamily() + "; "
			+ "font-size: " + font.getSize() + "pt; width: " + jPanel2.getParent().getSize().width + "px;}";
		((HTMLDocument) jEditorPane1.getDocument()).getStyleSheet().addRule(bodyRule);

		jComboBox1.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent event) {
				TypingFactory tf = (TypingFactory) jComboBox1.getSelectedItem();
				URL url = tf.getDescription();
				if (url == null)
					url = LoadDataSetVisualPanel1.class.getResource("LoadDataSetVisualPanel1.html");

				try {
					jEditorPane1.setPage(url);
				} catch (IOException e) {
					// Do nothing...
					System.err.println(e.getMessage());
				}
				Font font = UIManager.getFont("Label.font");
				String bodyRule = "body { font-family: " + font.getFamily() + "; "
					+ "font-size: " + font.getSize() + "pt; width: " + jPanel2.getParent().getSize().width + "px;}";
				((HTMLDocument) jEditorPane1.getDocument()).getStyleSheet().addRule(bodyRule);
			}
		});
	}

	@Override
	public String getName() {
		return "Dataset";
	}

	public String getDatasetName() {
		return jTextField1.getText();
	}

	public TypingFactory getTypingFactory() {
		return (TypingFactory) jComboBox1.getSelectedItem();
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
        jComboBox1 = new javax.swing.JComboBox();
        jPanel5 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jEditorPane1 = new javax.swing.JEditorPane();

        setLayout(new java.awt.BorderLayout());

        jPanel2.setLayout(new java.awt.BorderLayout());

        jPanel3.setLayout(new java.awt.GridLayout(2, 0, 0, 8));

        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, org.openide.util.NbBundle.getMessage(LoadDataSetVisualPanel1.class, "LoadDataSetVisualPanel1.jLabel1.text")); // NOI18N
        jLabel1.setBorder(javax.swing.BorderFactory.createEmptyBorder(2, 12, 2, 8));
        jPanel3.add(jLabel1);

        org.openide.awt.Mnemonics.setLocalizedText(jLabel2, org.openide.util.NbBundle.getMessage(LoadDataSetVisualPanel1.class, "LoadDataSetVisualPanel1.jLabel2.text")); // NOI18N
        jLabel2.setBorder(javax.swing.BorderFactory.createEmptyBorder(2, 12, 2, 8));
        jPanel3.add(jLabel2);

        jPanel2.add(jPanel3, java.awt.BorderLayout.WEST);

        jPanel4.setLayout(new java.awt.GridLayout(2, 0, 0, 8));

        jTextField1.setText(org.openide.util.NbBundle.getMessage(LoadDataSetVisualPanel1.class, "LoadDataSetVisualPanel1.jTextField1.text")); // NOI18N
        jPanel4.add(jTextField1);

        jComboBox1.setModel(typeListModel);
        jPanel4.add(jComboBox1);

        jPanel2.add(jPanel4, java.awt.BorderLayout.CENTER);
        jPanel2.add(jPanel5, java.awt.BorderLayout.EAST);

        add(jPanel2, java.awt.BorderLayout.PAGE_START);

        jScrollPane1.setBorder(null);
        jScrollPane1.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        jEditorPane1.setBackground(jPanel5.getBackground());
        jEditorPane1.setBorder(javax.swing.BorderFactory.createEmptyBorder(8, 12, 8, 12));
        jEditorPane1.setContentType(org.openide.util.NbBundle.getMessage(LoadDataSetVisualPanel1.class, "LoadDataSetVisualPanel1.jEditorPane1.contentType")); // NOI18N
        jEditorPane1.setEditable(false);
        jEditorPane1.setMaximumSize(new java.awt.Dimension(200, 200));
        jScrollPane1.setViewportView(jEditorPane1);

        add(jScrollPane1, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox jComboBox1;
    private javax.swing.JEditorPane jEditorPane1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField jTextField1;
    // End of variables declaration//GEN-END:variables
}
