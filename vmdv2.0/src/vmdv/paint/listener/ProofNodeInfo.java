package vmdv.paint.listener;

//import sctl.formulas.Formula;

public class ProofNodeInfo implements NodeInfo {
	private boolean root;
	private String paintId;
	private String id;
	private String lable;
	
	public ProofNodeInfo(boolean root, String paintId, String id, String lable) {
		this.root = root;
		this.paintId = paintId;
		this.id = id;
		this.lable = lable;
	}
	
	public boolean isRoot() {
		return root;
	}

	public String getId() {
		return id;
	}

	public String getPaintId() {
		return paintId;
	}

	public void setPaintId(String paintId) {
		this.paintId = paintId;
	}

	public String getLable() {
		return lable;
	}

}
