package methods;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import core.Network;
import dataTypes.Response;

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
	public static String call(String brokerUrl) {
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
		String response = Network.callGet(uri.toString());
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
