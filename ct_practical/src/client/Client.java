package client;


import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import dataTypes.Location;
import dataTypes.Response;
import methods.Announce;

public abstract class Client extends Runner {
	protected Location location = null;
	
	private double lastRegistered = 0.0;

	protected String brokerUrl = null;
	protected String registryId = null;
	
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
				|| registryId == null
				|| (System.currentTimeMillis() - lastRegistered > 10 * 1000)) {
			Response response = null;
			if(getType().equals("producer")){
				response = Announce.callAsProducer(getLocation());
			} else {
				response = Announce.callAsConsumer(registryId, brokerUrl, getLocation(), ((Consumer)this).interestRadius);
			}
			if(response != null){
				JSONObject obj = (JSONObject) JSONValue.parse(response.body);
				this.registryId = (String)obj.get("consumer_id");
				this.brokerUrl = (String)obj.get("broker_url");
				if(brokerUrl != null){
					brokerUrl = brokerUrl.substring(7, brokerUrl.length()-1);
				}
			}
			lastRegistered = System.currentTimeMillis();
		}
		if (getType().equals("consumer") && registryId ==null) {
			System.err.println("Error getting consumerId "+getType()+" "+name+" from registery");
		}
		if (brokerUrl == null) {
			System.err.println("Error getting brokerUrl "+getType()+" "+name+" with registery");
		}
	}

	protected void onTick(){
		register();
	}

}
