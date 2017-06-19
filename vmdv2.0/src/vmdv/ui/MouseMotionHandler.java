package vmdv.ui;

import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

public class MouseMotionHandler implements MouseMotionListener {

	private Viewer viewer;
	
	public void setViewer(Viewer viewer) {
		this.viewer = viewer;
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		int dragedX = e.getX() - viewer.dragStartX;
		int dragedY = e.getY() - viewer.dragStartY;
//		System.out.println("dragedX: "+dragedX+", dragedY: "+dragedY);
		viewer.phi = ((dragedX/10)+viewer.phi) % 360;
		viewer.theta = ((dragedY/10)+viewer.theta) % 360;
		double r = Math.sqrt(Math.pow(viewer.eyex,2)+Math.pow(viewer.eyey, 2)+Math.pow(viewer.eyez,2));
		viewer.eyez = r*Math.sin(viewer.theta*Math.PI/180)*Math.cos(viewer.phi*Math.PI/180);
		viewer.eyex = r*Math.sin(viewer.theta*Math.PI/180)*Math.sin(viewer.phi*Math.PI/180);
		viewer.eyey = r*Math.cos(viewer.theta*Math.PI/180);
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		viewer.mousePosition = e.getPoint();
	}


}
