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

package net.phyloviz.upgma.treeviewer;

/**
 *
 * @author Marta Nascimento
 */
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.KeyStroke;
import net.phyloviz.category.CategoryProvider;
import net.phyloviz.upgma.treeviewer.render.DistanceFilterEdgeRenderer;
import net.phyloviz.upgma.treeviewer.render.LabeledEdgeRenderer;
import net.phyloviz.upgma.treeviewer.render.NodeLinkLayout;
import net.phyloviz.upgma.treeviewer.render.NodeRenderer;
import net.phyloviz.upgma.treeviewer.render.OrthogonalEdgeRenderer;
import prefuse.Display;
import prefuse.Constants;
import prefuse.Visualization;
import prefuse.action.Action;
import prefuse.action.ActionList;
import prefuse.action.ItemAction;
import prefuse.action.RepaintAction;
import prefuse.action.animate.ColorAnimator;
import prefuse.action.animate.LocationAnimator;
import prefuse.action.animate.QualityControlAnimator;
import prefuse.action.animate.VisibilityAnimator;
import prefuse.action.assignment.ColorAction;
import prefuse.action.assignment.FontAction;
import prefuse.action.filter.VisibilityFilter;
import prefuse.action.layout.CollapsedSubtreeLayout;
import prefuse.activity.SlowInSlowOutPacer;
import prefuse.controls.FocusControl;
import prefuse.controls.PanControl;
import prefuse.controls.WheelZoomControl;
import prefuse.controls.ZoomControl;
import prefuse.controls.ZoomToFitControl;
import prefuse.data.Tree;
import prefuse.data.Tuple;
import prefuse.data.event.TupleSetListener;
import prefuse.data.search.PrefixSearchTupleSet;
import prefuse.data.tuple.TupleSet;
import prefuse.render.EdgeRenderer;
import prefuse.render.AbstractShapeRenderer;
import prefuse.render.DefaultRendererFactory;
import prefuse.render.Renderer;
import prefuse.util.ColorLib;
import prefuse.util.FontLib;
import prefuse.util.GraphicsLib;
import prefuse.util.display.DisplayLib;
import prefuse.util.ui.JSearchPanel;
import prefuse.visual.VisualItem;
import prefuse.visual.expression.InGroupPredicate;
import prefuse.visual.sort.TreeDepthItemSorter;

/**
 * Demonstration of a node-link tree viewer
 *
 * @version 1.0
 * @author <a href="http://jheer.org">jeffrey heer</a>
 */
public final class TreeView extends Display {

    public static final String TREE_CHI = "/chi-ontology.xml.gz";

    protected static final String tree = "tree";
    protected static final String treeNodes = "tree.nodes";
    protected static final String treeEdges = "tree.edges";

    private AbstractShapeRenderer m_nodeRenderer;
    private EdgeRenderer m_edgeRenderer;
    private ItemAction nodeColor;
    private String m_label = "label";
    private int m_orientation = Constants.ORIENT_LEFT_RIGHT;
    private ItemAction edgeColor;
    private DefaultRendererFactory rf;
    public transient CategoryProvider cp;
    private boolean labeledRender = false;
    private boolean linear = false;
    private boolean rescaleDistance = false;
    private final JSearchPanel searchPanel;
    private boolean searchMatch = false;
    public float cutDistance, maxDistance;
    private final String distanceProvider;
    private final int tdSize;

