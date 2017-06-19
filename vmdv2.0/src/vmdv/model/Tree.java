package vmdv.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Random;
import java.util.Set;

import org.json.JSONObject;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.util.awt.TextRenderer;
import com.jogamp.opengl.util.gl2.GLUT;

import vmdv.config.ColorConfig;
import vmdv.config.GraphConfig;
import vmdv.config.GraphConfig.GraphType;
import vmdv.dev.AssistAffect;
import vmdv.dev.affects.AddEdgeAffect;
import vmdv.dev.affects.AddNodeAffect;
import vmdv.dev.affects.ClearColorAffect;
import vmdv.dev.affects.HighlightNodeAffect;
import vmdv.dev.affects.RemoveEdgeAffect;
import vmdv.dev.affects.RemoveNodeAffect;
import vmdv.dev.affects.UnHighlightNodeAffect;

public class Tree extends AbstractGraph {
	private int height = 0;
	public HashMap<String, ArrayList<TreeNode>> depthMap = new HashMap<String, ArrayList<TreeNode>>();
	private HashMap<TreeNode, TreeEdges> struct = new HashMap<TreeNode, TreeEdges>();
	private TreeNode root;
	private Random random = new Random();

	public Tree() {
		this.struct = new HashMap<TreeNode, TreeEdges>();
	}

	public TreeNode getRoot() {
		return root;
	}

	public void removeSubtree(String id) {
		TreeNode n = (TreeNode) getNode(id);
		LinkedList<TreeNode> tmp_nodes = new LinkedList<TreeNode>();
		tmp_nodes.addLast(n);
		while (!tmp_nodes.isEmpty()) {
			TreeNode tmp_n = tmp_nodes.removeFirst();
			for (TreeEdge e : struct.get(tmp_n).posts) {
				tmp_nodes.addLast(e.to);
			}
			this.removeNode(tmp_n);
		}
	}

	private void removeNode(TreeNode tn) {
		if (tn != null) {
			TreeEdges tes = struct.get(tn);
			TreeNode parent = tes.pre.from;
			struct.get(parent).posts.remove(tes.pre);
			struct.remove(tn);

			LinkedList<TreeNode> tmp_nodes = new LinkedList<TreeNode>();
			for (TreeEdge te : tes.posts) {
				tmp_nodes.addLast(te.to);
			}
			while (!tmp_nodes.isEmpty()) {
				TreeNode tmp_node = tmp_nodes.removeFirst();
				struct.remove(tmp_node);
				for (TreeEdge tmp_te : struct.get(tmp_node).posts) {
					tmp_nodes.addLast(tmp_te.to);
				}
			}
		}
	}

	public void updateHeight() {
		TreeNode r = this.root;
		if (root == null) {
			return;
		}
		r.depth = 0;
		height = 0;
		LinkedList<TreeNode> heighted = new LinkedList<TreeNode>();
		heighted.addLast(r);
		while (!heighted.isEmpty()) {
			TreeNode tn = heighted.removeFirst();
			int fromDepth = tn.depth;
			ArrayList<TreeNode> depthList = depthMap.get(String.valueOf(fromDepth));
			if (depthList == null) {
				ArrayList<TreeNode> tmp_depthList = new ArrayList<TreeNode>();
				tmp_depthList.add(tn);
				depthMap.put(String.valueOf(fromDepth), tmp_depthList);
			} else {
				depthList.add(tn);
			}

			for (TreeEdge te : struct.get(tn).posts) {

				te.to.depth = fromDepth + 1;
				if (height < fromDepth + 1) {
					height = fromDepth + 1;
				}
				heighted.addLast(te.to);
			}
		}
	}

	public void updateDepthColor() {
		RGBColor fromColor = ColorConfig.fromColor;
		RGBColor toColor = ColorConfig.toColor;

		float dr = toColor.getRed() - fromColor.getRed();
		float dg = toColor.getGreen() - fromColor.getGreen();
		float db = toColor.getBlue() - fromColor.getBlue();

		for (TreeNode tn : this.struct.keySet()) {
			tn.oriColor = (new RGBColor(fromColor.getRed() + dr * tn.depth / height,
					fromColor.getGreen() + dg * tn.depth / height, fromColor.getBlue() + db * tn.depth / height));
		}
		if (root != null) {
			root.oriColor = ColorConfig.rootColor;
		}
	}

	public Set<TreeNode> getTreeNodes() {
		return struct.keySet();
	}

	@Override
	public Set<AbstractNode> getNodes() {
		Set<AbstractNode> nodes = new HashSet<AbstractNode>();
		for (TreeNode tn : getTreeNodes()) {
			nodes.add(tn);
		}
		return nodes;
	}

