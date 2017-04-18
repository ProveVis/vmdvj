package vmdv.paint.treeViewer;

public class ClearProofTreePopup extends PopupItem {

	public ClearProofTreePopup(String l) {
		super(l);
	}

	@Override
	public void action(TreeVisualizer tv) {
		// TODO Auto-generated method stub
		tv.operateListener.clearProofTree();
	}

}
