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
	
}
