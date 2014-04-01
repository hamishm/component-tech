package methods;

import org.json.simple.JSONObject;

import core.Network;
import dataTypes.Location;

public class Register {
	private static final String registryUrl = "";
	
	/**
	 * 
	 * @param type - the type of the calling class (producer/consumer)
	 * @param location
	 * @return json string of results or null if error occured
	 */
	public static String call(String type, Location location) {
		JSONObject obj = new JSONObject();
		obj.put("type", type);
		obj.put("location", location.getJsonObj().toJSONString());
		String jsonPayload = obj.toJSONString();
		String response = Network.callPost(registryUrl, jsonPayload);
		return response;		
	}

}
