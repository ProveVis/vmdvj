package vmdv.paint.treeViewer;

import com.jogamp.opengl.GLAutoDrawable;

import vmdv.paint.graph.Tree;
import vmdv.paint.graph.TreeNode;

public class ResetRootAffect implements AssistAffect {
	private Tree tree;
	private TreeNode tn;
	
	public ResetRootAffect(Tree tree, TreeNode tn) {
		this.tree = tree;
		this.tn = tn;
	}
	@Override
	public void affect(GLAutoDrawable gld) {
		if(tn != null) {
			tree.setRoot(tn);
		}
		
	}

}
