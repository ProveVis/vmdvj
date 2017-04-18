package vmdv.paint.treeViewer;

import javax.swing.JOptionPane;

import com.jogamp.opengl.GLAutoDrawable;

import vmdv.paint.graph.TreeNode;

public class ShowHideLabelAffect implements AssistAffect {
	private TreeNode n;
	
	public ShowHideLabelAffect(TreeNode n) {
		this.n = n;
	}
	@Override
	public void affect(GLAutoDrawable gld) {
//		System.out.println("Showing label of "+n.getId());
//		n.setLabelVisible(!n.isLableVisible());
		JOptionPane.showMessageDialog(null, n.getLabel(), "Formula", JOptionPane.PLAIN_MESSAGE);

	}

}
