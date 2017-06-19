package vmdv.dev.affects;

import vmdv.communicate.UnHighlightNodeRequest;
import vmdv.control.Session;
import vmdv.dev.AssistAffect;
import vmdv.model.AbstractNode;
import vmdv.model.DiNode;
import vmdv.model.TreeNode;

public class UnHighlightNodeAffect extends AssistAffect {
	private AbstractNode node;

	public UnHighlightNodeAffect(AbstractNode node) {
		this.node = node;
	}

	@Override
	public void affect(Session session) {
		if(node instanceof TreeNode) {
			TreeNode tn = (TreeNode)node;
			tn.showChildLabel = false;
			tn.picked = false;
			tn.clearColor();
			session.addRequestMsg(new UnHighlightNodeRequest(tn.id));
		} else if(node instanceof DiNode) {
			DiNode dn = (DiNode)node;
			dn.picked = false;
			dn.clearColor();
		}
	}

}
