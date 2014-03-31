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

import dataTypes.Location;
import dataTypes.Response;
import dataTypes.SensorData;

public class PostSensorData {
	private final String HttpMethod = "POST";
	private final String path = "/produce";
	
	private String host;
	private String sessionId;
	
	Response response = null;
	
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
	 * Calls this method
	 * @param url - the URL of the associated broker
	 * @throws IOException 
	 * @throws ClientProtocolException 
	 * @throws URISyntaxException 
	 */
	public Response call(Location location, SensorData data) 
			throws ClientProtocolException, IOException, URISyntaxException{
		URI uri = new URIBuilder()
				.setScheme("http")
				.setHost(host)
				.setPath(path+"/"+sessionId)
				.build();
		
		System.out.println("Calling: " + uri);
		
		//http stuff here
		CloseableHttpClient httpclient = HttpClients.createDefault();
		HttpPost httppost = new HttpPost(uri);

		httppost.addHeader("Accept", "application/json");
		httppost.addHeader("Accept-Charset", "utf-8");
		httppost.addHeader("Content-Length", "0");
		//add json message:
		StringEntity entity = new StringEntity(
				data.getJsonObj().toJSONString(),
				"UTF-8");
		System.out.println(IOUtils.toString(entity.getContent()));
		httppost.setEntity(entity);
		HttpResponse response = httpclient.execute(httppost);
		if(response.getStatusLine().getStatusCode() != 200){
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
	public void call(){
		
	}
}
