package net.phyloviz.njviewer.ui;

import java.util.ArrayList;
import java.util.Iterator;
import javax.swing.JMenuItem;
import net.phyloviz.category.CategoryChangeListener;
import net.phyloviz.category.CategoryProvider;
import net.phyloviz.core.data.Profile;
import net.phyloviz.core.data.TypingData;
import net.phyloviz.nj.tree.NeighborJoiningItem;
import net.phyloviz.njviewer.render.ChartRenderer;
import net.phyloviz.upgmanjcore.visualization.GView;
import net.phyloviz.upgmanjcore.visualization.IGTPanel;
import net.phyloviz.upgmanjcore.visualization.PersistentVisualization;
import org.openide.util.Lookup.Result;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.util.lookup.Lookups;
import org.openide.windows.TopComponent;

public final class GTPanel extends TopComponent implements IGTPanel {

    private GraphView gv;
    private ArrayList<JMenuItem> al;
    private final Result<CategoryProvider> r;
    private CategoryChangeListener gvCatListen;
    private CategoryProvider catProvider;

    /**
     * Creates new form GTPanel
     */
    public GTPanel(String name, final NeighborJoiningItem njr, TypingData<? extends Profile> ds) {
        super(Lookups.singleton(njr));
        initComponents();
        this.setName(name);
        float distanceFilter = -1;
        PersistentVisualization pv = njr.getPersistentVisualization();
        if (pv != null) {
            distanceFilter = pv.distanceFilterValue;
            gv = new GraphView(name, njr);
            loadVisualization(pv);
        }
        else{
            gv = new GraphView(name, njr);
        }
        this.add(gv);
        gvCatListen = new CategoryChangeListener() {

            @Override
            public void categoryChange(CategoryProvider cp) {

                if (cp.isOn()) {
                    catProvider = cp;
                    gv.setDefaultRenderer(new ChartRenderer(cp, gv));
                    gv.setCategoryProvider(cp);
                } else {
                    catProvider = null;
                    gv.resetDefaultRenderer();
                    gv.setCategoryProvider(null);
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
        gv.loadGraph(njr.getRoot(), njr.getDistance(), distanceFilter);

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
        gv.closeInfoPanel();
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
    public PersistentVisualization getPersistentVisualization() {

        PersistentVisualization pc = new PersistentVisualization();
        
        pc.categoryProvider = catProvider;
        pc.distanceFilterValue = gv.getDistanceFilterValue();
        pc.linearSize = gv.getLinearSize();
        return pc;
    }

    @Override
    public void loadVisualization(PersistentVisualization pv) {

        if(pv.categoryProvider != null){
            catProvider = pv.categoryProvider;
            gv.setDefaultRenderer( new ChartRenderer(pv.categoryProvider, gv));
            gv.setCategoryProvider(pv.categoryProvider);
        }
        
        if(pv.distanceFilterValue != -1){
            gv.setDistanceFilterValue(pv.distanceFilterValue);
        }
        if(pv.linearSize){
            gv.setLinearSize(pv.linearSize);
        }
    }
}