    public TreeView(Tree t, String label, int tdSize, float maxDistance, String distanceProvider) {
        super(new Visualization());
        this.maxDistance = maxDistance;
        this.distanceProvider = distanceProvider;
        m_label = label;
        this.tdSize = tdSize;
        m_vis.add(tree, t);

        m_nodeRenderer = new NodeRenderer(m_label, this);//new LabelRenderer(m_label);
        m_edgeRenderer = new OrthogonalEdgeRenderer(this);
        //new EdgeRenderer(Constants.EDGE_TYPE_LINE);

        rf = new DefaultRendererFactory(m_nodeRenderer);
        rf.add(new InGroupPredicate(treeEdges), m_edgeRenderer);
        m_vis.setRendererFactory(rf);

        // colors
        nodeColor = new NodeColorAction(treeNodes);
        ItemAction textColor = new ColorAction(treeNodes,
                VisualItem.TEXTCOLOR, ColorLib.rgb(0, 0, 0));
        m_vis.putAction("textColor", textColor);

        edgeColor = new ColorAction(treeEdges,
                VisualItem.STROKECOLOR, ColorLib.rgb(4, 58, 71));

        // quick repaint
        ActionList repaint = new ActionList();
        repaint.add(nodeColor);
        repaint.add(new RepaintAction());
        m_vis.putAction("repaint", repaint);

        // full paint
        ActionList fullPaint = new ActionList();
        fullPaint.add(nodeColor);
        m_vis.putAction("fullPaint", fullPaint);

        // animate paint change
        ActionList animatePaint = new ActionList(400);
        animatePaint.add(new ColorAnimator(treeNodes));
        animatePaint.add(new RepaintAction());
        m_vis.putAction("animatePaint", animatePaint);

        // create the tree layout action
        NodeLinkLayout treeLayout = new NodeLinkLayout(this, tree, m_orientation, 50, 0, 8);
        treeLayout.setLayoutAnchor(new Point2D.Double(25, 300));
        m_vis.putAction("treeLayout", treeLayout);

        CollapsedSubtreeLayout subLayout
                = new CollapsedSubtreeLayout(tree, m_orientation);
        m_vis.putAction("subLayout", subLayout);

        AutoPanAction autoPan = new AutoPanAction();

        // create the filtering and layout
        ActionList filter = new ActionList();
        filter.add(new VisibilityFilter(tree, m_predicate));
        filter.add(new FontAction(treeNodes, FontLib.getFont("Tahoma", 16)));
        filter.add(treeLayout);
        filter.add(subLayout);
        filter.add(textColor);
        filter.add(nodeColor);
        filter.add(edgeColor);
        m_vis.putAction("filter", filter);

        // animated transition
        ActionList animate = new ActionList(1000);
        animate.setPacingFunction(new SlowInSlowOutPacer());
        animate.add(autoPan);
        animate.add(new QualityControlAnimator());
        animate.add(new VisibilityAnimator(tree));
        animate.add(new LocationAnimator(treeNodes));
        animate.add(new ColorAnimator(treeNodes));
        animate.add(new RepaintAction());
        m_vis.putAction("animate", animate);
        m_vis.alwaysRunAfter("filter", "animate");

        // create animator for orientation changes
        ActionList orient = new ActionList(2000);
        orient.setPacingFunction(new SlowInSlowOutPacer());
        orient.add(autoPan);
        orient.add(new QualityControlAnimator());
        orient.add(new LocationAnimator(treeNodes));
        orient.add(new RepaintAction());
        m_vis.putAction("orient", orient);

        // ------------------------------------------------
        // initialize the display
        setSize(700, 600);
        setItemSorter(new TreeDepthItemSorter());
        addControlListener(new ZoomToFitControl());
        addControlListener(new ZoomControl());
        addControlListener(new WheelZoomControl());
        addControlListener(new PanControl());
        addControlListener(new FocusControl(1));

        registerKeyboardAction(
                new OrientAction(Constants.ORIENT_LEFT_RIGHT),
                "left-to-right", KeyStroke.getKeyStroke("ctrl 1"), WHEN_FOCUSED);
        registerKeyboardAction(
                new OrientAction(Constants.ORIENT_TOP_BOTTOM),
                "top-to-bottom", KeyStroke.getKeyStroke("ctrl 2"), WHEN_FOCUSED);
        registerKeyboardAction(
                new OrientAction(Constants.ORIENT_RIGHT_LEFT),
                "right-to-left", KeyStroke.getKeyStroke("ctrl 3"), WHEN_FOCUSED);
        registerKeyboardAction(
                new OrientAction(Constants.ORIENT_BOTTOM_TOP),
                "bottom-to-top", KeyStroke.getKeyStroke("ctrl 4"), WHEN_FOCUSED);

        // ------------------------------------------------
        // filter graph and perform layout
        setOrientation(m_orientation);
        m_vis.run("filter");

        TupleSet search = new PrefixSearchTupleSet();
        m_vis.addFocusGroup(Visualization.SEARCH_ITEMS, search);
        search.addTupleSetListener(new TupleSetListener() {

            @Override
            public void tupleSetChanged(TupleSet t, Tuple[] add, Tuple[] rem) {
                searchMatch = true;
                m_vis.cancel("animatePaint");
                m_vis.run("fullPaint");
                m_vis.run("animatePaint");
            }
        });

        searchPanel = new NodeSearchPanel(m_vis);
        searchPanel.setShowResultCount(true);
//        

        //rotate(new Point(200,300), Math.PI);
    }

