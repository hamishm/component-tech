package tests.dataTypes;

import dataTypes.Data;
import dataTypes.Date;
import dataTypes.Location;
import dataTypes.SensorData;

public class TestUtils {
	public static SensorData getSensorData(){
		SensorData sd = new SensorData(getDate(), getLocation(), getData());
		return sd;
	}
	
	public static  Location getLocation(){
		final double lati = 123.12;
		final double longi = 456.6;
		Location loc = new Location(lati,longi);
		return loc;
	}
	
	public static  Date getDate(){
		String dateS = "2003-01-1712:12:12.342-0800";
		Date d = new Date(dateS);
		return d;
	}
	
	public static  Data getData(){
		Data d = new Data("ExampleDataString");
		return d;
	}
}
