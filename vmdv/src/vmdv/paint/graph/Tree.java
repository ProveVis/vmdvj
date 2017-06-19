package vmdv.paint.graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Random;
import java.util.Set;

import vmdv.paint.treeViewer.TreeVisualizeListener;

public class Tree{
	private TreeNode root;
//	private HashSet<TreeNode> nodes;
//	private ArrayList<TreeEdge> edges;
	private HashMap<TreeNode, ArrayList<TreeEdge>> treeStruct;
	
	private HashMap<String, ArrayList<TreeNode>> depthMap = new HashMap<String, ArrayList<TreeNode>>();
	
	private float ka = 5f;
	private float kr = 1f;
	private float kg = 1f;
	private float ko = 1;
	
	private int height = 0;
	
	public Tree(TreeNode root) {
		this.root = root;
//		root.setColor(TreeVisualizeListener.rootColor);
//		root.setSize(0.2);
//		nodes = new HashSet<TreeNode>();
//		edges = new ArrayList<TreeEdge>();
		treeStruct = new HashMap<TreeNode, ArrayList<TreeEdge>>();
	}
	
	public Tree() {
		treeStruct = new HashMap<TreeNode, ArrayList<TreeEdge>>();
	}
	
	public TreeNode getRoot() {
		return root;
	}
	
	public void setRoot(TreeNode n) {
		RGBColor oriColorN = n.getOriColor();
		if(root == null) {
			root = n;
			return;
		}
		RGBColor oriColorRoot = root.getOriColor();
		double oriSizeN = n.getOriSize();
		double oriSizeRoot = root.getOriSize();
		
		n.setOriColor(oriColorRoot);
		n.setOriSize(oriSizeRoot);
		root.setOriColor(oriColorN);
		root.setOriSize(oriSizeN);
		
		root.resetSize();
		root.clearColor();
		n.resetSize();
		n.clearColor();
		n.setXYZ(0, 0, 0);
		root = n;
		layout();
		/*
		HashMap<TreeNode, ArrayList<TreeEdge>> tmp_treeStruct = new HashMap<TreeNode, ArrayList<TreeEdge>>();
		LinkedList<TreeNode> added = new LinkedList<TreeNode>();
		added.addFirst(n);
		while(!added.isEmpty()) {
			TreeNode tn = added.removeLast();
			ArrayList<TreeEdge> tes = treeStruct.get(tn);
			for(TreeEdge e : tes) {
				TreeNode ton = e.getTo();
				added.addLast(ton);
				addTreeEdge(e, tmp_treeStruct);
			}
		}
		this.treeStruct = tmp_treeStruct;
		*/
	}
	
	private void addTreeEdge(TreeEdge e, HashMap<TreeNode, ArrayList<TreeEdge>> treeStruct) {
		for(TreeNode n : treeStruct.keySet()) {
			if(n.equals(e.getFrom())) {
				treeStruct.get(n).add(e);
				return;
			}
		}
		ArrayList<TreeEdge> tmp_edges = new ArrayList<TreeEdge>();
		tmp_edges.add(e);
//		e.getTo().setDepth(e.getFrom().getDepth()+1);
//		int toDepth = e.getTo().getDepth();
//		if(height < toDepth) {
//			height = toDepth;
//		}
		treeStruct.put(e.getFrom(), tmp_edges);
	}
	
