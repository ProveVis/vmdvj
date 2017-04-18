package vmdv.paint.graph;

import vmdv.paint.stateGraphViewer.StateGraphVisualizeListener;

public class StateNode {
	protected String id;
	protected double size;
	protected XYZ xyz;
	protected RGBColor color;
	protected RGBColor oriColor;
	protected boolean visible;
	protected String label;
	protected boolean showLabel;
	protected boolean picked;
	protected float mess;
	protected XYZ force;
	protected XYZ speed;
	
	public float getMess() {
		return mess;
	}

	public void setMess(float mess) {
		this.mess = mess;
	}

	public XYZ getForce() {
		return force;
	}

	public void setForce(XYZ force) {
		this.force = force;
	}
	
	public void addForce(double xf, double yf, double zf) {
		force.addXyz(xf, yf, zf);
	}

	public XYZ getSpeed() {
		return speed;
	}

	public void setSpeed(XYZ speed) {
		this.speed = speed;
	}

	public StateNode(String id, String label) {
		this.xyz = new XYZ(0,0,0);
		this.oriColor = new RGBColor(StateGraphVisualizeListener.oriColor.getRed(), StateGraphVisualizeListener.oriColor.getGreen(), StateGraphVisualizeListener.oriColor.getBlue());
		this.color = new RGBColor(StateGraphVisualizeListener.oriColor.getRed(), StateGraphVisualizeListener.oriColor.getGreen(), StateGraphVisualizeListener.oriColor.getBlue());
		this.visible = true;
		this.id = id;
		this.size = 0.2;
		this.label = label;
		this.showLabel = false;
		this.picked = false;
		this.force = new XYZ(0,0,0);
		this.mess = 1.0f;
		this.speed = new XYZ(0,0,0);
	}
	
	public String getLabel() {
		return label;
	}
	
	public String getId() {
		return id;
	}
	
	public void setXYZ(double x, double y, double z) {
		xyz.setX(x);
		xyz.setY(y);
		xyz.setZ(z);
	}
	
	public void setX(double x) {
		xyz.setX(x);
	}
	
	public void setY(double y) {
		xyz.setY(y);
	}
	
	public void setZ(double z) {
		xyz.setZ(z);
	}
	
	public XYZ getXYZ() {
		return this.xyz;
	}
	
	public double getX() {
		return xyz.getX();
	}
	
	public double getY() {
		return xyz.getY();
	}
	
	public double getZ() {
		return xyz.getZ();
	}
	
	public void setColor(float red, float green, float blue) {
		color.setColor(red, green, blue);
	}
	
	public void setColor(RGBColor color) {
		this.color = color;
	}
	
	public RGBColor getColor() {
		return this.color;
	}
	
	public void clearColor() {
		color = new RGBColor(oriColor.getRed(), oriColor.getGreen(), oriColor.getBlue());
	}
	
	public double getSize() {
		return size;
	}
	
	public void resetSize() {
		size = 0.1;
	}
	
	public void setSize(double s) {
		size = s;
	}
	
	public boolean isVisible() {
		return visible;
	}
	
	public void setVisible(boolean v) {
		this.visible = v;
	}
	
	public boolean isLableVisible() {
		return showLabel;
	}
	
	public void setLabelVisible(boolean v) {
		showLabel = v;
	}
	
	public boolean isPicked() {
		return picked;
	}

	public void setPicked(boolean picked) {
		this.picked = picked;
	}

	@Override
	public boolean equals(Object obj) {
		if(obj instanceof StateNode) {
			if(id.equals(((StateNode) obj).id)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public int hashCode() {
		return id.hashCode();
	}
	
}
