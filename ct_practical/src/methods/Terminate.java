package methods;

import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.client.utils.URIBuilder;

import core.Network;
import dataTypes.Response;

public class Terminate {
	public static Response call(String brokerUrl, String consumerId){
		URI uri;
		try {
			uri = new URIBuilder()
					.setScheme("http")
					.setHost(brokerUrl)
					.setPath("/consumer/"+consumerId)
					.build();
		} catch (URISyntaxException e) {
			System.err.println("MalformedURI: host: " + brokerUrl);
			return null;
		}
		return  Network.callDelete(uri.toString());
	}
}
