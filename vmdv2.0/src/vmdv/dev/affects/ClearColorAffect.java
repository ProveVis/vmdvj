package vmdv.dev.affects;

import java.util.LinkedList;

import vmdv.communicate.ClearColorRequest;
import vmdv.control.Session;
import vmdv.dev.AssistAffect;
import vmdv.model.AbstractGraph;
import vmdv.model.DiGraph;
import vmdv.model.DiNode;
import vmdv.model.Tree;
import vmdv.model.TreeNode;
import vmdv.ui.Viewer;

public class ClearColorAffect extends AssistAffect {

	@Override
	public void affect(Session session) {
		Viewer viewer = session.getViewer();
		AbstractGraph graph = viewer.getGraph();
		if(graph instanceof Tree) {
			Tree tree = (Tree)graph;
			LinkedList<TreeNode> looked = new LinkedList<TreeNode>();
			looked.addLast(tree.getRoot());
			while(!looked.isEmpty()) {
				TreeNode n = looked.removeFirst();
				n.showChildLabel = false;
				n.clearColor();
				n.resetSize();
//				if(n.isPicked()) {
					n.picked = false;
//					session.addRequestMsg(new UnHighlightNodeRequest(n.id));
//					tv.operateListener.unHightLightState(n.getId());
//				}
				for(TreeNode tn : tree.children(n)) {
					looked.addLast(tn);
				}
			}
			session.addRequestMsg(new ClearColorRequest());
		} else if(graph instanceof DiGraph) {
			DiGraph dgraph = (DiGraph)graph;
			for(DiNode dn: dgraph.getDiNodes()) {
				dn.clearColor();
				dn.resetSize();
			}
		}
	}

}
