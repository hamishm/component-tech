package methods;

import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.client.utils.URIBuilder;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import core.Network;
import dataTypes.Location;

/**
 * Consumer calls this the first time it encounters the broker
 */
public class Handshake {

	/**
	 * Calls the handshake method to the broker
	 * @param brokerUrl
	 * @return sessionId issued by the broker or null
	 * if call failed for any reason
	 */
	public static String call(String brokerUrl, Location loc, double radius) {
		URI uri;
		try {
			uri = new URIBuilder()
				.setScheme("http")
				.setHost(brokerUrl)
				.setPath("/consumer")
				.build();			
		} catch (URISyntaxException e) {
			System.err.println("MalformedURI: url: " + brokerUrl);
			return null;
		}
		String payload = "{\"latitude\":" + loc.getLatitude()
				+",\"longitude\":"+loc.getLongitude()
				+",\"radius\":"+radius+"}";
		String response = Network.callPost(uri.toString(), payload);
		if(response != null){
			try{
				JSONObject jsonObj = (JSONObject) JSONValue.parse(response);
				return (String)jsonObj.get("session_id");
			} catch (Exception e){
				System.err.println("Malformed response: " + response);
			}
		}
		return null;
	}
	
	
	

}
