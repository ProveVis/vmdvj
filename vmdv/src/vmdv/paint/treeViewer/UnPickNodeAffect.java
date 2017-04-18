package vmdv.paint.treeViewer;

import com.jogamp.opengl.GLAutoDrawable;

import vmdv.paint.graph.TreeNode;

public class UnPickNodeAffect implements AssistAffect {
	private TreeNode node;
	
	public UnPickNodeAffect(TreeNode node) {
		this.node = node;
	}


	@Override
	public void affect(GLAutoDrawable gld) {
		node.showChildLabel = false;
		node.setPicked(false);
		node.clearColor();
	}

}
