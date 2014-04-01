package core;

import java.io.InputStream;
import java.net.URI;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import dataTypes.Response;

public class Network {

	/**
	 * Calls a post request using Apache libraries
	 * 
	 * @param url - the full url to send to
	 * @param payload - the message that should be sent along with the post request
	 * @return text body of the response, null if the request failed in anyway
	 */
	public static Response callPost(String url, String payload) {
		// set up request
		System.out.println(url);
		System.out.println(payload);
		
		Response r = null;
		
		CloseableHttpClient httpclient = HttpClients.createDefault();
		HttpPost httppost = new HttpPost(url);
		httppost.addHeader("Accept", "application/json");
		httppost.addHeader("Accept-Charset", "utf-8");
		StringEntity entity = new StringEntity(payload, "UTF-8");
		httppost.setEntity(entity);
		// make request
		try {
			HttpResponse response = httpclient.execute(httppost);
			r = new Response(response.getStatusLine().getStatusCode(), "");
			HttpEntity rEntity = response.getEntity();
			if (rEntity != null) {
				InputStream instream = rEntity.getContent();
				try {
					String responseJson = IOUtils.toString(instream, "utf-8");
					r.body = responseJson;
				} finally {
					instream.close();
				}
			}
		} catch (Exception e) {
			System.err.println("Something went wrong during network transaction.");
			e.printStackTrace();
		}
		return r;
	}

	/**
	 * Calls a get request using Apache libraries
	 * 
	 * @param url - the full url to send to
	 * @return text body of the response, null if the request failed to get a response
	 */
	public static Response callGet(String url) {
		System.out.println(url);
		Response r = null;
		
		CloseableHttpClient httpclient = HttpClients.createDefault();
		HttpGet httpget = new HttpGet(url);
		httpget.addHeader("Accept", "application/json");
		httpget.addHeader("Accept-Charset", "utf-8");
		try{
			HttpResponse response = httpclient.execute(httpget);
			r = new Response(response.getStatusLine().getStatusCode(), "");
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				InputStream instream = entity.getContent();
				try {
					String responseJson = IOUtils.toString(instream, "utf-8");
					r.body = responseJson;
				} finally {
					instream.close();
				}
			}
		} catch (Exception e){
			System.err.println("Something went wrong during network transaction.");
			return null;
		}
		return r;
	}

}
