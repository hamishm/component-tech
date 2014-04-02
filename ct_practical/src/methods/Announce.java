package methods;

import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.client.utils.URIBuilder;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import core.Network;
import dataTypes.Location;
import dataTypes.Response;

public class Announce {

	private static String registryUrl = "localhost";

	/**
	 * Calls the announce method
	 * 
	 * @param brokerUrl
	 * @return sessionId issued by the broker or null if call failed for any
	 *         reason
	 */
	public static Response callAsConsumer(String registryId, String brokerUrl,
			Location loc, double radius) {
		URI uri;
		try {
			if (registryId == null) {
				uri = new URIBuilder().setScheme("http").setHost(registryUrl)
						.setPath("/announce/consumer").build();
			} else {
				uri = new URIBuilder().setScheme("http").setHost(registryUrl)
						.setPath("/announce/consumer/" + registryId).build();
			}
		} catch (URISyntaxException e) {
			System.err.println("MalformedURI in call announce Consumer: url: "
					+ registryUrl);
			return null;
		}
		String payload = "{\"latitude\":" + loc.getLatitude()
				+ ",\"longitude\":" + loc.getLongitude() + ",\"radius\":"
				+ radius + "}";
		Response response = Network.callPost(uri.toString(), payload);
		if (response.body == null || response.body.equals(""))
			return null;
		return response;
	}

	/**
	 * Calls the announce method
	 * 
	 * @Return a response object, should contain a broker url
	 */
	public static Response callAsProducer(Location loc) {
		URI uri;
		try {
			uri = new URIBuilder().setScheme("http").setHost(registryUrl)
					.setPath("/announce/producer").build();
		} catch (URISyntaxException e) {
			System.err.println("MalformedURI in call announce Consumer: url: "
					+ registryUrl);
			return null;
		}
		String payload = "{\"latitude\":" + loc.getLatitude()
				+ ",\"longitude\":" + loc.getLongitude() + "}";
		Response response = Network.callPost(uri.toString(), payload);
		if (response.body==null || response.body.equals(""))
			return null;
		return response;
	}
}
