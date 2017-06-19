package vmdv.paint.treeViewer;

import vmdv.paint.graph.RGBColor;
import vmdv.paint.graph.TreeNode;

import com.jogamp.opengl.GLAutoDrawable;

public class PickNodeAffect implements AssistAffect {
	private TreeVisualizer tv;
	private TreeNode node;
	private RGBColor color;
	
	public PickNodeAffect(TreeVisualizer tv, TreeNode node, RGBColor color) {
		this.tv = tv;
		this.color = color;
		this.node = node;
	}

	@Override
	public void affect(GLAutoDrawable gld) {
		node.setPicked(true);
		node.setColor(color.getRed(), color.getGreen(), color.getBlue());
		StringBuilder sb = new StringBuilder();
		sb.append("<html>");
		sb.append("<h3>Formula: "+node.getLabel()+"</h3><br>");
		int index = 1;
		for(TreeNode tn : tv.tree.getChildrenNodes(node)) {
			tn.showChildLabel = true;
			tn.setColor(tv.listener.red);
			tn.childLabel = "     Child "+index;
			sb.append("<h3>Child "+index+": "+tn.getLabel()+"</h3><br>");
			index++;
		}
		sb.append("</html>");
		tv.setTextLabel(sb.toString());
	}

}
