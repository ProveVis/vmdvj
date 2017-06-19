package vmdv.config;

import vmdv.model.RGBColor;

public interface ColorConfig {
	public static final RGBColor red = new RGBColor(1,0,0);
	public static final RGBColor green = new RGBColor(0,1,0);
	public static final RGBColor blue = new RGBColor(0,0,1);
//	public static final RGBColor highlight = new RGBColor(198.0f/255,145.0f/255,69.0f/255);
	public static final RGBColor highlight = new RGBColor(1,0,0);
//	public static final RGBColor rootColor = new RGBColor(56.0f/255,94.0f/255,15.0f/255);
	public static final RGBColor rootColor = new RGBColor(0,0,0);
	public static final RGBColor oriColor = new RGBColor(0,0,0);
	public static final RGBColor hoverColor = new RGBColor(186.0f/255,52.0f/255,10.0f/255);
	public static final RGBColor stepColor = new RGBColor(1,0,0);
//	public static final RGBColor fromColor = new RGBColor(12.0f/255,22.0f/255,146.0f/255);
	public static final RGBColor fromColor = new RGBColor(44.0f/255,82.0f/255,68.0f/255);
	public static final RGBColor toColor = new RGBColor(0,1,0);
	public static final RGBColor same_subtree_color = new RGBColor(199.0f/255, 134.0f/255, 5.0f/255);
	public static final RGBColor childColor = new RGBColor(183.0f/255,143.0f/255,71.0f/255);
}
