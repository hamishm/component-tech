package client;

import methods.PostSensorData;
import dataTypes.Response;

public class Producer extends Client {

	public Producer(){
		super(TestTools.getRandomLocation());
	}
	
	public void produce() {
		handlePostData();
	}

	/**
	 * Sends data if the producer has a session id and 
	 * broker url
	 */
	private void handlePostData() {
		if (brokerUrl != null && sessionId != null) {
			Response r = PostSensorData.call(brokerUrl, sessionId, TestTools.getData(getLocation()));
			if(r == null || r.code != 200){
				System.err.println("Producer " + name + " Failed to get a response.");
			} else {
				
			}
		}
	}

	
	
	public String getType(){
		return "producer";
	}

	@Override
	protected void onTick() {
		super.onTick();
		produce();
	}

}
