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
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.json.simple.JSONObject;

import core.JsonObject;
import dataTypes.Response;

public class GetSensorData {
	private final String HttpMethod = "POST";
	private final String path = "/consume";
	
	private final String host;
	private final String sessionId;
	
	Response response = null;
	
	public GetSensorData(String host, String sessionId){
		this.host = host;
		this.sessionId = sessionId;
	}
	
	/**
	 * Calls this method
	 * @param url - the URL of the associated broker
	 * @throws IOException 
	 * @throws ClientProtocolException 
	 * @throws URISyntaxException 
	 */
	public Response call() throws ClientProtocolException, IOException, URISyntaxException{
		URI uri = new URIBuilder()
				.setScheme("http")
				.setHost(host)
				.setPath(path)
				.setParameter("id", sessionId)
				.build();
		
		System.out.println("Calling: " + uri);
		
		//http stuff here
		CloseableHttpClient httpclient = HttpClients.createDefault();
		HttpPost httppost = new HttpPost(uri);

		httppost.addHeader("Accept", "application/json");
		httppost.addHeader("Accept-Charset", "utf-8");
		httppost.addHeader("Content-Length", "0");

		HttpResponse response = httpclient.execute(httppost);
		if(response.getStatusLine().getStatusCode() != 200){
			System.err.println("Error response code:");
			System.err.println(response.getStatusLine().toString());
			return null;
		}
		
		HttpEntity entity = response.getEntity();
		if (entity != null) {
			InputStream instream = entity.getContent();
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
}
