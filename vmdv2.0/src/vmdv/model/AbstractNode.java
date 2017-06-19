package vmdv.model;

public abstract class AbstractNode {
	public RGBColor oriColor = new RGBColor(0,0,0);
	public double oriSize;

	public String id;
	public double size;
	public XYZ xyz;
	public RGBColor color;
	public boolean visible;
	public String label;

	public boolean showLabel;
	public boolean picked;
	public XYZ force;
	// public int depth;

	public AbstractNode(String id, String label) {
		this.xyz = new XYZ(0, 0, 0);
		this.color = new RGBColor(0, 0, 0);
		this.visible = true;
		this.id = id;
		this.oriSize = 0.2;
		this.size = 0.2;
		this.label = label;
		this.showLabel = false;
		this.picked = false;
		this.force = new XYZ(0, 0, 0);
	}
	
	public void clearColor() {
		this.color = new RGBColor(oriColor.getRed(), oriColor.getGreen(), oriColor.getBlue());
	}
	
	public void resetSize() {
		this.size = oriSize;
	}

	public void addForce(double d, double e, double f) {
		force.addXyz(d, e, f);
	}
	
	public void setForce(double x, double y, double z) {
		force = new XYZ(x, y, z);
	}

	public void setXYZ(double d, double e, double f) {
		xyz.setX(d);
		xyz.setY(e);
		xyz.setZ(f);		
	}
}
