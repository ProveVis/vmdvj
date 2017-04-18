package vmdv.paint.treeViewer;

import java.util.LinkedList;

import vmdv.paint.graph.Tree;
import vmdv.paint.graph.TreeNode;

import com.jogamp.opengl.GLAutoDrawable;

public class HideAllLabelAffect implements AssistAffect {
	private Tree tree;
	
	public HideAllLabelAffect(Tree tree) {
		this.tree = tree;
	}
	@Override
	public void affect(GLAutoDrawable gld) {
		LinkedList<TreeNode> looked = new LinkedList<TreeNode>();
		looked.addLast(tree.getRoot());
		while(!looked.isEmpty()) {
			TreeNode n = looked.removeFirst();
			n.setLabelVisible(false);
			for(TreeNode tn : tree.getChildrenNodes(n)) {
				looked.addLast(tn);
			}
		}
	}

}
