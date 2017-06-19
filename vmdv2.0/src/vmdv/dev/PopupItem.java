package vmdv.dev;

import vmdv.control.Session;

public abstract class PopupItem {
	protected String label;
	
	public PopupItem(String l) {
		this.label = l;
	}
	
	public String getLabel() {
		return label;
	}
	
	public abstract void action(Session session);
}