	public Set<TreeNode> children(TreeNode tn) {
		Set<TreeNode> children_nodes = new HashSet<TreeNode>();
		for (TreeEdge te : struct.get(tn).posts) {
			children_nodes.add(te.to);
		}
		return children_nodes;
	}

	public Set<TreeNode> children(String id) {
		TreeNode tn = (TreeNode) getNode(id);
		assert (tn != null);
		return children(tn);
	}

	public TreeNode parent(TreeNode tn) {
		assert (tn != null);
		return struct.get(tn).pre.from;
	}

	public TreeNode parent(String id) {
		return parent((TreeNode) getNode(id));
	}

	@Override
	public void addNode(String id, String label) {
		TreeNode tmp_n = (TreeNode) getNode(id);
		if(tmp_n != null) {
			return;
		}
		TreeNode tn = new TreeNode(id, label);
		TreeEdges tes = new TreeEdges();
		if (struct.isEmpty()) {
			root = tn;
		}
		struct.put(tn, tes);
		tn.setXYZ(random.nextDouble(), random.nextDouble(), random.nextDouble());
	}

	public void addNode(String id, String label, String proofState) {
		TreeNode tmp_n = (TreeNode) getNode(id);
		if(tmp_n != null) {
			return;
		}
		TreeNode tn = new TreeNode(id, label, proofState);
		TreeEdges tes = new TreeEdges();
		if (struct.isEmpty()) {
			root = tn;
		}
		struct.put(tn, tes);
		tn.setXYZ(random.nextDouble(), random.nextDouble(), random.nextDouble());
		
	}

	@Override
	public void addEdge(String fromId, String toId) {
		// super.addEdge(fromId, toId);
		TreeNode fn = (TreeNode) getNode(fromId);
		TreeNode tn = (TreeNode) getNode(toId);
		// make sure both nodes of the added edge exist
		assert (fn != null && tn != null);
		TreeEdge te = new TreeEdge(fn, tn, null);
		struct.get(fn).posts.add(te);
		struct.get(tn).pre = te;

		int fnDepth = fn.depth;
		tn.depth = fnDepth + 1;
		if (height < fnDepth + 1) {
			height = fnDepth + 1;
		}
		updateDepthColor();
		fn.clearColor();
		tn.clearColor();
	}
	
	public void addEdge(String fromId, String toId, String label) {
		// super.addEdge(fromId, toId);
		TreeNode fn = (TreeNode) getNode(fromId);
		TreeNode tn = (TreeNode) getNode(toId);
		// make sure both nodes of the added edge exist
		assert (fn != null && tn != null);
		TreeEdge te = new TreeEdge(fn, tn, label);
		struct.get(fn).posts.add(te);
		struct.get(tn).pre = te;

		int fnDepth = fn.depth;
		tn.depth = fnDepth + 1;
		if (height < fnDepth + 1) {
			height = fnDepth + 1;
		}
		updateDepthColor();
		fn.clearColor();
		tn.clearColor();
	}

	@Override
	public void removeNode(String id) {
		TreeNode tn = (TreeNode) getNode(id);
		if (tn == null) {
			return;
		}
		removeNode(tn);
	}

	@Override
	public void removeEdge(String fromId, String toId) {
		TreeNode fn = (TreeNode) getNode(fromId);
		TreeNode tn = (TreeNode) getNode(toId);
		assert (fn != null && tn != null);
		// TreeEdge te = struct.get(tn).pre;
		removeNode(tn);
	}

	@Override
	public AbstractNode getNearestNode(double x, double y, double z) {
		double dist = 0.25;
		TreeNode rn = null;

		for (TreeNode n : struct.keySet()) {
			double tmp_dist = Math.sqrt(
					Math.pow(n.xyz.getX() - x, 2) + Math.pow(n.xyz.getY() - y, 2) + Math.pow(n.xyz.getZ() - z, 2));
			if (tmp_dist <= dist) {
				rn = n;
				break;
			}
		}

		return rn;
	}

	@Override
	public void clearColor() {
		for (TreeNode tn : struct.keySet()) {
			tn.clearColor();
		}
	}

