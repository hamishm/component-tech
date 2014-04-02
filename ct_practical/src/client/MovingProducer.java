package client;

import dataTypes.Location;

/**
 * Simulates someone who is walking around Fife.
 */
public class MovingProducer extends Producer {

	private double speed = .0001; // gps coord/second
	private long lastCalled;
	private double[] targetPoint = null;
	private double[] currentPoint  = null;
	
	public MovingProducer(){
		super(TestTools.getRandomLocation(.8, .8));
		currentPoint = new double[]{this.location.getLatitude(), this.location.getLongitude()};
	}
	
	/**
	 * Generates a location based on random movement
	 */
	@Override
	public Location getLocation() {
		if(targetPoint == null){
			targetPoint = TestTools.getRandomPoint(.8,.8);
		}
		long time = System.currentTimeMillis() - lastCalled;
		time /= (1000); //how many seconds have past since last called?
		double dist = time * speed;
		currentPoint = TestTools.moveTowards(currentPoint, targetPoint, dist);
		if(TestTools.distance(currentPoint, targetPoint) < .002)
			targetPoint = null;
		lastCalled = System.currentTimeMillis();
		return new Location(currentPoint[0], currentPoint[1]);
	}

}
