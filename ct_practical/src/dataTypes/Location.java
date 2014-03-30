package dataTypes;

import org.json.simple.JSONObject;

import core.JsonObject;

/**
 * Represents a coordinate
 */
public class Location implements JsonObject{

	double latitude;
	double longitude;
	
	public Location(double latitude, double longitude){
		this.latitude = latitude;
		this.longitude = longitude;
	}
	
	public Location(JSONObject obj){
		load(obj);
	}
	
	@Override
	public JSONObject getJsonObj() {
		JSONObject obj = new JSONObject();
		obj.put("latitude", latitude);
		obj.put("longitude", longitude);
		return obj;
	}

	public double getLatitude() {
		return this.latitude;
	}
	
	public double getLongitude() {
		return this.longitude;
	}

	@Override
	public void load(JSONObject obj) {
		double olatitude = ((Number)obj.get("latitude")).doubleValue();
		double olongitude = ((Number)obj.get("longitude")).doubleValue();
		this.latitude = olatitude;
		this.longitude = olongitude;
	}

	public String toString(){
		return "["+latitude+", "+longitude+"]";
	}
}
