package client;

import methods.Register;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import dataTypes.Location;

public abstract class Client implements Runnable{
	protected int sleepTime = 2000; // 2 seconds

	private Thread thread = null;
	private boolean running = false;

	protected Location location = null;
	
	private double lastRegistered = 0.0;

	protected String brokerUrl = "localhost";
	protected String sessionId = "bogusValue";
	
	protected String name = String.valueOf((int)(Math.random()*10000));
	
	protected void tick(){
		register();
	}
	
	public Location getLocation(){
		return location;
	}
	
	public String getType(){
		return "client";
	}
	
	@Override
	public void run() {
		running = true;
		while (running) {
			try {
				tick();
				thread.sleep(sleepTime);
			} catch (Exception e) {
				if (e instanceof InterruptedException) {
					running = false;
					thread.interrupt();
					thread = null;
				} else {
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * Starts the client on its dedicated thread 
	 */
	public void start(){
		if(thread == null){
			thread = new Thread(this);
			thread.start();
		}
	}

	/**
	 * kills the client
	 */
	public void stop() {
		if(running){
			thread.interrupt();
		}
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


}
