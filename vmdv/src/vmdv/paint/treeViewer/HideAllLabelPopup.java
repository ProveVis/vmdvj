package vmdv.paint.treeViewer;

public class HideAllLabelPopup extends PopupItem {

	public HideAllLabelPopup(String l) {
		super(l);
	}

	@Override
	public void action(TreeVisualizer tv) {
		tv.listener.addAffect(new HideAllLabelAffect(tv.tree));
	}

}
