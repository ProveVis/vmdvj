package vmdv.paint.graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Random;
import java.util.Set;

import vmdv.paint.stateGraphViewer.StateGraphVisualizeListener;

public class StateGraph {
	private StateNode startState;
	private HashMap<StateNode, DagStruct> stateGraphStruct;
	private float ka = 1f;
	private float kr = 1f;
	private float kg = 1f;
	private float ko = 1;
	
	public StateGraph(StateNode startNode) {
		this.startState = startNode;
		stateGraphStruct = new HashMap<StateNode, DagStruct>();
		if(startState != null) {
			stateGraphStruct.put(startState, new DagStruct());
		}
	}
	
	public void setStart(StateNode sn) {
		startState = sn;
		startState.setColor(new RGBColor(0,0,1));
	}
	
	public StateNode getStart() {
		return this.startState;
	}
	
	public void clearGraph() {
		this.startState = null;
		this.stateGraphStruct.clear();
	}
	
	public boolean nodeExists(String id) {
//		System.out.println("test node exists "+id);
		if(stateGraphStruct.isEmpty()) {
			return false;
		}
		for(StateNode sn : stateGraphStruct.keySet()) {
//			System.out.println("stategraphstruct size "+stateGraphStruct.size());
			if(sn.getId().equals(id)) {
				return true;
			}
		}
		return false;
	}
	
	public boolean edgeExists(String from, String to) {
		for(StateNode sn : stateGraphStruct.keySet()) {
			if(sn.getId().equals(from)) {
				ArrayList<StateEdge> posts = stateGraphStruct.get(sn).getPostEdges();
				for(StateEdge se : posts) {
					if(se.getTo().getId().equals(to)) {
						return true;
					}
				}
			} else if(sn.getId().equals(to)) {
				ArrayList<StateEdge> pres = stateGraphStruct.get(sn).getPreEdges();
				for(StateEdge se : pres) {
					if(se.getFrom().getId().equals(from)) {
						return true;
					}
				}
			}
		}
		return false;
	}
	
	public Set<StateNode> getNodes() {
		return stateGraphStruct.keySet();
	}
	
	public void addNode(StateNode n) {
		if (this.startState == null) {
			this.setStart(n);
			this.startState.setColor(0, 0, 0);
		}
		synchronized(stateGraphStruct.keySet()) {
		stateGraphStruct.put(n, new DagStruct());
		}
	}

	public void addEdge(StateEdge e) {
		StateNode fn = e.getFrom();
		StateNode tn = e.getTo();
		DagStruct dsf = stateGraphStruct.get(fn);
		if(dsf != null) {
			dsf.addPostEdge(e);
		}
		DagStruct dst = stateGraphStruct.get(tn);
		if(dst != null) {
			dst.addPreEdge(e);
		}
	}
	
	public StateNode getNodeById(String id) {
		StateNode sn = null;
		for(StateNode n : stateGraphStruct.keySet()) {
			if(n.getId().equals(id)) {
				sn = n;
				break;
			}
		}
		return sn;
	}
	public StateNode getNodeByLabel(String state) {
		for(StateNode sn : stateGraphStruct.keySet()) {
			if(sn.getLabel().equals(state)) {
				return sn;
			}
		}
		return null;
	}
	public void deleteNode(StateNode n) {
		stateGraphStruct.remove(n);
	}
	public void deleteNodeById(String id) {
		for(StateNode sn : stateGraphStruct.keySet()) {
			if(sn.getId().equals(id)) {
				stateGraphStruct.remove(sn);
			}
		}
	}
	
	

	public void deleteEdge(StateEdge e) {
		for(StateNode n : stateGraphStruct.keySet()) {
			DagStruct ds = stateGraphStruct.get(n);
			if(ds.isPostEdge(e) || ds.isPreEdge(e)) {
				ds.removeEdge(e);
			}
		}
	}
	
	public void deleteEdgeByIds(String from, String to) {
		for(StateNode sn : stateGraphStruct.keySet()) {
			DagStruct ds = stateGraphStruct.get(sn);
			if(sn.id.equals(from)) {
				ds.removePostEdgeById(from);
			} else if(sn.id.equals(to)) {
				ds.removePreEdgeById(to);
			}
		}
	}
	
