package vmdv.dev.popup;

import vmdv.control.Session;
import vmdv.dev.PopupItem;
import vmdv.dev.affects.HideAllLabelsAffect;

public class HideAllLabelsPopup extends PopupItem {

	public HideAllLabelsPopup(String l) {
		super(l);
	}

	@Override
	public void action(Session session) {
		session.getViewer().affect.addLast(new HideAllLabelsAffect());
	}

}
