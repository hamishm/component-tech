package dataTypes;

import java.util.ArrayList;

import org.json.simple.JSONObject;

import core.JsonObject;

/**
 * Main class for holding sensor data
 */
public class SensorData implements JsonObject{

	Date date;
	Location location;
	Data data;
	
	public SensorData(Date de, Location l, Data da){
		this.date = de;
		this.location = l;
		this.data = da;
	}
	
	public Date getDate() {
		return date;
	}

	public Location getLocation() {
		return location;
	}

	public Data getData() {
		return data;
	}

	public SensorData(JSONObject obj){
		load(obj);
	}
	
	@Override
	public void load(JSONObject obj) {
		this.date = new Date(obj);
		this.location = new Location((JSONObject)obj.get("location"));
		this.data = new Data((JSONObject)obj.get("data"));
	}

	@Override
	public JSONObject getJsonObj() {
		JSONObject obj = new JSONObject();
		obj.put("date", date.toString());
		obj.put("location", location.getJsonObj());
		obj.put("data", data.getJsonObj());
		return obj;
	}

}
