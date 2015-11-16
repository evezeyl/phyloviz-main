///*-
// * Copyright (c) 2011, PHYLOViZ Team <phyloviz@gmail.com>
// * All rights reserved.
// * 
// * This file is part of PHYLOViZ <http://www.phyloviz.net>.
// *
// * This program is free software: you can redistribute it and/or modify it
// * under the terms of the GNU General Public License as published by the
// * Free Software Foundation, either version 3 of the License, or (at your
// * option) any later version.
// * 
// * This program is distributed in the hope that it will be useful, but
// * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
// * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
// * for more details.
// * 
// * You should have received a copy of the GNU General Public License along
// * with this program.  If not, see <http://www.gnu.org/licenses/>.
// * 
// * Linking this library statically or dynamically with other modules is
// * making a combined work based on this library.  Thus, the terms and
// * conditions of the GNU General Public License cover the whole combination.
// * 
// * As a special exception, the copyright holders of this library give you
// * permission to link this library with independent modules to produce an
// * executable, regardless of the license terms of these independent modules,
// * and to copy and distribute the resulting executable under terms of your
// * choice, provided that you also meet, for each linked independent module,
// * the terms and conditions of the license of that module.  An independent
// * module is a module which is not derived from or based on this library.
// * If you modify this library, you may extend this exception to your version
// * of the library, but you are not obligated to do so.  If you do not wish
// * to do so, delete this exception statement from your version.
// */
//
//package net.phyloviz.gtview.ui;
//
//import java.util.ArrayList;
//import javax.swing.JComponent;
//import javax.swing.JPanel;
//import net.phyloviz.upgmanjcore.visualization.ForcePair;
//import net.phyloviz.upgmanjcore.visualization.InfoPanel;
//import prefuse.Visualization;
//import prefuse.action.layout.graph.ForceDirectedLayout;
//import prefuse.util.ui.JForcePanel;
//
//public abstract class GView extends JPanel {
//
//	public abstract JComponent getDisplay();
//
//	public abstract Visualization getVisualization();
//
//	public abstract boolean getLinearSize();
//
//	public abstract void setLinearSize(boolean status);
//	
//	public abstract void setLevelLabel(boolean status);
//    
//        public abstract void setEdgePercentageLabel(boolean status);
//
//	public abstract boolean showLabel();
//
//	public abstract void setShowLabel(boolean status);
//
//	public abstract void setHighQuality(boolean status);
//
//	public abstract JForcePanel getForcePanel();
//
//	public abstract ForceDirectedLayout getForceLayout();
//
//        public abstract ArrayList<ForcePair> getForces();
//
//	public abstract void showGroupPanel(boolean status);
//
//	public abstract void showInfoPanel();
//
//	public abstract void closeInfoPanel();
//
//	public abstract InfoPanel getInfoPanel();
//}
