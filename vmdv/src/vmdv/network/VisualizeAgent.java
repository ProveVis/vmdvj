package vmdv.network;

import vmdv.paint.graph.StateEdge;
import vmdv.paint.graph.StateGraph;
import vmdv.paint.graph.StateNode;
import vmdv.paint.graph.Tree;
import vmdv.paint.listener.ProofEdgeInfo;
import vmdv.paint.listener.ProofNodeInfo;
import vmdv.paint.listener.TreeVisualOperateListener;
import vmdv.paint.stateGraphViewer.StateGraphVisualizer;
import vmdv.paint.treeViewer.ClearProofTreePopup;
import vmdv.paint.treeViewer.HideAllLabelPopup;
import vmdv.paint.treeViewer.ShowAllLabelPopup;
import vmdv.paint.treeViewer.TreeVisualizer;

public class VisualizeAgent implements TreeVisualOperateListener {
	private Tree t;
	private StateGraph sg;
	public NetAgent na;
	private TreeVisualizer tv;
	private StateGraphVisualizer sv;
	
	public VisualizeAgent(TreeVisualizer tv, StateGraphVisualizer sv) {
		this.tv = tv;
		this.sv = sv;
		na = new NetAgent(this);
	}
	
	public void addTreeNode(String pid, String id, String lable) {
		if(id.equals("0")) {
			tv.addNode(new ProofNodeInfo(true, pid, id, lable));
		} else {
			tv.addNode(new ProofNodeInfo(false, pid, id, lable));
		}
	}
	
	public void addTreeEdge(String pid, String cid) {
		tv.addEdge(new ProofEdgeInfo(pid, cid));
	}
	
	
	public void addState(String id, String state, boolean start) {
		if(sv.sg.nodeExists(id)) {
			System.out.println("state already in");
			return;
		}
		StateNode sn = new StateNode(id, state);
		sv.sg.addNode(sn);
		if(start) {
			sv.sg.setStart(sn);
		}
	}

	
	public void removeState(String id) {
		sv.sg.deleteNodeById(id);
	}

	
	public void addEdge(String from, String to) {
		if(sv.sg.edgeExists(from, to)) {
			return;
		}
		StateNode snFrom = sv.sg.getNodeById(from);
		StateNode snTo = sv.sg.getNodeById(to);
		if(snFrom != null && snTo != null) {
			sv.sg.addEdge(new StateEdge(snFrom, snTo));
		}
	}

	
	public void removeEdge(String from, String to) {
		sv.sg.deleteEdgeByIds(from, to);
	}

	

	public static void main(String[] args) {
		TreeVisualizer tv = new TreeVisualizer("Proof Tree");
		StateGraphVisualizer sv = new StateGraphVisualizer("State Graph");
		VisualizeAgent m = new VisualizeAgent(tv, null);
		
	    tv.setOperateListener(m);
	    tv.addBackPopup(new ShowAllLabelPopup("Show All Labels"));
		tv.addBackPopup(new HideAllLabelPopup("Hide All Labels"));
		tv.addBackPopup(new ClearProofTreePopup("Clear Proof Tree"));		
		m.setStateGraphVisualizer(sv);
	    m.setProofVisualizer(tv);
	    tv.listener.na = m.na;
	    sv.listener.na = m.na;
		m.showProofTree();
		m.showStateGraph();
	}

	public void showProofTree() {
		tv.updateLayout();
		tv.show();
	}
	
	public void showStateGraph() {
		sv.updateLayout();
		sv.show();
	}

	private void setProofVisualizer(TreeVisualizer tv2) {
		this.tv = tv2;
	}


	private void setStateGraphVisualizer(StateGraphVisualizer sv) {
		this.sv = sv;
	}



	@Override
	public void hightLightState(String fmlId) {
		na.sendMsg("high_state:"+fmlId);
	}



	@Override
	public void unHightLightState(String fmlId) {
//		na.sendMsg("unhi_state:"+fmlId);
	}



	@Override
	public void oneStepForward(String paintId, String fmlId) {
		// TODO Auto-generated method stub
		
	}



	@Override
	public void clearProofTree() {
		// TODO Auto-generated method stub
		
	}

	public void endAddings() {
//		this.showProofTree();
//		this.showStateGraph();
		tv.updateLayout();
//		sv.updateLayout();
	}

}
