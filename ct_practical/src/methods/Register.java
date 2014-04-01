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
	public Response call(Location location) throws URISyntaxException, IOException {
		URI uri = new URIBuilder()
			.setScheme("http")
			.setHost(registryUrl)
			.setPath("register/"+callingType)
			.build();

		System.out.println("Calling: " + uri);

		// http stuff here
		CloseableHttpClient httpclient = HttpClients.createDefault();
		HttpPost httppost = new HttpPost(uri);

		httppost.addHeader("Accept", "application/json");
		httppost.addHeader("Accept-Charset", "utf-8");
		httppost.addHeader("Content-Length", "0");
		// add json message:
		StringEntity entity = new StringEntity(
				location.getJsonObj().toJSONString(), "UTF-8");
		System.out.println(IOUtils.toString(entity.getContent()));
		httppost.setEntity(entity);
		HttpResponse response = httpclient.execute(httppost);
		if (response.getStatusLine().getStatusCode() != 200) {
			System.err.println("Error response code:");
			System.err.println(response.getStatusLine().toString());
			return null;
		}

		HttpEntity rEntity = response.getEntity();
		if (rEntity != null) {
			InputStream instream = rEntity.getContent();
			try {
				String responseJson = IOUtils.toString(instream, "utf-8");
				System.out.println(responseJson);
				Response r = new Response(responseJson);
				return r;
			} finally {
				instream.close();
			}
		}

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

}