	public void setHeight() {
		TreeNode r = this.root;
		if(root == null) {
			return;
		}
		r.setDepth(0);
		height = 0;
		LinkedList<TreeNode> heighted = new LinkedList<TreeNode>();
		heighted.addLast(r);
		while(!heighted.isEmpty()) {
			TreeNode tn = heighted.removeFirst();
			int fromDepth = tn.getDepth();
			ArrayList<TreeNode> depthList = depthMap.get(String.valueOf(fromDepth));
			if(depthList == null) {
				ArrayList<TreeNode> tmp_depthList = new ArrayList<TreeNode>();
				tmp_depthList.add(tn);
				depthMap.put(String.valueOf(fromDepth), tmp_depthList);
			} else {
				depthList.add(tn);
			}
			
			for(TreeEdge te : treeStruct.get(tn)) {
				
				te.getTo().setDepth(fromDepth+1);
				if(height < fromDepth+1) {
					height = fromDepth+1;
				}
				heighted.addLast(te.getTo());
			}
//			nodes.remove(tn);
//			treeStruct.remove(tn);
		}
	}
	
	private void deleteTreeEdge(TreeEdge e, HashMap<TreeNode, ArrayList<TreeEdge>> treeStruct) {
		for(TreeNode n : treeStruct.keySet()) {
			if(n.equals(e.getFrom())) {
				ArrayList<TreeEdge> tmp_edges = treeStruct.get(n);
				for(TreeEdge te : tmp_edges) {
					if(te.equals(e)) {
						if(tmp_edges.size() == 1) {
							treeStruct.remove(n);
							return;
						} else {
							tmp_edges.remove(e);
							return;
						}
					}
				}
				return;
			}
		}
	}
	
	private void deleteSubTree(TreeNode r) {
		//delete all subtrees of node r
		LinkedList<TreeNode> deleted = new LinkedList<TreeNode>();
		deleted.addLast(r);
		while(!deleted.isEmpty()) {
			TreeNode tn = deleted.removeFirst();
			for(TreeEdge te : treeStruct.get(tn)) {
//				edges.remove(te);
				deleted.addLast(te.getTo());
			}
//			nodes.remove(tn);
			treeStruct.remove(tn);
		}
		//delete edge pointing to r
		for(TreeNode n : treeStruct.keySet()) {
			ArrayList<TreeEdge> tes = treeStruct.get(n);
			for(TreeEdge e : tes) {
				if(e.getTo().equals(r)) {
					deleteTreeEdge(e, treeStruct);
					return;
				}
			}
		}
	}
	
	public void closeSubTree(TreeNode r) {
		LinkedList<TreeNode> closed = new LinkedList<TreeNode>();
		for(TreeEdge e : treeStruct.get(r)) {
			closed.addLast(e.getTo());
		}
		while(!closed.isEmpty()) {
			TreeNode tn = closed.removeFirst();
			for(TreeEdge te : treeStruct.get(tn)) {
				closed.addLast(te.getTo());
			}
			tn.setVisible(false);
		}
	}
	
	public void expandSubTree(TreeNode r) {
		LinkedList<TreeNode> closed = new LinkedList<TreeNode>();
		for(TreeEdge e : treeStruct.get(r)) {
			closed.addLast(e.getTo());
		}
		while(!closed.isEmpty()) {
			TreeNode tn = closed.removeFirst();
			for(TreeEdge te : treeStruct.get(tn)) {
				closed.addLast(te.getTo());
			}
			tn.setVisible(true);
		}
	}
	
	public TreeNode getPreNode(TreeNode otn) {
		if(this.root.equals(otn)) {
			return null;
		}
		LinkedList<TreeNode> looked = new LinkedList<TreeNode>();
		looked.addLast(root);
		while(!looked.isEmpty()) {
			TreeNode tn = looked.removeFirst();
			
			for(TreeEdge te : treeStruct.get(tn)) {
				if(te.getTo().getPaintId().equals(otn.getPaintId())) {
					return te.getFrom();
				} else {
					looked.addLast(te.getTo());
				}
			}
			
//			for(TreeEdge e : treeStruct.get(tn)) {
//				looked.addLast(e.getTo());
//			}
		}
		return null;
	}
	
	public ArrayList<TreeNode> getChildrenNodes(TreeNode n) {
		ArrayList<TreeNode> childrenNodes = new ArrayList<TreeNode>();
		for(TreeEdge e : treeStruct.get(n)) {
			childrenNodes.add(e.getTo());
		}
		return childrenNodes;
	}
	
