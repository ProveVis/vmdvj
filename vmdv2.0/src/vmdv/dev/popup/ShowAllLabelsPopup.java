package vmdv.dev.popup;

import vmdv.control.Session;
import vmdv.dev.PopupItem;
import vmdv.dev.affects.ShowAllLabelsAffect;

public class ShowAllLabelsPopup extends PopupItem {

	public ShowAllLabelsPopup(String l) {
		super(l);
	}

	@Override
	public void action(Session session) {
		session.getViewer().affect.addLast(new ShowAllLabelsAffect());
	}

}
