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


public class Consumer extends Client {
	private String consumerId = null;
	
	Location interestLoc = null;
	double interestRadius = 1;
	
	/**
	 * Initialized the consumer, placing it at 0,0
	 * and a random interest location
	 */
	public Consumer() {
		super();
		this.interestLoc = new Location(.5,.5);
	}
	
	public Consumer(Location loc, Location interestLoc, double interestRadius){
		super(loc);
		this.interestLoc = interestLoc;
		this.interestRadius = interestRadius;
	}

	public void consume() {
		if(brokerUrl == null){
			System.err.println("Consumer called consume with no broker url");
			return;
		}
		if(consumerId == null){
			consumerId = Handshake.call(
					this.brokerUrl, this.interestLoc, interestRadius);
		}
		int numResults = getData();
		System.out.println(this.name + " Consumer got "+ numResults + " from request");
	}
	
	public String getType(){
		return "consumer";
	}

	/**
	 * Sends data if the producer has a session id and 
	 * broker url
	 */
	private int getData() {
		int results = 0;
		if (brokerUrl != null && registryId != null && consumerId != null) {
			Response r = GetSensorData.call(brokerUrl, consumerId);
			if(r == null || r.code != 200){
				System.err.println("Consumer " + name + " Failed to get a response.");
			} else {
				JSONArray result = (JSONArray)JSONValue.parse(r.body);
				for (Object obj : result) {
					results++;
					JSONObject object = (JSONObject)obj;
					JSONObject location = (JSONObject)object.get("location");
					Location loc = new Location(location);
					if(!TestTools.pointInBox(interestLoc, interestRadius, loc)){
						System.err.println("Consumer " + name + " recieved unrequest location data:");
						System.err.println("Interest: " + interestLoc.toString() + " Radius: " + interestRadius);
						System.err.println("Recieved: " + loc.toString());
					}
				}
			}
		}
		return results;
	}
	
	protected void onTick(){
		super.onTick();
		consume();
	}
}
