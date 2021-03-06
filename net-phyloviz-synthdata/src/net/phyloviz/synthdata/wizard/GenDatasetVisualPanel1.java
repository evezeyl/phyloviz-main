/*-
 * Copyright (c) 2016, PHYLOViZ Team <phyloviz@gmail.com>
 * All rights reserved.
 * 
 * This file is part of PHYLOViZ <http://www.phyloviz.net/>.
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
 */

package net.phyloviz.synthdata.wizard;

import java.awt.Font;
import java.io.IOException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;

public final class GenDatasetVisualPanel1 extends JPanel {

    /** Creates new form GenDatasetVisualPanel1 */
    public GenDatasetVisualPanel1() {

	   initComponents();

	   try {
		  URL url = GenDatasetVisualPanel1.class.getResource("GenDatasetVisualPanel1.html");
		  jEditorPane1.setEditorKit(new HTMLEditorKit());
		  jEditorPane1.setPage(url);
		  Font font = UIManager.getFont("Label.font");
		  String bodyRule = "body { font-family: " + font.getFamily() + "; "
				+ "font-size: " + font.getSize() + "pt; width: " + jEditorPane1.getSize().width + "px;}";
		  ((HTMLDocument) jEditorPane1.getDocument()).getStyleSheet().addRule(bodyRule);
	   } catch (IOException e) {
		  Logger.getLogger(GenDatasetVisualPanel1.class.getName()).log(Level.WARNING,
				e.getLocalizedMessage());
	   }
    }

    @Override
    public String getName() {
	   return "Dataset";
    }

    public String getDatasetName() {
	   return jTextField1.getText();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jEditorPane1 = new javax.swing.JEditorPane();

        setLayout(new java.awt.BorderLayout());

        jPanel2.setMinimumSize(new java.awt.Dimension(130, 25));
        jPanel2.setPreferredSize(new java.awt.Dimension(130, 25));
        jPanel2.setLayout(new java.awt.BorderLayout());

        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, org.openide.util.NbBundle.getMessage(GenDatasetVisualPanel1.class, "GenDatasetVisualPanel1.jLabel1.text")); // NOI18N
        jLabel1.setBorder(javax.swing.BorderFactory.createEmptyBorder(2, 12, 2, 8));
        jPanel2.add(jLabel1, java.awt.BorderLayout.WEST);

        jTextField1.setText(org.openide.util.NbBundle.getMessage(GenDatasetVisualPanel1.class, "GenDatasetVisualPanel1.jTextField1.text")); // NOI18N
        jPanel2.add(jTextField1, java.awt.BorderLayout.CENTER);

        add(jPanel2, java.awt.BorderLayout.PAGE_START);

        jEditorPane1.setBackground(jPanel2.getBackground());
        jEditorPane1.setBorder(javax.swing.BorderFactory.createEmptyBorder(8, 12, 8, 12));
        jEditorPane1.setEditable(false);
        jEditorPane1.setMaximumSize(new java.awt.Dimension(200, 200));
        add(jEditorPane1, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JEditorPane jEditorPane1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JTextField jTextField1;
    // End of variables declaration//GEN-END:variables
}
