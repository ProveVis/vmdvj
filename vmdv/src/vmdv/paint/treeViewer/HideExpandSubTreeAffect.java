package vmdv.paint.treeViewer;

import com.jogamp.opengl.GLAutoDrawable;

import vmdv.paint.graph.TreeNode;

public class HideExpandSubTreeAffect implements AssistAffect {
	private TreeNode n;

	public HideExpandSubTreeAffect(TreeNode n) {
		this.n = n;
	}

	@Override
	public void affect(GLAutoDrawable gld) {
		if (n.isShowSubtree()) {
			n.setColor(0, 1, 0);
			n.setShowSubtree(false);
		} else {
			n.clearColor();
			n.setShowSubtree(true);
		}
	}

}
