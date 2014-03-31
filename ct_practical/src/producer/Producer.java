package producer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Producer implements Runnable {
	public static final int SLEEP_TIME = 2000;
	private static final String USER_AGENT = "Mozilla/5.0";

	private final String id;
	private boolean running = false;
	private boolean handlingRequest = false;

	public Producer() {
		this.id = "";
	}

	public Producer(String id) {
		this.id = id;
	}

	@Override
	public void run() {
		running = true;
		while (running) {
			try {
				Thread.currentThread().sleep(SLEEP_TIME);
				System.out.println("Producer thread tick:");
				sendGet();
			} catch (Exception e) {
				if (e instanceof InterruptedException) {
					System.out.print("Producer thread interupted. Killing thread.");
					running = false;
					while(handlingRequest);;
					Thread.currentThread().interrupt();
				} else {
					System.err.println("Error in GET request");
					e.printStackTrace();
				}
			}
		}
	}

	// HTTP GET request
	private void sendGet() throws Exception {

		String url = "http://ms267.host.cs.st-andrews.ac.uk/ct/test.json";

		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();

		// method and header:
		con.setRequestMethod("POST");
		con.setRequestProperty("Accept", "application/json");
		con.setRequestProperty("Accept-Charset", "utf-8");
		con.setRequestProperty("User-Agent", USER_AGENT);
		
		
		
		System.out.println("\nSending 'GET' request to URL : " + url);
		int responseCode = -1;
		try{
			handlingRequest = true;
			responseCode = con.getResponseCode();
		} catch (IOException e){
			System.err.print("Connection failed");
			return;
		} finally{
			handlingRequest = false;
		}
		System.out.println("Response Code : " + responseCode);

		BufferedReader in = new BufferedReader(new InputStreamReader(
				con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();

		// print result
		System.out.println(response.toString());

	}

}