    public JSearchPanel getSearchPanel() {
        return searchPanel;
    }

    public Renderer getNodeRenderer() {
        return m_nodeRenderer;
    }

    public Visualization getViz() {
        return m_vis;
    }

    public Renderer getEdgeRenderer() {
        return m_edgeRenderer;
    }

    public String getDistanceProvider() {
        return distanceProvider;
    }
    // ------------------------------------------------------------------------

    public void setOrientation(int orientation) {
        NodeLinkLayout rtl
                = (NodeLinkLayout) m_vis.getAction("treeLayout");
        CollapsedSubtreeLayout stl
                = (CollapsedSubtreeLayout) m_vis.getAction("subLayout");
        switch (orientation) {
            case Constants.ORIENT_LEFT_RIGHT:
                ((NodeRenderer) m_nodeRenderer).setHorizontalAlignment(Constants.LEFT);
                m_edgeRenderer.setHorizontalAlignment1(Constants.RIGHT);
                m_edgeRenderer.setHorizontalAlignment2(Constants.LEFT);
                m_edgeRenderer.setVerticalAlignment1(Constants.CENTER);
                m_edgeRenderer.setVerticalAlignment2(Constants.CENTER);
                break;
            case Constants.ORIENT_RIGHT_LEFT:
                ((NodeRenderer) m_nodeRenderer).setHorizontalAlignment(Constants.RIGHT);
                m_edgeRenderer.setHorizontalAlignment1(Constants.LEFT);
                m_edgeRenderer.setHorizontalAlignment2(Constants.RIGHT);
                m_edgeRenderer.setVerticalAlignment1(Constants.CENTER);
                m_edgeRenderer.setVerticalAlignment2(Constants.CENTER);
                break;
            case Constants.ORIENT_TOP_BOTTOM:
                ((NodeRenderer) m_nodeRenderer).setHorizontalAlignment(Constants.CENTER);
                m_edgeRenderer.setHorizontalAlignment1(Constants.CENTER);
                m_edgeRenderer.setHorizontalAlignment2(Constants.CENTER);
                m_edgeRenderer.setVerticalAlignment1(Constants.BOTTOM);
                m_edgeRenderer.setVerticalAlignment2(Constants.TOP);
                break;
            case Constants.ORIENT_BOTTOM_TOP:
                ((NodeRenderer) m_nodeRenderer).setHorizontalAlignment(Constants.CENTER);
                m_edgeRenderer.setHorizontalAlignment1(Constants.CENTER);
                m_edgeRenderer.setHorizontalAlignment2(Constants.CENTER);
                m_edgeRenderer.setVerticalAlignment1(Constants.TOP);
                m_edgeRenderer.setVerticalAlignment2(Constants.BOTTOM);
                break;
            default:
                throw new IllegalArgumentException(
                        "Unrecognized orientation value: " + orientation);
        }
        m_orientation = orientation;
        rtl.setOrientation(orientation);
        stl.setOrientation(orientation);
    }

    public int getOrientation() {
        return m_orientation;
    }

    public double getScaleX() {
        return ((NodeLinkLayout) m_vis.getAction("treeLayout")).getScaleX();
    }

    public double getScaleY() {
        return ((NodeLinkLayout) m_vis.getAction("treeLayout")).getScaleY();
    }

