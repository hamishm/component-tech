package producer;

import java.io.IOException;
import java.net.URISyntaxException;

import org.apache.http.client.ClientProtocolException;
import org.joda.time.DateTime;

import methods.PostSensorData;
import methods.Register;
import dataTypes.Data;
import dataTypes.Date;
import dataTypes.Location;
import dataTypes.SensorData;

public class Producer implements Runnable {
	public static final int SLEEP_TIME = 2000; // 2 seconds

	private boolean running = false;
	private Register registerMethod = null;
	private PostSensorData postDataMethod = null;

	private Location location = null;

	private double lastRegistered = 0.0;
	private String brokerUrl = null;
	private String sessionId = null;

	public Producer() {
		registerMethod = new Register();
		postDataMethod = new PostSensorData(brokerUrl, brokerUrl);
	}

	public void produce() {
		handleRegister();
		handlePostData();
	}

	/**
	 * Sends data if the producer has a session id and 
	 * broker url
	 */
	private void handlePostData() {
		if (brokerUrl != null && sessionId != null) {
			postDataMethod.setHost(brokerUrl);
			postDataMethod.setSessionId(sessionId);
			try {
				postDataMethod.call(getLocation(), getData());
			} catch (IOException | URISyntaxException e) {
				System.err.println("Producer send failure:");
				e.printStackTrace();
			}
		}
	}

	/**
	 * Sets the Producer's sessionId and brokerUrl to the
	 * values which the middleware's registry assigns it
	 */
	private void handleRegister() {
		if (brokerUrl == null
				|| sessionId == null
				|| (System.currentTimeMillis() - lastRegistered > 60 * 10 * 1000)) {
			registerMethod.call();
			sessionId = registerMethod.getSessionId();
			brokerUrl = registerMethod.getBrokerUrl();
			lastRegistered = System.currentTimeMillis();
		}
		if (sessionId == null || brokerUrl == null) {
			System.err.println("Error registering.");
		}
	}

	/**
	 * Produces a random location around Fife the first time this method is
	 * called. Returns same location from then on
	 * 
	 * @return
	 */
	public Location getLocation() {
		if (location == null) { // initalizes a random location around Fife
			double xc = 56.322629; // cupar lat
			double yc = -2.985964; // cupar long
			double rand = Math.random();
			double radius = .193439 * rand;
			double angle = (rand * 10) % (Math.PI * 2);
			double lat = (radius * Math.cos(angle)) + xc;
			double lon = (radius * Math.sin(angle)) + yc;
			this.location = new Location(lat, lon);
		}
		return location;
	}

	/**
	 * Gets a random sensor data with the current time, location of the
	 * producer, and a random number as the sensor data value
	 * 
	 * @return
	 */
	public SensorData getData() {
		SensorData rand = new SensorData(new Date(DateTime.now()),
				getLocation(), new Data(String.valueOf(Math.random())));
		return rand;
	}

	@Override
	public void run() {
		running = true;
		while (running) {
			try {
				produce();
				Thread.currentThread().sleep(SLEEP_TIME);
			} catch (Exception e) {
				if (e instanceof InterruptedException) {
					running = false;
					Thread.currentThread().interrupt();
				} else {
					e.printStackTrace();
				}
			}
		}
	}

}
