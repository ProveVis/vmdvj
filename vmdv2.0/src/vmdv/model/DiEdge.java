package vmdv.model;

public class DiEdge {

	public DiNode from;
	public DiNode to;
	public RGBColor color;
	public float size;

	public DiEdge(DiNode pn, DiNode cn) {
		this.from = pn;
		this.to = cn;
		this.color = new RGBColor(0,0,0);
		this.size = 1.0f;
	}

	public void clearColor() {
		color = new RGBColor();
	}
}
