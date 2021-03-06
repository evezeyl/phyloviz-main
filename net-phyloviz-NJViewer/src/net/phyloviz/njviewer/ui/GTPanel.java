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

package net.phyloviz.njviewer.ui;

import java.util.ArrayList;
import java.util.Iterator;
import javax.swing.JMenuItem;
import net.phyloviz.category.CategoryChangeListener;
import net.phyloviz.category.CategoryProvider;
import net.phyloviz.core.data.DataModel;
import net.phyloviz.core.data.Profile;
import net.phyloviz.core.data.TypingData;
import net.phyloviz.nj.tree.NJRoot;
import net.phyloviz.nj.tree.NeighborJoiningItem;
import net.phyloviz.njviewer.render.BarChartRenderer;
import net.phyloviz.njviewer.render.ChartRenderer;
import net.phyloviz.tview.TViewPanel;
import net.phyloviz.upgmanjcore.visualization.GView;
import net.phyloviz.upgmanjcore.visualization.IGTPanel;
import net.phyloviz.upgmanjcore.visualization.PersistentVisualization;
import net.phyloviz.upgmanjcore.visualization.Visualization;
import org.openide.util.Lookup.Result;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.util.lookup.Lookups;
import org.openide.windows.TopComponent;
import prefuse.render.AbstractShapeRenderer;

public final class GTPanel extends TopComponent implements IGTPanel {

    private GraphView gv;
    private ArrayList<JMenuItem> al;
    private final Result<CategoryProvider> r;
    private CategoryChangeListener gvCatListen;
    private CategoryProvider catProvider;
    private TypingData ds;
    private NeighborJoiningItem njr;

    /**
     * Creates new form GTPanel
     */
    public GTPanel(String name, final NeighborJoiningItem njr, TypingData<? extends Profile> ds) {
        super(Lookups.singleton(njr));
        initComponents();
        this.setName(name);
        this.ds = ds;
        this.njr = njr;
        float distanceFilter = -1;
        Visualization viz = njr.getVisualization();
        if (viz != null) {
            PersistentVisualization pv = viz.pv;
            if(pv != null){
                distanceFilter = pv.distanceFilterValue;
                gv = new GraphView(name, njr, pv.linearSize, pv.nodesPositions, pv.isRadialLayout);
                loadVisualization(viz);
            } else {
                gv = new GraphView(name, njr, false, null, true);
            }
        } else {
            gv = new GraphView(name, njr, false, null, true);
        }
        this.add(gv);
        gv.enableViewControl(gv.isRadial());
        gvCatListen = new CategoryChangeListener() {

            @Override
            public void categoryChange(CategoryProvider cp) {

                if (cp.isOn()) {
                    catProvider = cp;
                    boolean isRadial = gv.isRadial();
                    AbstractShapeRenderer chart = isRadial ? new BarChartRenderer(cp, gv) : new ChartRenderer(cp, gv);
                    gv.setDefaultRenderer(chart);
                    gv.setCategoryProvider(cp);
                    gv.enableViewControl(isRadial);
                } else {
                    catProvider = null;
                    gv.resetDefaultRenderer();
                    gv.setCategoryProvider(null);
                    gv.enableViewControl(gv.isRadial());
                }
            }
        };

        // Let us track category providers...
        // TODO: implement this within a renderer.
        r = ds.getLookup().lookupResult(CategoryProvider.class);
        Iterator<? extends CategoryProvider> i = r.allInstances().iterator();
        while (i.hasNext()) {
            i.next().addCategoryChangeListener(gvCatListen);
        }

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
        gv.loadGraph(njr.getRoot(), njr.getDistance(), distanceFilter, viz != null && viz.pv != null);

        double maxDist = Double.MIN_VALUE;
        double minDist = Double.MAX_VALUE;
        for(NJRoot.EdgeDistanceWrapper edge : njr.getRoot().getList()){
            double distance = edge.distance;
            if(distance > maxDist) maxDist = distance;
            if(distance < minDist) minDist = distance;
        }
        
//        clp = new ChartLegendPanel(new Dimension(128, 128), new CategoryProvider(minDist, maxDist, 10), 10);
//        clp.setName(name + " (Selection view)");
//        clp.open();
//        clp.requestActive();

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
        gv.stopAnimation();
        super.componentClosed();
        gv.closePanels();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setLayout(new java.awt.BorderLayout());
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
    @Override
    public GView getGView() {
        return gv;
    }

    @Override
    public Visualization getVisualization() {

        Visualization v = new Visualization();
        PersistentVisualization pv = new PersistentVisualization();

        if (catProvider != null && catProvider.isOn()) {

            for (TopComponent tc : TopComponent.getRegistry().getOpened()) {
                if (tc instanceof TViewPanel) {
                    TViewPanel tvp = (TViewPanel) tc;
                    TypingData td = tvp.ds.getLookup().lookup(TypingData.class);
                    if (ds == td) {
                        DataModel dm = catProvider.getDataModel();
                        if (dm == tvp.cp.getDataModel()) {
                            v.filter = tvp.getFilter();
                            v.category = catProvider;
                            break;
                        }
                    }
                }
            }
        }
        pv.distanceFilterValue = gv.getDistanceFilterValue();
        pv.linearSize = gv.getLinearSize();
        pv.nodesPositions = ((GraphView) gv).getNodesPositions();
        pv.isRadialLayout = gv.isRadial();
        v.pv = pv;

        return v;
    }

    @Override
    public void loadVisualization(Visualization viz) {
        if (viz.category != null) {
            catProvider = viz.category;

//            gv.setDefaultRenderer(new BarChartRenderer(catProvider, gv));
            gv.setCategoryProvider(catProvider);
        }

//        if (viz.pv.distanceFilterValue != -1) {
//            gv.setDistanceFilterValue(viz.pv.distanceFilterValue);
//        }
//        if (viz.pv.linearSize) {
//            gv.setLinearSize(viz.pv.linearSize);
//        }
        gv.repaint();

    }

}
