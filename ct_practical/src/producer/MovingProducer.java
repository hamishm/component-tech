package producer;

import dataTypes.Location;

/**
 * Simulates someone who is walking around Fife.
 */
public class MovingProducer extends Producer {

	private long speed = 5; // m/s of producer
	private long lastCalled;
	private double[] targetPoint = null;
	private double[] currentPoint;
	
	public MovingProducer(){
		currentPoint = getPointInFife();
	}
	
	/**
	 * Generates a location based on random movement
	 * but within the bounds of Fife.
	 */
	@Override
	public Location getLocation() {
		if(targetPoint == null){
			targetPoint = getPointInFife();
		}
		long time = System.currentTimeMillis() - lastCalled;
		time /= (1000); //how many seconds have past since last called?
		long dist = time * speed;
		currentPoint = moveTowards(currentPoint, targetPoint, dist);
		if(distance(currentPoint, targetPoint) < 5)
			targetPoint = getPointInFife();
		lastCalled = System.currentTimeMillis();
		return new Location(currentPoint[0], currentPoint[1]);
	}
	
	/**
	 * Gets a random point around Fife
	 * @return
	 */
	private double[] getPointInFife(){
		double xc = 56.322629; // cupar lat
		double yc = -2.985964; // cupar long
		double rand = Math.random();
		double radius = .193439 * rand;
		double angle = (rand * 10) % (Math.PI * 2);
		double lat = (radius * Math.cos(angle)) + xc;
		double lon = (radius * Math.sin(angle)) + yc;
		return new double[] {lat,lon};
	}
	
	/**
	 * @return distance in m between 2 gps coordinates
	 */
	private double distance(double[] p1, double[] p2){
		double distGps = Math.sqrt(
				((p1[0]-p2[0])*(p1[0]-p2[0])) + 
				(p1[1]-p2[1])*(p1[1]-p2[1]));
		return distGps*62040.31672;//conversion factor to m
	}
	
	/**
	 * Returns the point on a line representing a 
	 * step taken towards a point
	 * @param start
	 * @param goal
	 * @param dist
	 * @return goal if you were to overshoot the target
	 */
	private double[] moveTowards(double[] start, double[] goal, double dist){
		if(dist > distance(start,goal))
			return goal;
		double angle = Math.atan2(goal[0]-start[0], goal[1]-start[1]);
		double x = Math.cos(angle)*dist + start[0];
		double y = Math.sin(angle)*dist + start[1];
		return new double[] {x,y};
	}

}
