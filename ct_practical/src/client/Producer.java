package client;

import methods.PostSensorData;
import dataTypes.Location;
import dataTypes.Response;

public class Producer extends Client {

	public Producer(Location loc) {
		super(loc);
	}

	public Producer() {
		super(TestTools.getRandomLocation());
	}

	public void produce() {
		postData();
	}

	/**
	 * Sends data if the producer has a session id and broker url
	 */
	protected void postData() {
		if (brokerUrl == null) {
			System.err.println("Consumer called consume with no broker url");
			return;
		}
		Response r = PostSensorData.call(brokerUrl,
				TestTools.getData(getLocation()));
		if (r == null || r.code != 200) {
			System.err.println("Producer " + name
					+ " Failed to get a response.");
		} else {
			// success
		}
	}

	public String getType() {
		return "producer";
	}

	@Override
	protected void onTick() {
		super.onTick();
		produce();
	}

}
