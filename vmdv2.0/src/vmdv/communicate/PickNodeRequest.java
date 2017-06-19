package vmdv.communicate;

import org.json.JSONObject;

public class PickNodeRequest extends RequestMsg {
	private String nid;
	
	public PickNodeRequest(String nid) {
		this.nid = nid;
	}

	@Override
	public JSONObject to_json() {
		JSONObject json = new JSONObject();
		json.accumulate("type", "pick_node");
		json.accumulate("node_id", nid);
		return json;
	}

}
