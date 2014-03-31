package methods;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import core.JsonObject;
import dataTypes.Location;
import dataTypes.Response;

public class GetSensorData implements JsonObject{
	private final String MethodName = "SensorData";
	private final String HttpMethod = "GET";
	private final String URI = "/sensordata";
	private final String RequiresOAuth = "Y";
	
	//params
	Location location = null;
	double radius = 0.0;
	
	Response response = null;
	
	public GetSensorData(Location location, double radius){
		this.location = location;
		this.radius = radius;
	}
	
	/**
	 * Calls this method
	 * @param url - the URL of the associated broker
	 */
	public Response call(String url){
		//http stuff here
		return response;
	}

	@Override
	public void load(JSONObject obj) {
		JSONObject r = (JSONObject)obj.get("response");
		if(r!=null){
			response = new Response(r);
		}
		JSONArray pArr = (JSONArray)obj.get("parameters");
		if(pArr != null && pArr.size() > 1){
			location = new Location(((JSONObject)pArr.get(0)));
			radius = (((Number)pArr.get(1)).doubleValue());
		}
	}

	@Override
	public JSONObject getJsonObj() {
		return null;
	}
	
	
}
