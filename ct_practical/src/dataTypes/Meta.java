package dataTypes;

import org.json.simple.JSONObject;

import core.JsonObject;

public class Meta implements JsonObject{

	String value = null;
	
	public Meta(JSONObject obj){
		load(obj);
	}
	
	@Override
	public void load(JSONObject obj) {
		if(obj != null){
			value = obj.toJSONString();
		}
	}

	@Override
	public JSONObject getJsonObj() {
		JSONObject obj = new JSONObject();
		obj.put("value", value);
		return obj;
	}

}
