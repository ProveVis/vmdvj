package vmdv.test;
import org.json.*;

public class TestJson {

	
	public static void main(String[] args) {
		String str = "{\"username\": \"your name\", \"user_json\": {\"username\": \"your name\", \"nickname\": \"your nickname\"}}";
		JSONObject json = new JSONObject(str);
		json.accumulate("hello", "world");
		JSONObject json2 = new JSONObject();
		json2.accumulate("hello2", "world2");
		json.accumulate("hello_2", json2);
		System.out.println(json.get("username"));
//		json.accumulate("hello", "world");
		if (!json.isNull("hello")) {
			json.remove("hello");
			json.accumulate("hello", "world3");
		}
		System.out.println(json.toString());
		
		String str_json = "{\"type\":\"add_edge\",\"session_id\":\"River_Crossing\",\"from_id\":\"6\",\"to_id\":\"5\",\"label\":\"\"}{\"type\":\"add_edge\",\"session_id\":\"River_Crossing\",\"from_id\":\"6\",\"to_id\":\"5\",\"label\":\"\"}";
		JSONArray json_2 = new JSONArray(str_json);
		System.out.println("str of multiple json: "+json_2.toString());
	}

}
