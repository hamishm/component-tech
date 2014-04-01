package methods;

import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.client.utils.URIBuilder;

import core.Network;
import dataTypes.Response;
import dataTypes.SensorData;

public class PostSensorData {
	private final String path = "/produce";
	
	private String host;
	private String sessionId;
	
	public PostSensorData(String host, String sessionId){
		this.host = host;
		this.sessionId = sessionId;
	}
	
	public void setHost(String hosturl){
		this.host = hosturl;
	}
	
	public void setSessionId(String id){
		this.sessionId = id;
	}
	
	/**
	 * Calls this object's network method. 
	 * Must set correct host and sessionid for
	 * call to succeed
	 * @return - string response of the request 
	 * or null if request failed
	 */
	public Response call(SensorData data) {
		URI uri;
		try {
			uri = new URIBuilder()
					.setScheme("http")
					.setHost(host)
					.setPath(path+"/"+sessionId)
					.build();
		} catch (URISyntaxException e) {
			System.err.println("MalformedURI: host: " + host + " path: " + path);
			return null;
		}
		String response = Network.callPost(uri.toString(), data.getJsonObj().toJSONString());
		if(response != null){
			try{
				return new Response(response);
			} catch (Exception e){
				System.err.println("Malformed response: " + response);
			}
		}
		return null;
	}
	
}
