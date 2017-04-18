package vmdv.paint.treeViewer;

public class ResetEyePopup extends PopupItem {

	public ResetEyePopup(String l) {
		super(l);
	}

	@Override
	public void action(TreeVisualizer tv) {
		tv.listener.setEye(0, 0, 5);
	}

}
