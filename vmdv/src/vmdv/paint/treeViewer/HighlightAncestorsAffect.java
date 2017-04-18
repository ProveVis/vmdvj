package vmdv.paint.treeViewer;

import java.util.LinkedList;

import com.jogamp.opengl.GLAutoDrawable;

import vmdv.paint.graph.Tree;
import vmdv.paint.graph.TreeNode;

public class HighlightAncestorsAffect implements AssistAffect {

	private TreeNode tn;
	private Tree tree;
	
	public HighlightAncestorsAffect(Tree tree, TreeNode tn) {
		this.tree = tree;
		this.tn = tn;
	}
	
	@Override
	public void affect(GLAutoDrawable gld) {
		// TODO Auto-generated method stub
		TreeNode tmpNode = tn;
		while(tmpNode != null) {
			TreeNode ttn = tree.getPreNode(tmpNode);
			if(ttn != null) {
				ttn.setColor(TreeVisualizeListener.highlight);
			}
			tmpNode = ttn;
		}
	}

}