	public ArrayList<TreeEdge> getEdges(TreeNode n) {
		return treeStruct.get(n);
	}

	public void addNode(TreeNode n) {
		//set the first node added to be root
	//	if(root == null) {
	//		root = n;
	//	}
		synchronized(treeStruct.keySet()) {
		ArrayList<TreeEdge> treeEdges = treeStruct.get(n);
		if(treeEdges == null) {
			treeStruct.put(n, new ArrayList<TreeEdge>());
		} else {
			System.out.println("already exists node "+n.getPaintId()+","+n.getId()+", size "+treeEdges.size());
		}
		}
		
	}
	
	public TreeNode getNodeById(String id) {
		for(TreeNode tn : treeStruct.keySet()) {
			if(tn.getPaintId().equals(id)) {
				return tn;
			}
		}
		return null;
	}

	public void addEdge(TreeEdge e) {
		addTreeEdge(e, treeStruct);
	}
	
	public void addEdge(String fromId, String toId) {
		TreeNode fn = null;
		TreeNode tn = null;
//		LinkedList<TreeNode> looked = new LinkedList<TreeNode>();
//		looked.addLast(root);
//		while(!looked.isEmpty()) {
//			TreeNode n = looked.removeFirst();
//			if(n == null) {
//				System.out.println("n is null");
//			}
//			if(n.getId().equals(fromId)) {
//				fn = n;
//			} else if(n.getId().equals(toId)) {
//				tn = n;
//			}
//			if(fn == null || tn == null) {
//				ArrayList<TreeEdge> edges = treeStruct.get(n);
//				if(edges == null) {
//					treeStruct.remove(n);
//					treeStruct.put(n, new ArrayList<TreeEdge>());
//					edges = treeStruct.get(n);
//				} else {
//					for(TreeEdge e : edges) {
//						looked.addLast(e.getTo());
//					}
//				}
//				System.out.println(treeStruct.size()+","+edges);
//			} else {
//				break;
//			}
//		}
		fn = getNodeById(fromId);
		tn = getNodeById(toId);
//		addEdge(new TreeEdge(fn, tn));
//		ArrayList<TreeEdge> edges = treeStruct.get(fn);
		if(fn == null) {
			System.out.println("node paintId "+fromId+" not found");
		}
//		tn.setDepth(fn.getDepth()+1);
		treeStruct.get(fn).add(new TreeEdge(fn, tn));
		this.setHeight();
		this.setDepthColor();
//		System.out.println("TreeEdge added: "+fn.getId()+"--"+tn.getId());
	}

	public void deleteNode(TreeNode n) {
		deleteSubTree(n);
	}
	
	public void deleteNode(String id) {
		LinkedList<TreeNode> looked = new LinkedList<TreeNode>();
		looked.addLast(root);
		while(!looked.isEmpty()) {
			TreeNode tn = looked.removeFirst();
			if(tn.getPaintId().equals(id)) {
				deleteNode(tn);
				return;
			} else {
				for(TreeEdge e : treeStruct.get(tn)) {
					looked.addLast(e.getTo());
				}
			}
		}
	}

	public void deleteEdge(TreeEdge e) {
//		edges.remove(e);
		deleteTreeEdge(e, treeStruct);
	}
	
	public void deleteEdge(String fromId, String toId) {
		LinkedList<TreeNode> looked = new LinkedList<TreeNode>();
		looked.addLast(root);
		while(!looked.isEmpty()) {
			TreeNode n = looked.removeFirst();
			for(TreeEdge e : treeStruct.get(n)) {
				if(e.getFrom().getPaintId().equals(fromId) && e.getTo().getPaintId().equals(toId)) {
					deleteTreeEdge(e, treeStruct);
					return;
				} else {
					looked.addLast(e.getTo());
				}
			}
		}
	}
	
