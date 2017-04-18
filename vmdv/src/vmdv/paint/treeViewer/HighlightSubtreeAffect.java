package vmdv.paint.treeViewer;

import java.util.LinkedList;

import vmdv.paint.graph.Tree;
import vmdv.paint.graph.TreeNode;

import com.jogamp.opengl.GLAutoDrawable;

public class HighlightSubtreeAffect implements AssistAffect {

	private Tree tree;
	private TreeNode selectedNode;
	
	public HighlightSubtreeAffect(Tree tree, TreeNode selectedNode) {
		this.tree = tree;
		this.selectedNode = selectedNode;
	}
	
	
//	private TreeNode getSameLabelNode(TreeNode otn) {
//		LinkedList<TreeNode> looked = new LinkedList<TreeNode>();
//		looked.addLast(tree.getRoot());
//		while(!looked.isEmpty()) {
//			TreeNode n = looked.removeFirst();
//			if (!(n.getId().equals(otn.getId()))) {
//				String[] lable1 = n.getLabel().split("\\{");
//				String[] lable2 = otn.getLabel().split("\\{");
//				if (lable1 != null && lable1[0].equals(lable2[0])) {
//					System.out.println("same label pattern: "+lable1[0]+" and "+lable2[0]);
//					return n;
//				}
//				
//			}
//			for(TreeNode tn : tree.getChildrenNodes(n)) {
//				looked.addLast(tn);
//			}
//		}
//		return null;
//	}
	
//	private void highlightSubtree(TreeNode otn) {
//		LinkedList<TreeNode> looked = new LinkedList<TreeNode>();
//		looked.addLast(otn);
//		while(!looked.isEmpty()) {
//			TreeNode n = looked.removeFirst();
//			n.setColor(TreeVisualizeListener.same_subtree_color);
//			for(TreeNode tn : tree.getChildrenNodes(n)) {
//				looked.addLast(tn);
//			}
//		}
//	}

	@Override
	public void affect(GLAutoDrawable gld) {
		LinkedList<TreeNode> looked = new LinkedList<TreeNode>();
		looked.addLast(tree.getRoot());
		while(!looked.isEmpty()) {
			TreeNode n = looked.removeFirst();
			if(!n.getPaintId().equals(selectedNode.getPaintId())) {
				n.setColor(178.0f/255, 178.0f/255, 178.0f/255);
				for (TreeNode tn : tree.getChildrenNodes(n)) {
					looked.addLast(tn);
				}
			}
		}
	}

}
