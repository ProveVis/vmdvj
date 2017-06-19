package vmdv.dev.popup;

import vmdv.control.Session;
import vmdv.dev.PopupItem;

public class ResetEyePopup extends PopupItem {

	public ResetEyePopup(String l) {
		super(l);
	}

	@Override
	public void action(Session session) {
		session.getViewer().setEye(0, 0, 5);
	}
}
