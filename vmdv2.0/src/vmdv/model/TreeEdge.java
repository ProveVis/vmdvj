package vmdv.model;

public class TreeEdge {
	public TreeNode from;
	public TreeNode to;
	public String label;
	public RGBColor color;
	public float size;

	public TreeEdge(TreeNode pn, TreeNode cn, String label) {
		this.from = pn;
		this.to = cn;
		this.label = label;
		this.color = new RGBColor(0,0,0);
		this.size = 1.0f;
	}


	public void clearColor() {
		color = new RGBColor();
	}
}
