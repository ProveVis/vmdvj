package vmdv.dev.popup;

import java.util.Set;

import vmdv.control.Session;
import vmdv.dev.PopupItem;
import vmdv.dev.affects.ShowNodeInfoAffect;
import vmdv.model.AbstractNode;

public class ShowNodeInfoPopup extends PopupItem {

	public ShowNodeInfoPopup(String l) {
		super(l);
	}

	@Override
	public void action(Session session) {
		Set<AbstractNode> ns = session.getViewer().getSelectedNode();
		if(ns.size() == 1) {
			for(AbstractNode an: ns) {
				session.getViewer().affect.addLast(new ShowNodeInfoAffect(an));
			}
		}

	}

}
