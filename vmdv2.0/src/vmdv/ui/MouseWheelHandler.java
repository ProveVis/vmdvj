package vmdv.ui;

import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

public class MouseWheelHandler implements MouseWheelListener {
	private Viewer viewer;
	
	public void setViewer(Viewer viewer) {
		this.viewer = viewer;
	}
	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		int step = e.getWheelRotation();
		double current = Math.sqrt(Math.pow(viewer.eyex, 2) + Math.pow(viewer.eyey, 2) + Math.pow(viewer.eyez, 2));
		if(current < 1.0 && step < 0) {
			return;
		}
		viewer.eyex += viewer.eyex / current * step;
		viewer.eyey += viewer.eyey / current * step;
		viewer.eyez += viewer.eyez / current * step;
	}
}
