package vmdv.paint.treeViewer;

import java.util.LinkedList;

import vmdv.paint.graph.Tree;
import vmdv.paint.graph.TreeNode;

import com.jogamp.opengl.GLAutoDrawable;

public class SearchByTextAffect implements AssistAffect {
	private TreeVisualizer tv;
	private Tree tree;
	private String text;
	private boolean hightlightSubtree;
	
	public SearchByTextAffect(TreeVisualizer tv, Tree tree, String text, boolean hightlightSubtree) {
		this.tv = tv;
		this.tree = tree;
		this.text = text;
		this.hightlightSubtree = hightlightSubtree;
	}

	@Override
	public void affect(GLAutoDrawable gld) {
		LinkedList<TreeNode> looked = new LinkedList<TreeNode>();
		looked.addLast(tree.getRoot());
		while(!looked.isEmpty()) {
			TreeNode n = looked.removeFirst();
			if(n.getLabel().startsWith(text) && n.isVisible()) {
				if(this.hightlightSubtree) {
					hightlightSubtree(n);
				} else {
					n.setColor(TreeVisualizeListener.highlight);
					tv.operateListener.hightLightState(n.getId());
//					tv.operateListener.hightLightState(n.getId());
					for(TreeNode tn : tree.getChildrenNodes(n)) {
						looked.addLast(tn);
					}
				}
			} else {
				for(TreeNode tn : tree.getChildrenNodes(n)) {
					looked.addLast(tn);
				}
			}
		}
	}
	
	private void hightlightSubtree(TreeNode tn) {
		LinkedList<TreeNode> looked = new LinkedList<TreeNode>();
		looked.addLast(tn);
		while(!looked.isEmpty()) {
			TreeNode n = looked.removeFirst();
			n.setColor(TreeVisualizeListener.highlight);
			tv.operateListener.hightLightState(n.getId());
			for(TreeNode tnc : tree.getChildrenNodes(n)) {
				looked.addLast(tnc);
			}
		}
	}

}
