package dataTypes;

import org.json.simple.JSONObject;

import core.JsonObject;

public class Error implements JsonObject{
	
	String value = null;

	public Error(JSONObject obj) {
		load(obj);
	}
	
	public String getValue(){
		return value;
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
