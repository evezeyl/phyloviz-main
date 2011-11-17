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

package net.phyloviz.gtview.ui;

import java.util.ArrayList;
import java.util.Iterator;
import javax.swing.JMenuItem;
import net.phyloviz.category.CategoryChangeListener;
import net.phyloviz.category.CategoryProvider;
import net.phyloviz.core.data.DataSet;
import net.phyloviz.core.data.Profile;
import net.phyloviz.core.data.TypingData;
import net.phyloviz.goeburst.tree.GOeBurstMSTResult;
import net.phyloviz.gtview.render.ChartRenderer;
import org.openide.util.Lookup.Result;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.util.lookup.Lookups;
import org.openide.windows.TopComponent;

public class GTPanel2 extends TopComponent {

	private GraphView2 gv;
	private ArrayList<JMenuItem> al;
	private Result<CategoryProvider> r;
	private CategoryChangeListener gvCatListen;

	/** Creates new form GTPanel */
	public GTPanel2(String name, GOeBurstMSTResult gr, TypingData<? extends Profile> ds) {
		super(Lookups.singleton(gr));
		initComponents();
		this.setName(name);
		gv = new GraphView2(name, gr);
		this.add(gv);
		gv.startAnimation();

		gvCatListen = new CategoryChangeListener() {

			@Override
			public void categoryChange(CategoryProvider cp) {

				if (cp.isOn()) {
					gv.setDefaultRenderer(new ChartRenderer(cp, gv));
					gv.setCategoryProvider(cp);
				} else {
					gv.resetDefaultRenderer();
					gv.setCategoryProvider(null);
				}
			}
		};

		// Let us track category providers...
		// TODO: implement this within a renderer.
		r = ds.getLookup().lookupResult(CategoryProvider.class);
		Iterator<? extends CategoryProvider> i = r.allInstances().iterator();
		while (i.hasNext())
			i.next().addCategoryChangeListener(gvCatListen);

		r.addLookupListener(new LookupListener() {

			@SuppressWarnings("unchecked")
			@Override
			public void resultChanged(LookupEvent le) {

				Iterator<? extends CategoryProvider> i = ((Result<CategoryProvider>) le.getSource()).allInstances().iterator();
				while (i.hasNext()) {
					CategoryProvider cp = i.next();
					cp.removeCategoryChangeListener(gvCatListen);
					cp.addCategoryChangeListener(gvCatListen);
				}
			}
		});
	}

	@Override
	public int getPersistenceType() {
		return PERSISTENCE_NEVER;
	}

	@Override
	protected String preferredID() {
		return "GTPanel";
	}

	@Override
	protected void componentClosed() {
		gv.closeInfoPanel();
		gv.stopAnimation();
		super.componentClosed();
	}

	/** This method is called from within the constructor to
	 * initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is
	 * always regenerated by the Form Editor.
	 */
	@SuppressWarnings("unchecked")
        // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
        private void initComponents() {

                setLayout(new java.awt.BorderLayout());
        }// </editor-fold>//GEN-END:initComponents


        // Variables declaration - do not modify//GEN-BEGIN:variables
        // End of variables declaration//GEN-END:variables
}
