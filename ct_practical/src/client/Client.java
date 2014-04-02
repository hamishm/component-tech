package client;

import methods.Register;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import dataTypes.Location;

public abstract class Client {
	protected Location location = null;
	
	private double lastRegistered = 0.0;

	protected String brokerUrl = "localhost";
	protected String sessionId = "bogusValue";
	
	protected String name = String.valueOf((int)(Math.random()*10000));
	
	public Client(){
		this.location = new Location(0,0);
	}
	
	public Client(Location loc){
		this.location = loc;
	}
	
	public Location getLocation(){
		return location;
	}
	
	public String getType(){
		return "client";
	}
	
	/**
	 * Calls the registry to get a 
	 * sessionid and broker url
	 */
	private void register() {
		if (brokerUrl == null
				|| sessionId == null){
				//|| (System.currentTimeMillis() - lastRegistered > 60 * 10 * 1000)) {
			String response = Register.call(getType(), getLocation());
			if(response != null){
				JSONObject obj = (JSONObject) JSONValue.parse(response);
				this.sessionId = (String)obj.get("session_id");
				this.brokerUrl = (String)obj.get("broker_url");
			}
			lastRegistered = System.currentTimeMillis();
		}
		if (sessionId == null || brokerUrl == null) {
			System.err.println("Error registering client with registery: " + name);
		}
	}

	protected void onTick(){
		register();
	}

}
