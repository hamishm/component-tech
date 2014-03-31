package tests.dataTypes;

import static org.junit.Assert.assertTrue;

import org.json.simple.JSONObject;
import org.junit.Test;

import dataTypes.Location;

public class TestLocation {

	@Test
	public void testToJSON() {
		final double lati = 123.12;
		final double longi = 456.6;
		Location loc = new Location(lati,longi);
		JSONObject obj = loc.getJsonObj();
		double glat = ((Number)obj.get("latitude")).doubleValue();
		double glong = ((Number)obj.get("longitude")).doubleValue();
		assertTrue("lat values differed",glat == lati);
		assertTrue("long values differed",glong == longi);
	}
	
	@Test
	public void testFromJSON() {
		final double lati = 123.12;
		final double longi = 456.6;
		JSONObject obj = new JSONObject();
		obj.put("latitude", lati);
		obj.put("longitude", longi);
		Location loc = new Location(obj);
		assertTrue("lat values differed",loc.getLatitude() == lati);
		assertTrue("long values differed",loc.getLongitude() == longi);
	}
}
