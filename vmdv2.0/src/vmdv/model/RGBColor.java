package vmdv.model;

public class RGBColor {
	private float red = 1.0f;
	private float green = 1.0f;
	private float blue = 1.0f;
	
	public RGBColor(float red, float green, float blue) {
		this.red = red;
		this.green = green;
		this.blue = blue;
	}
	
	public RGBColor() {
	}
	
	public void setColor(float red, float green, float blue) {
		this.red = red;
		this.green = green;
		this.blue = blue;
	}
	
	public float getRed() {
		return red;
	}
	
	public float getGreen() {
		return green;
	}
	
	public float getBlue() {
		return blue;
	}
}