    void changeDistance(int d) {
        m_vis.cancel("animatePaint");

        NodeLinkLayout rtl = (NodeLinkLayout) m_vis.getAction("treeLayout");

        rtl.setScaleX(d);
        m_edgeRenderer = new DistanceFilterEdgeRenderer(this, cutDistance, rtl.getScaleX(), labeledRender, maxDistance);

        rf = new DefaultRendererFactory(m_nodeRenderer);
        rf.add(new InGroupPredicate(treeEdges), m_edgeRenderer);

        m_vis.setRendererFactory(rf);
        m_vis.run("treeLayout");
        m_vis.run("fullPaint");
        m_vis.run("animatePaint");

    }

    void changeHigh(int h) {
        m_vis.cancel("animatePaint");

        NodeLinkLayout rtl = (NodeLinkLayout) m_vis.getAction("treeLayout");

        rtl.setScaleY(h);
        m_vis.run("treeLayout");
        m_vis.run("fullPaint");
        m_vis.run("animatePaint");

    }

    void cutDistance(float value, float max) {
        cutDistance = value;
        m_vis.cancel("animatePaint");
        NodeLinkLayout rtl = (NodeLinkLayout) m_vis.getAction("treeLayout");
        m_edgeRenderer = new DistanceFilterEdgeRenderer(this, value, rtl.getScaleX(), labeledRender, max);

        rf = new DefaultRendererFactory(m_nodeRenderer);
        rf.add(new InGroupPredicate(treeEdges), m_edgeRenderer);

        m_vis.setRendererFactory(rf);
        m_vis.run("treeLayout");
        m_vis.run("draw");
        m_vis.run("fullPaint");
        m_vis.run("animatePaint");

        m_vis.cancel("animatePaint");
        m_vis.run("treeLayout");
        m_vis.run("fullPaint");
        m_vis.run("animatePaint");
    }

    void setDistanceLabel(boolean status) {
        labeledRender = status;
        m_vis.cancel("animatePaint");

        rf = new DefaultRendererFactory(m_nodeRenderer);

        if (status) {
            m_edgeRenderer = new LabeledEdgeRenderer(this);
        } else {
            m_edgeRenderer = new OrthogonalEdgeRenderer(this);
        }

        rf.add(new InGroupPredicate(treeEdges), m_edgeRenderer);

        m_vis.setRendererFactory(rf);
        m_vis.run("treeLayout");
        m_vis.run("draw");
        m_vis.run("fullPaint");
        m_vis.run("animatePaint");
    }

    public boolean getRescaleEdges() {
        return rescaleDistance;
    }

    public void setRescaleEdges(boolean status) {
        rescaleDistance = status;
        m_vis.cancel("animatePaint");

        m_vis.run("treeLayout");
        // m_vis.run("animate");
        m_vis.run("draw");
        m_vis.run("fullPaint");
        m_vis.run("animatePaint");

        double x = rescaleDistance ? Math.log(1 + maxDistance) * getScaleX() : maxDistance * getScaleX();
        double y = tdSize * getScaleY();
        Point2D.Double p = new Point2D.Double(x / 2, y);
        panToAbs(p);
    }

    public boolean getLinearSize() {
        return linear;
    }

    void setLinearSize(boolean status) {
        linear = status;
        m_vis.cancel("animatePaint");

        m_vis.run("treeLayout");
        m_vis.run("draw");
        m_vis.run("fullPaint");
        m_vis.run("animatePaint");
    }

    void setDefaultRenderer(AbstractShapeRenderer r) {
        m_vis.cancel("treeLayout");
        m_vis.cancel("static");
        m_vis.cancel("animatePaint");

        m_nodeRenderer = r;
        rf = new DefaultRendererFactory(m_nodeRenderer);
        rf.add(new InGroupPredicate(treeEdges), m_edgeRenderer);
        rf.setDefaultRenderer(r);

        m_vis.setRendererFactory(rf);
        m_vis.run("treeLayout");
        m_vis.run("draw");
        m_vis.run("fullPaint");
        m_vis.run("animatePaint");
    }

