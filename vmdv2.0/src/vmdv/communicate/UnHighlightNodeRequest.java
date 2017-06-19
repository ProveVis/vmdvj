package vmdv.communicate;

import org.json.JSONObject;

public class UnHighlightNodeRequest extends RequestMsg {
	private String nid;
	
	public UnHighlightNodeRequest(String nid) {
		this.nid = nid;
	}

	@Override
	public JSONObject to_json() {
		JSONObject jo = new JSONObject();
		jo.accumulate("type", "unhighlight_node");
		jo.accumulate("node_id", this.nid);
		return jo;
	}

}
