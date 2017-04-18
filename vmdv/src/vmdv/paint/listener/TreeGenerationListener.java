package vmdv.paint.listener;

public interface TreeGenerationListener {
	public void addNode(ProofNodeInfo pni);
	public void removeNode(ProofNodeInfo pni);
	public void addEdge(ProofEdgeInfo pei);
	public void removeEdge(ProofEdgeInfo pei);
	public void updateLayout();
}
