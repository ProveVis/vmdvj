package vmdv.paint.treeViewer;

public class ShowAllLabelPopup extends PopupItem {

	public ShowAllLabelPopup(String l) {
		super(l);
	}

	@Override
	public void action(TreeVisualizer tv) {
		tv.listener.addAffect(new ShowAllLabelAffect(tv.tree));
	}

}
