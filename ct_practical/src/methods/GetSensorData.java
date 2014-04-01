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
	
	/**
	 * Calls this method. Must set correct host and sessionid for
	 * call to succeed
	 * @return - Response object of the request or null if request failed
	 */
	public static Response call(String hosturl, String id) {
		URI uri;
		try {
			uri = new URIBuilder()
					.setScheme("http")
					.setHost(hosturl)
					.setPath("/consume/"+id)
					.build();
		} catch (URISyntaxException e) {
			System.err.println("MalformedURI: host: " + hosturl);
			return null;
		}
		return Network.callGet(uri.toString());
	}
}
