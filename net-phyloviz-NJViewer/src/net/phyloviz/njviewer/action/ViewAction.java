package net.phyloviz.njviewer.action;

import java.beans.PropertyChangeEvent;
import javax.swing.SwingUtilities;
import net.phyloviz.core.data.Profile;
import net.phyloviz.core.data.TypingData;
import net.phyloviz.nj.tree.NeighborJoiningItem;
import net.phyloviz.njviewer.ui.GTPanel;
import net.phyloviz.project.ProjectItem;
import org.openide.awt.StatusDisplayer;
import org.openide.nodes.Node;
import org.openide.nodes.NodeEvent;
import org.openide.nodes.NodeListener;
import org.openide.nodes.NodeMemberEvent;
import org.openide.nodes.NodeReorderEvent;
import org.openide.util.HelpCtx;
import org.openide.util.actions.NodeAction;
import org.openide.windows.TopComponent;

public class ViewAction extends NodeAction {

	@Override
	protected void performAction(Node[] nodes) {
		NeighborJoiningItem gr = (NeighborJoiningItem) nodes[0].getLookup().lookup(ProjectItem.class);
		
		//If a viewer is already open, just give it focus
		for (TopComponent tc : TopComponent.getRegistry().getOpened()) {
                    Object o = tc.getLookup().lookup(NeighborJoiningItem.class);
                    if (tc instanceof GTPanel)
                        if(tc.getLookup().lookup(NeighborJoiningItem.class) == gr) {
                            tc.requestActive();
                            return;
                    }
		}

		TypingData<? extends Profile> td = nodes[0].getParentNode().getLookup().lookup(TypingData.class);

		//Nope, need a new viewer
        	StatusDisplayer.getDefault().setStatusText("Starting display...");
		GTPanel tvp = new GTPanel(nodes[0].getParentNode().getDisplayName() + ": " + nodes[0].getDisplayName(), gr, td);
		tvp.open();
		tvp.requestActive();

                nodes[0].getParentNode().addNodeListener(new LocalNodeListener(tvp));
	
	}

	@Override
	protected boolean enable(Node[] nodes) {
		return nodes.length == 1;
	}

	@Override
	protected boolean asynchronous() {
		return false;
	}

	@Override
	public String getName() {
		return "View";
	}

	@Override
	public HelpCtx getHelpCtx() {
		return null;
	}

	private class LocalNodeListener implements NodeListener {

		private GTPanel tvp;

		LocalNodeListener(GTPanel tvp) {
			this.tvp = tvp;
		}

		@Override
		public void childrenAdded(NodeMemberEvent nme) {
		}

		@Override
		public void childrenRemoved(NodeMemberEvent nme) {
		}

		@Override
		public void childrenReordered(NodeReorderEvent nre) {
		}

		@Override
		public void nodeDestroyed(NodeEvent ne) {

			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					tvp.close();
				}
			});
		}

		@Override
		public void propertyChange(PropertyChangeEvent evt) {
		}
	}

}
