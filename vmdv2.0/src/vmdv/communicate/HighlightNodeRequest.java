package vmdv.communicate;

import org.json.JSONObject;

public class HighlightNodeRequest extends RequestMsg {
	private String nid;
	
	public HighlightNodeRequest(String nid) {
		this.nid = nid;
	}

	@Override
	public JSONObject to_json() {
		JSONObject json = new JSONObject();
		json.accumulate("type", "highlight_node");
		json.accumulate("node_id", nid);
		return json;
	}

}
