package methods;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import dataTypes.Location;
import dataTypes.Response;


public class Register {
	private static final String registryUrl = "";
	
	String callingType = "";

	/**
	 * @return the url of the broker which it is registered with, or null if the
	 *         request failed
	 * @throws URISyntaxException 
	 * @throws IOException 
	 */
	public Response call(Location location) {

		return null;
	}

	public String getSessionId() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getBrokerUrl() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * 
	 * @param type - the type of the calling class (producer/consumer)
	 * @param location
	 * @return json string of results or null if error occured
	 */
	public static String call(String type, Location location) {
		return null;		
	}

}
