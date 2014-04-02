package methods;

import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.client.utils.URIBuilder;

import core.Network;
import dataTypes.Response;
import dataTypes.SensorData;

public class PostSensorData {
	
	/**
	 * Calls this object's network method. 
	 * Must set correct host and sessionid for
	 * call to succeed
	 * @return - string response of the request 
	 * or null if request failed
	 */
	public static Response call(String host, SensorData data) {
		URI uri;
		try {
			uri = new URIBuilder()
					.setScheme("http")
					.setHost(host)
					.setPath("/produce")
					.build();
		} catch (URISyntaxException e) {
			System.err.println("MalformedURI: host: " + host);
			return null;
		}
		
		return  Network.callPost(uri.toString(), data.getJsonObj().toJSONString());
	}
	
}
