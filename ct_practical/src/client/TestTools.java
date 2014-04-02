package client;

import org.joda.time.DateTime;

import dataTypes.Date;
import dataTypes.Location;
import dataTypes.SensorData;

public class TestTools {

	/**
	 * Produces a random location between 0,0 and 1,1
	 * @return Location
	 */
	public static Location getRandomLocation(){
		double lat, lon;
		lat = Math.random() ;
		lon = Math.random() ;
		return new Location(lat, lon);
	}
	
	/**
	 * Random point between 0,0 and bounds
	 */
	public static double[] getRandomPoint(double xBound, double yBound){
		double[] coords = new double[2];
		coords[0] = (Math.random()*1000)%xBound;
		coords[1] = (Math.random()*1000)%yBound;
		return coords;
	}
	
	public static Location getRandomLocation(double xBound, double yBound){
		double[] coords = getRandomPoint(xBound, yBound);
		return new Location(coords[0], coords[1]);
	}
	
	/**
	 * Gets a random sensor data with the current time, location of the
	 * producer, and a random number as the sensor data value
	 * 
	 * @return
	 */
	public static SensorData getData(Location loc) {
		SensorData rand = new SensorData(new Date(DateTime.now()),
				loc);
		rand.addData(Math.random());
		return rand;
	}
	
	/**
	 * Checks if the given point is within the square bounding
	 * box defined by the radius
	 * @param center
	 * @param test
	 * @param radius 
	 * @return true if in bounding box
	 */
	public static boolean pointInBox(Location interestLoc,
			double interestRadius, Location loc) {
		double x = interestLoc.getLatitude() - interestRadius;
		double y = interestLoc.getLongitude() - interestRadius;
		if(loc.getLatitude() >= x && loc.getLatitude() <= (x+interestRadius*2)
				&& loc.getLatitude() >= y && loc.getLongitude() <= (y+interestRadius*2))
			return true;
		return false;
	}

	public static double distance(double[] p1, double[] p2){
		double dist = Math.sqrt(
				((p1[0]-p2[0])*(p1[0]-p2[0])) + 
				(p1[1]-p2[1])*(p1[1]-p2[1]));
		return dist;
	}
	
	/**
	 * Returns the point on a line representing a 
	 * step taken towards a point
	 * @param start
	 * @param goal
	 * @param dist
	 * @return goal if you were to overshoot the target
	 */
	public static double[] moveTowards(double[] start, double[] goal, double dist){
		if(dist > distance(start,goal))
			return goal;
		double angle = Math.atan2(goal[0]-start[0], goal[1]-start[1]);
		double x = Math.cos(angle)*dist + start[0];
		double y = Math.sin(angle)*dist + start[1];
		return new double[] {x,y};
	}
	
}
