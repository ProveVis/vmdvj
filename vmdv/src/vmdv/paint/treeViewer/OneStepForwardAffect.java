package vmdv.paint.treeViewer;

import vmdv.paint.graph.TreeNode;

import com.jogamp.opengl.GLAutoDrawable;

public class OneStepForwardAffect implements AssistAffect {
	private TreeVisualizer tv;
	private TreeNode nodeSelected;

	public OneStepForwardAffect(TreeVisualizer tv, TreeNode nodeSelected) {
		this.tv = tv;
		this.nodeSelected = nodeSelected;
	}

	@Override
	public void affect(GLAutoDrawable gld) {
		// TODO Auto-generated method stub
		tv.operateListener.oneStepForward(nodeSelected.getPaintId(), nodeSelected.getId());
	}

}
