package vmdv.dev.affects;

import vmdv.control.Session;
import vmdv.dev.AssistAffect;
import vmdv.model.AbstractGraph;
import vmdv.model.DiGraph;
import vmdv.model.Tree;

public class AddNodeAffect extends AssistAffect {
	
	private String nid;
	private String label;
	private String nodeState;
	
	public AddNodeAffect(String nid, String label, String nodeState) {
		this.nid = nid;
		this.label = label;
		this.nodeState = nodeState;
	}

	@Override
	public void affect(Session session) {
		AbstractGraph graph = session.getGraph();
		if(graph instanceof Tree) {
			Tree tree = (Tree)graph;
			tree.addNode(nid, label, nodeState);
		} else if(graph instanceof DiGraph) {
			DiGraph diGraph = (DiGraph)graph;
			diGraph.addNode(nid, label);
		}
//		graph.addNode(nid, label);
	}

}