	public ArrayList<StateEdge> getPostEdges(StateNode sn) {
		return stateGraphStruct.get(sn).getPostEdges();
	}
	
	public ArrayList<StateEdge> getPreEdges(StateNode sn) {
		return stateGraphStruct.get(sn).getPreEdges();
	}
	
	public StateNode getNearestNode(double x, double y, double z) {
		double dist = 0.1;
		StateNode rn = null;
		for(StateNode n : stateGraphStruct.keySet()) {
			double tmpDist = Math.sqrt(Math.pow(n.getX()-x, 2)+Math.pow(n.getY()-y, 2)+Math.pow(n.getZ()-z, 2));
			if(tmpDist<dist) {
				rn = n;
				dist = tmpDist;
			}
		}
		
		return rn;
	}
	
	public void layout() {
		Random r = new Random();
		for(StateNode sn : stateGraphStruct.keySet()) {
			sn.setXYZ(r.nextDouble(), r.nextDouble(), r.nextDouble());
//			System.out.println("Random node position: "+sn.getX()+","+sn.getY()+","+sn.getZ());
		}
	}
	
	public void updateLayout(int times) {
		while(times > 0) {
			times --;
			//resistance force
			LinkedList<StateNode> tmp_sns = new LinkedList<StateNode>();
			Set<StateNode> sns = this.getNodes();
			synchronized(sns) {
				for(StateNode sn : sns) {
					tmp_sns.addFirst(sn);
				}
			}
			for(StateNode sn : tmp_sns) {
				for(StateNode n : tmp_sns) {
					if(!sn.getId().equals(n.getId())) {
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
			for(StateNode sn : tmp_sns) {
				//pre nodes
				DagStruct ds = stateGraphStruct.get(sn);
				for(StateEdge se : ds.getPreEdges()) {
					StateNode n = se.getFrom();
					XYZ snp = sn.getXYZ();
					XYZ np = n.getXYZ();
					double d2 = Math.pow(snp.getX()-np.getX(), 2)+Math.pow(snp.getY()-np.getY(), 2)+Math.pow(snp.getZ()-np.getZ(), 2);
					if(d2 == 0) {
						d2 = 1;
					}
//					XYZ preForce = sn.getForce();
////					System.out.println("snp: "+snp.getX()+","+snp.getY()+","+snp.getZ()+"\r\nnp: "+np.getX()+","+np.getY()+","+np.getZ());
////					System.out.println("Pre attraction: "+(ka*(np.getX() - snp.getX()))+","+(ka*(np.getY() - snp.getY()))+","+(ka*(np.getZ() - snp.getZ())));
//					sn.setForce(new XYZ(preForce.getX()+(ka*(np.getX() - snp.getX())), preForce.getY()+(ka*(np.getY() - snp.getY())), preForce.getZ()+(ka*(np.getZ() - snp.getZ()))));
					sn.addForce(ka*(np.getX() - snp.getX()), ka*(np.getY() - snp.getY()), ka*(np.getZ() - snp.getZ()));
//					sn.addForce(ka*(np.getX() - snp.getX())*Math.pow(d2, -0.5), ka*(np.getY() - snp.getY())*Math.pow(d2, -0.5), ka*(np.getZ() - snp.getZ())*Math.pow(d2, -0.5));
				}
				for(StateEdge se : ds.getPostEdges()) {
					StateNode n = se.getTo();
					XYZ snp = sn.getXYZ();
					XYZ np = n.getXYZ();
					double d2 = Math.pow(snp.getX()-np.getX(), 2)+Math.pow(snp.getY()-np.getY(), 2)+Math.pow(snp.getZ()-np.getZ(), 2);
					if(d2 == 0) {
						d2 = 1;
					}
//					XYZ preForce = sn.getForce();
//					sn.setForce(new XYZ(preForce.getX()+(ka*(np.getX() - snp.getX())), preForce.getY()+(ka*(np.getY() - snp.getY())), preForce.getZ()+(ka*(np.getZ() - snp.getZ()))));
					sn.addForce(ka*(np.getX() - snp.getX()), ka*(np.getY() - snp.getY()), ka*(np.getZ() - snp.getZ()));
//					sn.addForce(ka*(np.getX() - snp.getX())*Math.pow(d2, -0.5), ka*(np.getY() - snp.getY())*Math.pow(d2, -0.5), ka*(np.getZ() - snp.getZ())*Math.pow(d2, -0.5));
				}
			}
			
//			//gravity
//			for(StateNode sn : getNodes()) {
//				XYZ preForce = sn.getForce();
//				double d2 = Math.pow(sn.getX(), 2)+Math.pow(sn.getY(), 2)+Math.pow(sn.getZ(), 2);
//				sn.setForce(new XYZ(preForce.getX()-(sn.getX()/d2)*kg, preForce.getY()-(sn.getY()/d2)*kg, preForce.getZ()-(sn.getZ()/d2)*kg));
//			}
			
			//set move
			for(StateNode sn : tmp_sns) {
				XYZ force = sn.getForce();
				XYZ p = sn.getXYZ();
				sn.setXYZ(p.getX()+force.getX()*0.002, p.getY()+force.getY()*0.002, p.getZ()+force.getZ()*0.002);
				sn.setForce(new XYZ(0,0,0));
//				force = sn.getForce();
//				System.out.println("Node force: "+force.getX()+", "+force.getY()+","+force.getZ());
//				System.out.println("Node Position: "+(p.getX()+force.getX())+", "+(p.getY()+force.getY())+","+(p.getZ()+force.getZ()));
			}
			
//			System.out.println("update once complete");
		}
	}

	public void expandOneStep(String stateId) {
		// TODO Auto-generated method stub
		for(StateNode sn : this.stateGraphStruct.keySet()) {
			if(sn.getId().equals(stateId)) {
				DagStruct ds = this.stateGraphStruct.get(sn);
				for(StateEdge se : ds.getPostEdges()) {
					se.getTo().setVisible(true);
					RGBColor sc = StateGraphVisualizeListener.stepColor;
					se.getTo().setColor(sc.getRed(), sc.getGreen(), sc.getBlue());
				}
				return;
			}
		}
	}

	public void clearColor() {
		for(StateNode sn : this.stateGraphStruct.keySet()) {
			sn.clearColor();
		}
	}

}

class DagStruct {
	private ArrayList<StateEdge> preEdges;
	private ArrayList<StateEdge> postEdges;
	
	public DagStruct() {
		this.preEdges = new ArrayList<StateEdge>();
		this.postEdges = new ArrayList<StateEdge>();
	}
	
	public DagStruct(ArrayList<StateEdge> preEdges, ArrayList<StateEdge> postEdges) {
		this.preEdges = preEdges;
		this.postEdges = postEdges;
	}
	
	public ArrayList<StateEdge> getPreEdges() {
		return this.preEdges;
	}
	
	public ArrayList<StateEdge> getPostEdges() {
		return this.postEdges;
	}
	
	public void addPreEdge(StateEdge e) {
		preEdges.add(e);
	}
	
	public void addPostEdge(StateEdge e) {
		postEdges.add(e);
	}
	
	public void removePreEdgeById(String to) {
		for(StateEdge se : preEdges) {
			if(se.getTo().equals(to)) {
				preEdges.remove(se);
				break;
			}
		}
	}
	
	public void removePostEdgeById(String from) {
		for(StateEdge se : postEdges) {
			if(se.getFrom().equals(from)) {
				postEdges.remove(se);
				break;
			}
		}
	}
	
	public boolean isPreEdge(StateEdge e) {
		if(preEdges.contains(e)) {
			return true;
		} else {
			return false;
		}
	}
	
	public boolean isPostEdge(StateEdge e) {
		if(postEdges.contains(e)) {
			return true;
		} else {
			return false;
		}
	}
	
	public void removeEdge(StateEdge e) {
		if(isPreEdge(e)) {
			preEdges.remove(e);
		}
		if(isPostEdge(e)) {
			postEdges.remove(e);
		}
	}
}
