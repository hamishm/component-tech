package client;

import methods.GetSensorData;

import org.joda.time.DateTime;

import dataTypes.Date;
import dataTypes.Location;
import dataTypes.Response;
import dataTypes.SensorData;

public class Consumer extends Client{
	protected final String type = "consumer";
	
	private GetSensorData getDataMethod = null;

	public Consumer() {
		getDataMethod = new GetSensorData(brokerUrl, brokerUrl);
	}

	public void produce() {
		handleGetData();
	}

	/**
	 * Sends data if the producer has a session id and 
	 * broker url
	 */
	private void handleGetData() {
		if (brokerUrl != null && sessionId != null) {
			getDataMethod.setHost(brokerUrl);
			getDataMethod.setSessionId(sessionId);
			Response r = getDataMethod.call();
			if(r == null){
				System.err.println("Producer failed to get a response.");
			}
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
				getLocation());
		rand.addData(Math.random());
		return rand;
	}
	
	protected void tick(){
		super.tick();
		produce();
	}
}
