package vmdv.dev.popup;

import vmdv.control.Session;
import vmdv.dev.PopupItem;
import vmdv.dev.affects.ClearColorAffect;

public class ClearColorPopup extends PopupItem {

	public ClearColorPopup(String l) {
		super(l);
	}

	@Override
	public void action(Session session) {
		session.getViewer().affect.addLast(new ClearColorAffect());
	}

}
