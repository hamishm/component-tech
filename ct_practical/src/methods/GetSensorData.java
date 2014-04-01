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
import org.json.simple.JSONObject;

import core.JsonObject;
import core.Network;
import dataTypes.Response;

public class GetSensorData {
	private final String path = "/consume";
	
	private String host;
	private String sessionId;
	
	
	public GetSensorData(String host, String sessionId){
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
	 * Calls this method. Must set correct host and sessionid for
	 * call to succeed
	 * @return - Response object of the request or null if request failed
	 */
	public Response call() {
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
		String response = Network.callGet(uri.toString());
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
