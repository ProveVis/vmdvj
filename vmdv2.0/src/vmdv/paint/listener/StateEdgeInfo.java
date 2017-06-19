package vmdv.paint.listener;

public class StateEdgeInfo implements EdgeInfo {
	private String from;
	private String to;
	
	public StateEdgeInfo(String from, String to) {
		this.from = from;
		this.to = to;
	}
}
