package client;

import methods.PostSensorData;

import org.joda.time.DateTime;

import dataTypes.Date;
import dataTypes.Location;
import dataTypes.Response;
import dataTypes.SensorData;

public class Producer extends Client {

	public void produce() {
		handlePostData();
	}

	/**
	 * Sends data if the producer has a session id and 
	 * broker url
	 */
	private void handlePostData() {
		if (brokerUrl != null && sessionId != null) {
			Response r = PostSensorData.call(brokerUrl, sessionId, getData());
			if(r == null || r.code != 200){
				System.err.println("Producer " + name + " Failed to get a response.");
			} else {
				System.out.println("Producer " + name + " Success: " + r.body);
			}
		}
	}


	/**
	 * Produces a random location around Fife the first time this method is
	 * called. Returns same location from then on
	 * 
	 * Clusters locations close to Fife
	 * 
	 * @return random location in Fife
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
	
	public String getType(){
		return "producer";
	}

	@Override
	protected void tick() {
		super.tick();
		produce();
	}

}
