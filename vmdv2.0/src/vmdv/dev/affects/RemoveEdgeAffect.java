package vmdv.dev.affects;

import vmdv.control.Session;
import vmdv.dev.AssistAffect;
import vmdv.model.AbstractGraph;

public class RemoveEdgeAffect extends AssistAffect {
	private String from_id, to_id;

	public RemoveEdgeAffect(String from_id, String to_id) {
		this.from_id = from_id;
		this.to_id = to_id;
	}

	@Override
	public void affect(Session session) {
		AbstractGraph graph = session.getGraph();
		graph.removeEdge(from_id, to_id);
	}

}
