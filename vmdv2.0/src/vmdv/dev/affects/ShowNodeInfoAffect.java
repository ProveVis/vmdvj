package vmdv.dev.affects;

import javax.swing.JOptionPane;

import vmdv.control.Session;
import vmdv.dev.AssistAffect;
import vmdv.model.AbstractNode;

public class ShowNodeInfoAffect extends AssistAffect {
	private AbstractNode node;

	public ShowNodeInfoAffect(AbstractNode node) {
		this.node = node;
	}

	@Override
	public void affect(Session session) {
		if (node == null) {
			JOptionPane.showMessageDialog(session.getViewer(), "No node selected!", "Warning",
					JOptionPane.WARNING_MESSAGE);
		} else {
			JOptionPane.showMessageDialog(session.getViewer(), node.label, "Node " + node.id,
					JOptionPane.PLAIN_MESSAGE);
		}
	}
}
