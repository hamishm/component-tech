package methods;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.protocol.HTTP;

import core.Network;
import dataTypes.Location;
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