	@Override
	public void render(GL2 gl, GLUT glut, TextRenderer tr) {
		LinkedList<TreeNode> painting = new LinkedList<TreeNode>();
		painting.addLast(root);
		int drawedNodes = 0;
		while (!painting.isEmpty()) {
			// System.out.println("painting node...");
			TreeNode tn = painting.removeFirst();
			if (tn == null) {
				return;
			}
			if (tn.visible) {
				drawedNodes ++;
				RGBColor color = tn.color;
				gl.glPushMatrix();
				gl.glColor3f(color.getRed(), color.getGreen(), color.getBlue());
				gl.glTranslated(tn.xyz.getX(), tn.xyz.getY(), tn.xyz.getZ());
				glut.glutSolidSphere(tn.size, 10, 10);
				drawedNodes++;
				gl.glColor3f(1, 1, 1);

				if (tn.showLabel) {
					tr.begin3DRendering();
					tr.draw3D(tn.label, 0, 0, 0, 0.005f);
					tr.flush();
					tr.end3DRendering();
				}
				if (tn.showChildLabel) {
					tr.begin3DRendering();
					tr.draw3D(tn.childLabel, 0, 0, 0, 0.01f);
					tr.flush();
					tr.end3DRendering();
				}
				gl.glPopMatrix();

				gl.glDisable(GL2.GL_LIGHTING);
				gl.glDisable(GL2.GL_LIGHT0);
				gl.glEnable(GL2.GL_LIGHTING);
				gl.glEnable(GL2.GL_LIGHT0);

				if (tn.showSubtree) {
					for (TreeEdge e : struct.get(tn).posts) {
						TreeNode ton = e.to;
						if (ton.visible) {
							gl.glPushMatrix();
							RGBColor edgeColor = e.color;
							gl.glColor3f(edgeColor.getRed(), edgeColor.getGreen(), edgeColor.getBlue());
							gl.glLineWidth(e.size);
							gl.glBegin(GL2.GL_LINES);
							gl.glVertex3d(tn.xyz.getX(), tn.xyz.getY(), tn.xyz.getZ());
							gl.glVertex3d(ton.xyz.getX(), ton.xyz.getY(), ton.xyz.getZ());
							gl.glEnd();
							gl.glPopMatrix();

							painting.addLast(ton);
						}
					}
				}
			}
		}
//		System.out.println("Total nodes: "+struct.keySet().size()+", Drawn nodes: "+drawedNodes);
	}

	@Override
	public AbstractNode getNode(String id) {
		for (TreeNode tn : struct.keySet()) {
			if (tn.id.equals(id)) {
				return tn;
			}
		}
		return null;
	}

	@Override
	public AbstractNode getStart() {
		return root;
	}

	@Override
	public Set<AbstractNode> getSuccessors(String id) {
		Set<AbstractNode> succs = new HashSet<AbstractNode>();
		for (TreeNode tn : children(id)) {
			succs.add(tn);
		}
		return succs;
	}

	@Override
	public Set<AbstractNode> getPredecessors(String id) {
		Set<AbstractNode> preds = new HashSet<AbstractNode>();
		preds.add(parent(id));
		return preds;
	}

	@Override
	public Set<AbstractNode> getSuccessors(AbstractNode an) {
		Set<AbstractNode> succs = new HashSet<AbstractNode>();
		for (TreeNode tn : children((TreeNode) an)) {
			succs.add(tn);
		}
		return succs;
	}

	@Override
	public Set<AbstractNode> getPredecessors(AbstractNode an) {
		Set<AbstractNode> preds = new HashSet<AbstractNode>();
		preds.add(parent((TreeNode) an));
		return preds;
	}

	@Override
	public GraphType getType() {
		return GraphConfig.GraphType.TREE;
	}

	@Override
	public AssistAffect parseJSON(JSONObject json) {
		switch (json.getString("type")) {
		case "add_node": {
			JSONObject json_node = json.getJSONObject("node");
			return new AddNodeAffect(json_node.getString("id"), json_node.getString("label"), json_node.getString("state"));
//			break;
		}
		case "remove_node": 
			return new RemoveNodeAffect(json.getString("node_id"));
		case "add_edge": {
			return new AddEdgeAffect(json.getString("from_id"), json.getString("to_id"), json.getString("label"));
		}
		case "remove_edge":
			return new RemoveEdgeAffect(json.getString("from_id"), json.getString("to_id"));
		case "highlight_node": {
			return new HighlightNodeAffect(getNode(json.getString("node_id")));
		}
		case "unhighlight_node" :
			return new UnHighlightNodeAffect(getNode(json.getString("node_id")));
		case "clear_color": 
			return new ClearColorAffect();
		default:
			System.out.println("Message type not known: "+json.getString("type"));
		}
		return null;
	}
}

class TreeEdges {
	public TreeEdge pre;
	public Set<TreeEdge> posts = new HashSet<TreeEdge>();
}
