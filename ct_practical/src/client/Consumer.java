package client;

import methods.GetSensorData;
import methods.Handshake;

import org.joda.time.DateTime;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import dataTypes.Date;
import dataTypes.Location;
import dataTypes.Response;
import dataTypes.SensorData;


public class Consumer extends Client{
	private String consumerId = null;
	
	Location interestLoc = new Location(56.322629,-2.985964);
	double interestRadius = 10;
	
	public Consumer() {
	}

	public void produce() {
		if(consumerId == null){
			consumerId = Handshake.call(
					this.brokerUrl, this.interestLoc, interestRadius);
		}
		handleGetData();
	}
	
	public String getType(){
		return "consumer";
	}

	/**
	 * Sends data if the producer has a session id and 
	 * broker url
	 */
	private void handleGetData() {
		if (brokerUrl != null && sessionId != null && consumerId != null) {
			Response r = GetSensorData.call(brokerUrl, consumerId);
			if(r == null || r.code != 200){
				System.err.println("Consumer " + name + " Failed to get a response.");
			} else {
				JSONArray result = (JSONArray)JSONValue.parse(r.body);
				for (Object obj : result) {
					JSONObject object = (JSONObject)obj;
					JSONObject location = (JSONObject)object.get("location");
					double lon = ((Double)location.get("longitude")).doubleValue();
					double lat = ((Double)location.get("latitude")).doubleValue();
					if (lat < interestLoc.getLatitude() - interestRadius ||
						    lat > interestLoc.getLatitude() + interestRadius ||
						    lon < interestLoc.getLongitude() - interestRadius ||
						    lon > interestLoc.getLongitude() + interestRadius) {
						System.err.println("Consumer " + name + " received sensor data for wrong location");
					}
				}
				System.out.println("Consumer " + name + " Success: " + r.body);
			}
		}
	}

	/**
	 * Produces a random location around Fife the first time this method is
	 * called. Returns same location from then on
	 * 
	 * @return the location of the client
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
