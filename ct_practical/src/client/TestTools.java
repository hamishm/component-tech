package client;

import org.joda.time.DateTime;

import dataTypes.Date;
import dataTypes.Location;
import dataTypes.SensorData;

public class TestTools {

	/**
	 * Produces a random location
	 * @return Location
	 */
	public static Location getRandomLocation(){
		double lat, lon;
		lat = Math.random() % .4;
		lon = Math.random() % .4;
		return new Location(lat, lon);
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
		if(loc.getLatitude() >= x && loc.getLatitude() <= (x+interestRadius)
				&& loc.getLatitude() >= y && loc.getLongitude() <= (y+interestRadius))
			return true;
		return false;
	}

	
	
}
