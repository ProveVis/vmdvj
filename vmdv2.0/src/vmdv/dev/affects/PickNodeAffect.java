package vmdv.dev.affects;

import vmdv.config.ColorConfig;
import vmdv.control.Session;
import vmdv.dev.AssistAffect;
import vmdv.model.AbstractGraph;
import vmdv.model.AbstractNode;
import vmdv.model.DiGraph;
import vmdv.model.RGBColor;
import vmdv.model.Tree;
import vmdv.model.TreeNode;

public class PickNodeAffect extends AssistAffect {
	private AbstractNode node;
	
	public PickNodeAffect(AbstractNode node) {
		this.node = node;
	}
	

	@Override
	public void affect(Session session) {
		AbstractGraph graph = session.getGraph();
		if(graph instanceof Tree) {
			Tree tree = (Tree)graph;
			node.picked = true;
			RGBColor color = ColorConfig.red;
			node.color = new RGBColor(color.getRed(), color.getGreen(), color.getBlue());
//			StringBuilder sb = new StringBuilder();
//			sb.append("<html>");
//			sb.append("<h3>"+node.getLabel()+"</h3><br>");
			int index = 1;
			for(TreeNode tn : tree.children((TreeNode)node)) {
//				TreeNode tn = e.getTo();
				tn.showChildLabel = true;
				tn.color = color;
				tn.childLabel = "     Child "+index;
//				sb.append("<h3>Child "+index+": "+tn.getLabel()+"</h3><br>");
				index++;
			}
//			sb.append("</html>");
		} else if(graph instanceof DiGraph) {
			node.picked = true;
			RGBColor color = ColorConfig.red;
			node.color = new RGBColor(color.getRed(), color.getGreen(), color.getBlue());
		}
		
		

	}

}