    void resetDefaultRenderer() {
        m_vis.cancel("treeLayout");
        m_vis.cancel("static");
        m_vis.cancel("animatePaint");

        m_nodeRenderer = new NodeRenderer(m_label, this);//new LabelRenderer(m_label);
        rf = new DefaultRendererFactory(m_nodeRenderer);
        rf.add(new InGroupPredicate(treeEdges), m_edgeRenderer);
        rf.setDefaultRenderer(m_nodeRenderer);

        m_vis.setRendererFactory(rf);
        m_vis.run("treeLayout");
        m_vis.run("draw");
        m_vis.run("fullPaint");
        m_vis.run("animatePaint");
    }

    public void setCategoryProvider(CategoryProvider cp) {
        this.cp = cp;
    }

    // ------------------------------------------------------------------------
    // ------------------------------------------------------------------------
    private class OrientAction extends AbstractAction {

        private final int orientation;

        public OrientAction(int orientation) {
            this.orientation = orientation;
        }

        @Override
        public void actionPerformed(ActionEvent evt) {
            setOrientation(orientation);
            getVisualization().cancel("orient");
            getVisualization().run("treeLayout");
            getVisualization().run("orient");
        }
    }

    public class AutoPanAction extends Action {

        private final Point2D m_start = new Point2D.Double();
        private final Point2D m_end = new Point2D.Double();
        private final Point2D m_cur = new Point2D.Double();
        private final int m_bias = 150;

        @Override
        public void run(double frac) {
            TupleSet ts = m_vis.getFocusGroup(Visualization.FOCUS_ITEMS);
            if (ts.getTupleCount() == 0) {
                return;
            }

            if (frac == 0.0) {
                int xbias = 0, ybias = 0;
                switch (m_orientation) {
                    case Constants.ORIENT_LEFT_RIGHT:
                        xbias = m_bias;
                        break;
                    case Constants.ORIENT_RIGHT_LEFT:
                        xbias = -m_bias;
                        break;
                    case Constants.ORIENT_TOP_BOTTOM:
                        ybias = m_bias;
                        break;
                    case Constants.ORIENT_BOTTOM_TOP:
                        ybias = -m_bias;
                        break;
                }

                VisualItem vi = (VisualItem) ts.tuples().next();
                m_cur.setLocation(getWidth() / 2, getHeight() / 2);
                getAbsoluteCoordinate(m_cur, m_start);
                m_end.setLocation(vi.getX() + xbias, vi.getY() + ybias);
            } else {
                m_cur.setLocation(m_start.getX() + frac * (m_end.getX() - m_start.getX()),
                        m_start.getY() + frac * (m_end.getY() - m_start.getY()));
                panToAbs(m_cur);
            }
        }
    }

    private class NodeColorAction extends ColorAction {

        public NodeColorAction(String group) {
            super(group, VisualItem.FILLCOLOR);
        }

        @Override
        public int getColor(VisualItem item) {
            Tuple itemTuple = item.getSourceTuple();
            if (m_vis.isInGroup(item, Visualization.SEARCH_ITEMS)) {
                if (itemTuple.getString("p_id").equals(searchPanel.getQuery())) {
                    if (searchMatch) {
                        TreeView.this.panToAbs(new Point2D.Double(item.getX(), item.getY()));
                    }
                    searchMatch = false;
                }
                return ColorLib.rgb(255, 190, 190);
            } else if (m_vis.isInGroup(item, Visualization.FOCUS_ITEMS)) {
                return ColorLib.rgb(198, 229, 229);
            } else if (item.getDOI() > -1) {
                if (item.getBoolean("hide")) {
                    return ColorLib.rgba(164, 193, 193, 0);
                }
                return ColorLib.rgb(164, 193, 193);
            } else {
                return ColorLib.rgba(255, 255, 255, 0);
            }
        }

    } // end of inner class TreeMapColorAction

    private class NodeSearchPanel extends JSearchPanel {

        private static final long serialVersionUID = 1L;

        NodeSearchPanel(Visualization view) {
            super(view, treeNodes, Visualization.SEARCH_ITEMS, "p_id", true, true);
            setShowResultCount(false);
            setBorder(BorderFactory.createEmptyBorder(5, 5, 4, 0));
            setFont(FontLib.getFont("Tahoma", Font.PLAIN, 11));
            setBackground(Color.WHITE);
            setForeground(Color.BLACK);
            requestFocus();
        }
    }

} // end of class TreeMap

