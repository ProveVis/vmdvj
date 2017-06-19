package vmdv.paint.listener;

public class ProofEdgeInfo implements EdgeInfo {
	private String from;
	private String to;
	
	public ProofEdgeInfo(String from, String to) {
		this.from = from;
		this.to = to;
	}
	
	public String getFrom() {
		return from;
	}

	public String getTo() {
		return to;
	}

}