	public TreeNode getNearestNode(double x, double y, double z) {
		double dist = 0.25;
		TreeNode rn = null;
		LinkedList<TreeNode> looked = new LinkedList<TreeNode>();
		looked.add(root);
		while(!looked.isEmpty()) {
			TreeNode n = looked.removeFirst();
			double tmpDist = Math.sqrt(Math.pow(n.getX()-x, 2)+Math.pow(n.getY()-y, 2)+Math.pow(n.getZ()-z, 2));
			if(tmpDist<dist) {
				rn = n;
				dist = tmpDist;
			}
			for(TreeEdge e : treeStruct.get(n)) {
				looked.addLast(e.getTo());
			}
		}
		
		return rn;
	}

	public void layout() {
//		for(TreeNode tn : treeStruct.keySet()) {
//			System.out.println(tn.id+": "+treeStruct.get(tn).size());
//		}
		
		
//		LinkedList<TreeNode> layouted = new LinkedList<TreeNode>();
//		layouted.add(root);
//		while(!layouted.isEmpty()) {
//			TreeNode pn = layouted.removeFirst();
////			System.out.println(pn.getId()+": "+pn.getX()+","+pn.getY()+","+pn.getZ());
//			ArrayList<TreeEdge> tmp_edges = treeStruct.get(pn);
//			int childrenCount = tmp_edges.size();
//			int i = 0;
//			for(TreeEdge e : tmp_edges) {
//				i++;
//				TreeNode n = e.getTo();
//				n.setXYZ(pn.getX()+Math.sin(2*i*Math.PI/childrenCount), pn.getY()+1, pn.getZ()+Math.cos(2*i*Math.PI/childrenCount));
//				layouted.addLast(n);
//			}
//		}
		Random r = new Random();
//		double x = r.nextDouble();
//		double y = r.nextDouble();
//		double z = r.nextDouble();
//		
//		double d = Math.sqrt(Math.pow(x, 2)+ Math.pow(y,2)+ Math.pow(z, 2));
//		for(TreeNode tn : treeStruct.keySet()) {
//			tn.setXYZ(r.nextDouble(), r.nextDouble(), r.nextDouble());
//		}
		
		for(TreeNode tn : treeStruct.keySet()) {
			double x = r.nextDouble()-0.5;
			double y = r.nextDouble()-0.5;
			double z = r.nextDouble()-0.5;
			double depth = tn.getDepth()*0.5;
			double d = Math.sqrt(Math.pow(x, 2)+ Math.pow(y,2)+ Math.pow(z, 2));
			
			tn.setXYZ(x*depth/d, y*depth/d, z*depth/d);
		}
		
	}
	
