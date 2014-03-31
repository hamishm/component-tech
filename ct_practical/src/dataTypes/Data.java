package dataTypes;

import org.json.simple.JSONObject;

import core.JsonObject;

/**
 * Simply holds a double
 */
public class Data implements JsonObject{

	String value;
	
	public Data(JSONObject obj){
		load(obj);
	}
	
	public Data(String value){
		this.value = value;
	}
	
	@Override
	public JSONObject getJsonObj() {
		JSONObject obj = new JSONObject();
		obj.put("contents", value);
		return obj;
	}

	@Override
	public void load(JSONObject obj) {
		String eValue = ((String)obj.get("contents"));
		this.value = eValue;
	}

}
