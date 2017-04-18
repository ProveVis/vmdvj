package vmdv.paint.treeViewer;

import java.util.LinkedList;

import com.jogamp.opengl.GLAutoDrawable;

import vmdv.paint.graph.Tree;
import vmdv.paint.graph.TreeNode;

public class ClearColorAffect implements AssistAffect {
	private TreeVisualizer tv;
	private Tree tree;
	
	public ClearColorAffect(TreeVisualizer tv, Tree tree) {
		this.tree = tree;
		this.tv = tv;
	}
	
	@Override
	public void affect(GLAutoDrawable gld) {
		// TODO Auto-generated method stub
		tv.setTextLabel("");
		LinkedList<TreeNode> looked = new LinkedList<TreeNode>();
		looked.addLast(tree.getRoot());
		while(!looked.isEmpty()) {
			TreeNode n = looked.removeFirst();
			n.showChildLabel = false;
			n.clearColor();
//			if(n.isPicked()) {
				n.setPicked(false);
				tv.operateListener.unHightLightState(n.getId());
//			}
			for(TreeNode tn : tree.getChildrenNodes(n)) {
				looked.addLast(tn);
			}
		}
//		tree.getRoot().setColor(TreeVisualizeListener.rootColor);
	}

}
