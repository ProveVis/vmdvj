package vmdv.dev.affects;

import vmdv.control.Session;
import vmdv.dev.AssistAffect;
import vmdv.model.AbstractGraph;
import vmdv.model.AbstractNode;

public class HideAllLabelsAffect extends AssistAffect {

	@Override
	public void affect(Session session) {
		AbstractGraph graph = session.getGraph();
		for(AbstractNode an: graph.getNodes()) {
			an.showLabel = false;
		}
	}

}
