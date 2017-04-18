package vmdv.paint.treeViewer;

import com.jogamp.opengl.GLAutoDrawable;

import vmdv.paint.graph.Tree;
import vmdv.paint.graph.TreeNode;

public class SetAsRootAffect implements AssistAffect {
	private Tree tree;
	private TreeNode n;
	
	public SetAsRootAffect(Tree tree, TreeNode n) {
		this.tree = tree;
		this.n = n;
	}
	@Override
	public void affect(GLAutoDrawable gld) {
		tree.setRoot(n);
//		n.setXYZ(0, 0, 0);
//		tree.layout();
	}

}
