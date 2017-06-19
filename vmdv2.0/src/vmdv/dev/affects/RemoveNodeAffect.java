package vmdv.dev.affects;

import vmdv.control.Session;
import vmdv.dev.AssistAffect;
import vmdv.model.AbstractGraph;

public class RemoveNodeAffect extends AssistAffect {
	private String nid;

	public RemoveNodeAffect(String nid) {
		this.nid = nid;
	}

	@Override
	public void affect(Session session) {
		AbstractGraph graph = session.getGraph();
		graph.removeNode(nid);
	}

}
