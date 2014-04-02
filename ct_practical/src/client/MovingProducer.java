package client;

import dataTypes.Location;

public class MovingProducer extends Producer {

	private double speed = .001; // gps coord/second
	private long lastCalled;
	private double[] targetPoint = null;
	private double[] currentPoint  = null;
	
	public MovingProducer(){
		super(TestTools.getRandomLocation(.8, .8));
		currentPoint = new double[]{this.location.getLatitude(), this.location.getLongitude()};
	}
	
	private void updateLocation(){
		if(targetPoint == null){
			targetPoint = TestTools.getRandomPoint(.8,.8);
		}
		long time = System.currentTimeMillis() - lastCalled;
		time /= (1000); //how many seconds have past since last called?
		double dist = time * speed;
		currentPoint = TestTools.moveTowards(currentPoint, targetPoint, dist);
		if(TestTools.distance(currentPoint, targetPoint) < .002){
			targetPoint = null;
		}
		lastCalled = System.currentTimeMillis();
		this.location.setLatitude(currentPoint[0]);
		this.location.setLongitude(currentPoint[1]);
	}
	
	protected void onTick(){
		updateLocation();
		super.onTick();
	}
}
