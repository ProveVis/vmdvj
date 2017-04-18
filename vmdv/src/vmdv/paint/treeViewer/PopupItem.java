package vmdv.paint.treeViewer;

public abstract class PopupItem {
	protected String label;
	
	public PopupItem(String l) {
		this.label = l;
	}
	
	public String getLabel() {
		return label;
	}
	
	public abstract void action(TreeVisualizer tv);
}