	public void updateLayout2(int times) {
		if (root == null) {
			return;
		}
		
		//resistance from the same depth
		for(String d : depthMap.keySet()) {
			for(TreeNode tn : depthMap.get(d)) {
				for(TreeNode tn2 : depthMap.get(d)) {
					if(!tn.getPaintId().equals(tn2.getPaintId())) {
						XYZ snp = tn.getXYZ();
						XYZ np = tn2.getXYZ();
						double d2 = Math.pow(snp.getX()-np.getX(), 2)+Math.pow(snp.getY()-np.getY(), 2)+Math.pow(snp.getZ()-np.getZ(), 2);
//						System.out.println("distance: "+d2);
						if(d2 == 0) {
							d2 = 1;
						}
//						XYZ preForce = sn.getForce();
//						sn.setForce(new XYZ(preForce.getX()+(kr*(snp.getX()-np.getX())*Math.pow(d2, -1.5)), preForce.getY()+(kr*(snp.getY()-np.getY())*Math.pow(d2, -1.5)),preForce.getZ()+(kr*(snp.getZ()-np.getZ())*Math.pow(d2, -1.5))));
						double dx = snp.getX()-np.getX();
						double dy = snp.getY() - np.getY();
						double dz = snp.getZ() - np.getZ();
						dx = dx == 0? 1 : dx;
						dy = dy == 0? 1 : dy;
						dz = dz == 0? 1 : dz;
						tn.addForce(kr*(dx)*Math.pow(d2, -1.5), kr*(dy)*Math.pow(d2, -1.5), kr*(dz)*Math.pow(d2, -1.5));
//						System.out.println("setting resistance");
					}
				}
			}
		}
		
		//attraction
		for(String d : depthMap.keySet()) {
			for(TreeNode tn : depthMap.get(d)) {
				for(TreeEdge te : treeStruct.get(tn)) {
					TreeNode dn = te.getTo();
					XYZ snp = tn.getXYZ();
					XYZ dnp = dn.getXYZ();
//					double d2 = Math.pow(snp.getX()-np.getX(), 2)+Math.pow(snp.getY()-np.getY(), 2)+Math.pow(snp.getZ()-np.getZ(), 2);
//					if(d2 == 0) {
//						d2 = 1;
//					}
					tn.addForce(ka*(dnp.getX() - snp.getX()), ka*(dnp.getY() - snp.getY()), ka*(dnp.getZ() - snp.getZ()));
					dn.addForce(ka*(snp.getX() - dnp.getX()), ka*(snp.getY() - dnp.getY()), ka*(snp.getZ() - dnp.getZ()));
				}
			}
		}
		
		//set move
		for(TreeNode sn : treeStruct.keySet()) {
			XYZ force = sn.getForce();
			XYZ p = sn.getXYZ();
			sn.setXYZ(p.getX()+force.getX()*0.01, p.getY()+force.getY()*0.01, p.getZ()+force.getZ()*0.01);
			double xyz = Math.sqrt(Math.pow(sn.getX(), 2)+Math.pow(sn.getY(), 2)+Math.pow(sn.getY(), 2));
			double d = sn.getDepth();
			sn.setXYZ(sn.getX()*d/xyz, sn.getY()*d/xyz, sn.getZ()*d/xyz);
			sn.setLabel("     "+d);
			sn.setForce(new XYZ(0,0,0));
//			force = sn.getForce();
//			System.out.println("Node force: "+force.getX()+", "+force.getY()+","+force.getZ());
//			System.out.println("Node Position: "+(p.getX()+force.getX())+", "+(p.getY()+force.getY())+","+(p.getZ()+force.getZ()));
		}
		root.setXYZ(0, 0, 0);
		
		
	}
	
