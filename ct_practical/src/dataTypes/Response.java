package dataTypes;

import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import core.JsonObject;

public class Response implements JsonObject{

	ArrayList<SensorData> data = null;
	Error error = null;
	Meta meta = null;
	
	public Response(JSONObject obj) {
		load(obj);
	}
	
	public Response(String jsonString) {
		load((JSONObject)JSONValue.parse(jsonString));
	}

	public ArrayList<SensorData> getData() {
		return data;
	}

	public Error getError() {
		return error;
	}

	@Override
	public JSONObject getJsonObj() {
		JSONObject obj = new JSONObject();
		obj.put("error", error.getJsonObj());
		obj.put("meta", meta.getJsonObj());
		JSONArray arr = new JSONArray();
		for(SensorData d : data)
			arr.add(d.getJsonObj());
		obj.put("data", arr);
		return obj;
	}

	public Meta getMeta() {
		return meta;
	}

	@Override
	public void load(JSONObject obj) {
		JSONObject e = (JSONObject) obj.get("error");
		error = new Error(e);
		JSONObject m = (JSONObject) obj.get("meta");
		meta = new Meta(m);
		JSONArray dataArr = (JSONArray) obj.get("data");
		for(Object o : dataArr){
			JSONObject dataObj = (JSONObject)o;
			if(data == null)
				data = new ArrayList<SensorData>();
			data.add(new SensorData(dataObj));
		}
	}

}
