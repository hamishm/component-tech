package producer;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;

public class Producer2 implements Runnable {
	public static final int SLEEP_TIME = 2000;

	private final String id;
	private boolean running = false;
	private boolean handlingRequest = false;

	public Producer2() {
		this.id = "";
	}

	public Producer2(String id) {
		this.id = id;
	}

	@Override
	public void run() {
		running = true;
		while (running) {
			try {
				Thread.currentThread().sleep(SLEEP_TIME);
				System.out.println("Producer thread tick:");
				sendPost();
			} catch (Exception e) {
				if (e instanceof InterruptedException) {
					System.out
							.print("Producer thread interupted. Killing thread.");
					running = false;
					while (handlingRequest)
						;
					;
					Thread.currentThread().interrupt();
				} else {
					System.err.println("Error in request");
					e.printStackTrace();
				}
			}
		}
	}

	private void sendPost() throws ClientProtocolException, IOException {
		HttpClient httpclient = HttpClients.createDefault();
		HttpPost httppost = new HttpPost("http://www.a-domain.com/foo/");

		// Request parameters and other properties.
		List<NameValuePair> params = new ArrayList<NameValuePair>(1);
		params.add(new BasicNameValuePair("json", "[56.336078099999995,-2.8234819]"));
		httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));

		// Execute and get the response.
		HttpResponse response = httpclient.execute(httppost);
		HttpEntity entity = response.getEntity();

		if (entity != null) {
			InputStream instream = entity.getContent();
			try {
				// do something useful
			} finally {
				instream.close();
			}
		}
	}
}