	public void updateLayout(int times) {
		if(root == null) {
			return;
		}
//		Set<TreeNode> tns = treeStruct.keySet();
//		LinkedList<TreeNode> tmp_tns = new LinkedList<TreeNode>();
		
		
		while(times > 0) {
			times --;
			//resistance force
			Set<TreeNode> tns = treeStruct.keySet();
			LinkedList<TreeNode> tmp_tns = new LinkedList<TreeNode>();
			synchronized(tns) {
				for(TreeNode tn : tns) {
					tmp_tns.addFirst(tn);
				}
			}
			for(TreeNode sn : tns) {
				for(TreeNode n : tns) {
					if(!sn.getPaintId().equals(n.getPaintId())) {
						XYZ snp = sn.getXYZ();
						XYZ np = n.getXYZ();
						double d2 = Math.pow(snp.getX()-np.getX(), 2)+Math.pow(snp.getY()-np.getY(), 2)+Math.pow(snp.getZ()-np.getZ(), 2);
//						System.out.println("distance: "+d2);
						if(d2 == 0) {
							d2 = 1;
						}
//						XYZ preForce = sn.getForce();
//						sn.setForce(new XYZ(preForce.getX()+(kr*(snp.getX()-np.getX())*Math.pow(d2, -1.5)), preForce.getY()+(kr*(snp.getY()-np.getY())*Math.pow(d2, -1.5)),preForce.getZ()+(kr*(snp.getZ()-np.getZ())*Math.pow(d2, -1.5))));
						double dx = snp.getX()-np.getX();
						double dy = snp.getY() - np.getY();
						double dz = snp.getZ() - np.getZ();
						dx = dx == 0? 1 : dx;
						dy = dy == 0? 1 : dy;
						dz = dz == 0? 1 : dz;
						sn.addForce(kr*(dx)*Math.pow(d2, -1.5), kr*(dy)*Math.pow(d2, -1.5), kr*(dz)*Math.pow(d2, -1.5));
					}
				}
			}
			//attraction force
			for(TreeNode sn : tmp_tns) {
				for(TreeEdge te : treeStruct.get(sn)) {
					TreeNode dn = te.getTo();
					XYZ snp = sn.getXYZ();
					XYZ dnp = dn.getXYZ();
//					double d2 = Math.pow(snp.getX()-np.getX(), 2)+Math.pow(snp.getY()-np.getY(), 2)+Math.pow(snp.getZ()-np.getZ(), 2);
//					if(d2 == 0) {
//						d2 = 1;
//					}
					sn.addForce(ka*(dnp.getX() - snp.getX()), ka*(dnp.getY() - snp.getY()), ka*(dnp.getZ() - snp.getZ()));
					dn.addForce(ka*(snp.getX() - dnp.getX()), ka*(snp.getY() - dnp.getY()), ka*(snp.getZ() - dnp.getZ()));
				}
			}
						
			//set move
			for(TreeNode sn : tmp_tns) {
				XYZ force = sn.getForce();
				XYZ p = sn.getXYZ();
				sn.setXYZ(p.getX()+force.getX()*0.02, p.getY()+force.getY()*0.02, p.getZ()+force.getZ()*0.02);
//				double xyz = Math.sqrt(Math.pow(sn.getX(), 2)+Math.pow(sn.getY(), 2)+Math.pow(sn.getY(), 2));
//				double d = sn.getDepth();
//				sn.setXYZ(sn.getX()*d/xyz, sn.getY()*d/xyz, sn.getZ()*d/xyz);
//				sn.setLabel("     "+d);
				sn.setForce(new XYZ(0,0,0));
//				force = sn.getForce();
//				System.out.println("Node force: "+force.getX()+", "+force.getY()+","+force.getZ());
//				System.out.println("Node Position: "+(p.getX()+force.getX())+", "+(p.getY()+force.getY())+","+(p.getZ()+force.getZ()));
			}
			root.setXYZ(0, 0, 0);
		}
	}

	public void expandOneStep(String id) {
		for(TreeNode tn : this.treeStruct.keySet()) {
			if(tn.getPaintId().equals(id)) {
				for(TreeEdge te : this.treeStruct.get(tn)) {
					te.getTo().setVisible(true);
					te.getTo().setColor(TreeVisualizeListener.stepColor);
				}
				return;
			}
		}
	}

	public void clearColor() {
		for(TreeNode tn : this.treeStruct.keySet()) {
			tn.clearColor();
		}
	}
	
	public void setDepthColor() {
		RGBColor fromColor = TreeVisualizeListener.fromColor;
		RGBColor toColor = TreeVisualizeListener.toColor;
		
		float dr = toColor.getRed() - fromColor.getRed();
		float dg = toColor.getGreen() - fromColor.getGreen();
		float db = toColor.getBlue() - fromColor.getBlue();
		
		for(TreeNode tn : this.treeStruct.keySet()) {
			tn.setOriColor(new RGBColor(fromColor.getRed()+dr*tn.getDepth()/height, fromColor.getGreen()+dg*tn.getDepth()/height, fromColor.getBlue()+db*tn.getDepth()/height));
		}
		if(root != null) {
			root.setOriColor(TreeVisualizeListener.rootColor);
		}
	}
}

