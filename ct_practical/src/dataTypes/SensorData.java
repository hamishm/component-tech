package dataTypes;

import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import core.JsonObject;

/**
 * Main class for holding sensor data
 */
public class SensorData implements JsonObject{

	Date date;
	Location location;
	List<Double> data = new ArrayList<Double>();
	
	public SensorData(Date de, Location l, ArrayList<Double> data){
		this.date = de;
		this.location = l;
		this.data = data;
	}
	
	public void addData(Double d){
		data.add(d);
	}
	
	public SensorData(Date de, Location l){
		this.date = de;
		this.location = l;
	}
	
	public Date getDate() {
		return date;
	}

	public Location getLocation() {
		return location;
	}

	public List<Double> getData() {
		return data;
	}

	public SensorData(JSONObject obj){
		load(obj);
	}
	
	@Override
	public void load(JSONObject obj) {
		this.date = new Date(obj);
		this.location = new Location((JSONObject)obj.get("location"));
		JSONArray arr = (JSONArray)obj.get("data");
		for(Object o : arr){
			Number n = (Number)o;
			this.data.add(n.doubleValue());
		}
	}

	@Override
	public JSONObject getJsonObj() {
		JSONObject obj = new JSONObject();
		obj.put("date", date.toString());
		obj.put("location", location.getJsonObj());
		JSONArray arr = new JSONArray();
		for(Double d : data){
			arr.add(d);
		}
		obj.put("data", arr);
		return obj;
	}

}
