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
	public static String callPost(String url, String payload) {
		// set up request
		System.out.println(url);
		System.out.println(payload);
		
		CloseableHttpClient httpclient = HttpClients.createDefault();
		HttpPost httppost = new HttpPost(url);
		httppost.addHeader("Accept", "application/json");
		httppost.addHeader("Accept-Charset", "utf-8");
		StringEntity entity = new StringEntity(payload, "UTF-8");
		httppost.setEntity(entity);
		// make request
		try {
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
					return responseJson;
				} finally {
					instream.close();
				}
			}
		} catch (Exception e) {
			System.err.println("Something went wrong during network transaction.");
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Calls a get request using Apache libraries
	 * 
	 * @param url - the full url to send to
	 * @return text body of the response, null if the request failed in anyway
	 */
	public static String callGet(String url) {
		System.out.println(url);
		CloseableHttpClient httpclient = HttpClients.createDefault();
		HttpGet httpget = new HttpGet(url);
		httpget.addHeader("Accept", "application/json");
		httpget.addHeader("Accept-Charset", "utf-8");
		try{
		HttpResponse response = httpclient.execute(httpget);
		if (response.getStatusLine().getStatusCode() != 200) {
			System.err.println("Error response code:");
			System.err.println(response.getStatusLine().toString());
			return null;
		}
		HttpEntity entity = response.getEntity();
		if (entity != null) {
			InputStream instream = entity.getContent();
			try {
				String responseJson = IOUtils.toString(instream, "utf-8");
				return responseJson;
			} finally {
				instream.close();
			}
		}
		} catch (Exception e){
			System.err.println("Something went wrong during network transaction.");
			return null;
		}
		return null;
	}

}
