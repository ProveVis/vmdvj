package vmdv.communicate;

import org.json.JSONObject;

public class UnPickNodeRequest extends RequestMsg {
	private String nid;
	
	public UnPickNodeRequest(String nid) {
		this.nid = nid;
	}

	@Override
	public JSONObject to_json() {
		JSONObject jo = new JSONObject();
		jo.accumulate("type", "unpick_node");
		jo.accumulate("node_id", this.nid);
		return jo;
	}

}
