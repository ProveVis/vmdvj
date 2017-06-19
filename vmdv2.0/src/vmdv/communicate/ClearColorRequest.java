package vmdv.communicate;

import org.json.JSONObject;

public class ClearColorRequest extends RequestMsg {

	@Override
	public JSONObject to_json() {
		JSONObject json = new JSONObject();
		json.accumulate("type", "clear_color");
//		json.accumulate("node_id", nid);
		return json;
	}

}
