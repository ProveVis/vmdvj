package vmdv.paint.listener;

public class StateNodeInfo implements NodeInfo {
	private boolean start;
	private String id;
	private String label;
	
	public StateNodeInfo(boolean start, String id, String label) {
		this.start = start;
		this.id = id;
		this.label = label;
	}
} 
