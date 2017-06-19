package vmdv.ui;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import vmdv.dev.affects.HighlightNodeAffect;
import vmdv.dev.affects.UnHighlightNodeAffect;
import vmdv.model.AbstractNode;

public class MouseHandler implements MouseListener {

	private Viewer viewer;
	
	public void setViewer(Viewer viewer) {
		this.viewer = viewer;
	}

	@Override
	public void mouseClicked(MouseEvent e) {
//		viewer.mousePosition = e.getPoint();
		AbstractNode selected = viewer.hoverNode;
		if (e.isMetaDown()) {//right click the mouse
			viewer.popupShowed = true;
			if(selected != null) {//showing node popup menu
				viewer.nodePop.show(e.getComponent(), e.getX(), e.getY());
			} else {//showing background popup menu
				viewer.backPop.show(e.getComponent(), e.getX(), e.getY());
			}
		} else {//left click the mouse
			viewer.popupShowed = false;
			if(selected != null) {
				if(viewer.singleSelection) {
					if(selected.picked) {
						viewer.affect.addLast(new UnHighlightNodeAffect(selected));
						viewer.nodesSelected.clear();
					} else {
						for(AbstractNode an: viewer.nodesSelected) {
							viewer.affect.addLast(new UnHighlightNodeAffect(an));
							viewer.nodesSelected.remove(an);
						}
						viewer.nodesSelected.add(selected);
						viewer.affect.addLast(new HighlightNodeAffect(selected));
					}
				} else {
					//multi selection
					if(selected.picked) {
						viewer.affect.addLast(new UnHighlightNodeAffect(selected));
						viewer.nodesSelected.remove(selected);
					} else {
						viewer.nodesSelected.add(selected);
						viewer.affect.addLast(new HighlightNodeAffect(selected));
					}
				}
			} else {
				for(AbstractNode an: viewer.nodesSelected) {
					viewer.affect.addLast(new UnHighlightNodeAffect(an));
					viewer.nodesSelected.remove(an);
				}
			}
		}
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mousePressed(MouseEvent e) {
		viewer.dragStartX = e.getX();
		viewer.dragStartY = e.getY();
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub

	}



}
