package vmdv.paint.graph;

public class TreeNode {
	private RGBColor oriColor = new RGBColor();
	private double oriSize;
	protected String paintId;
	public boolean showChildLabel = false;
	public String childLabel = "";
	public String getPaintId() {
		return paintId;
	}

	public void setPaintId(String paintId) {
		this.paintId = paintId;
	}

	protected String id;
	protected double size;
	protected XYZ xyz;
	protected RGBColor color;
	protected boolean visible;
	protected String label;
	public void setLabel(String label) {
		this.label = label;
	}

	protected boolean showLabel;
	protected boolean showSubtree;
	protected boolean picked;
	protected XYZ force;
	protected int depth;
	

	public XYZ getForce() {
		return force;
	}

	public void setForce(XYZ force) {
		this.force = force;
	}

	public TreeNode(String paintId, String id, String label) {
		this.xyz = new XYZ(0,0,0);
		this.color = new RGBColor(0,0,0);
		this.visible = true;
		this.paintId = paintId;
		this.id = id;
//		this.size = 0.2;
		this.label = label;
		this.showLabel = false;
		this.showSubtree = true;
		this.picked = false;
		this.force = new XYZ(0,0,0);
	}
	
	public RGBColor getOriColor() {
		return oriColor;
	}

	public void setOriColor(RGBColor oriColor) {
		this.oriColor = oriColor;
	}

	public double getOriSize() {
		return oriSize;
	}

	public void setOriSize(double oriSize) {
		this.oriSize = oriSize;
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
		this.color = new RGBColor(oriColor.getRed(), oriColor.getGreen(), oriColor.getBlue());
	}
	
	public double getSize() {
		return size;
	}
	
	
	public int getDepth() {
		return depth;
	}

	public void setDepth(int depth) {
		this.depth = depth;
	}

	public void resetSize() {
		size = oriSize;
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
	
	public boolean isShowSubtree() {
		return showSubtree;
	}

	public void setShowSubtree(boolean showSubtree) {
		this.showSubtree = showSubtree;
	}
	
	public boolean isPicked() {
		return picked;
	}

	public void setPicked(boolean picked) {
		this.picked = picked;
	}
	
	public void addForce(double xf, double yf, double zf) {
		force.addXyz(xf, yf, zf);
	}

	@Override
	public boolean equals(Object obj) {
		if(obj instanceof TreeNode) {
			if((id.equals(((TreeNode) obj).id) && paintId.equals(((TreeNode) obj).paintId))) {
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
