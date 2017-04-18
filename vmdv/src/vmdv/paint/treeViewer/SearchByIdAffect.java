package vmdv.paint.treeViewer;

import java.util.LinkedList;

import vmdv.paint.graph.Tree;
import vmdv.paint.graph.TreeNode;

import com.jogamp.opengl.GLAutoDrawable;

public class SearchByIdAffect implements AssistAffect {
	private TreeVisualizer tv;
	private Tree tree;
	private String id;
	private boolean hightlightSubtree;
	
	public SearchByIdAffect(TreeVisualizer tv, Tree tree, String id, boolean hightlightSubtree) {
		this.tv = tv;
		this.tree = tree;
		this.id = id;
		this.hightlightSubtree = hightlightSubtree;
	}

	@Override
	public void affect(GLAutoDrawable gld) {
		LinkedList<TreeNode> looked = new LinkedList<TreeNode>();
		looked.addLast(tree.getRoot());
		while(!looked.isEmpty()) {
			TreeNode n = looked.removeFirst();
			if(n.getId().equals(id)) {
				if(this.hightlightSubtree) {
					hightlightSubtree(n);
				} else {
					n.setColor(1, 1, 0);
					tv.operateListener.hightLightState(n.getId());
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
			n.setColor(1, 1, 0);
			tv.operateListener.hightLightState(n.getId());
			for(TreeNode tnc : tree.getChildrenNodes(n)) {
				looked.addLast(tnc);
			}
		}
	}

}
