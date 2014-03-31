package tests.dataTypes;

import static org.junit.Assert.assertTrue;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.junit.Test;

import dataTypes.Data;
import dataTypes.Location;
import dataTypes.SensorData;

public class TestSensorData {

	@Test
	public void testFromJson() {
		String js = "{\"location\":"
				+ "{\"latitude\":\"213.213\",\"longitude\":\"432.21\"},"
				+ "\"date\":\"2003-01-1712:12:12.342-0800\","
				+ "\"data\":{\"contents\":\"five\"}}";
		JSONObject sdj = new JSONObject();
		sdj.put("location", new Location(300,400).getJsonObj());
		sdj.put("date", "2003-01-1712:12:12.342-0800");
		sdj.put("data", new Data("value").getJsonObj());
		SensorData sd = new SensorData(sdj);
		
		assertTrue(sd.getData().getJsonObj().get("contents").equals("value"));
	}
	
	@Test
	public void testFromJsonString() {
		String js = "{\"location\":"
				+ "{\"latitude\":213.213,\"longitude\":432.21},"
				+ "\"date\":\"2003-01-1712:12:12.342-0800\","
				+ "\"data\":{\"contents\":\"five\"}}";
		JSONObject obj = (JSONObject)JSONValue.parse(js);
		SensorData sd = new SensorData(obj);
		assertTrue(sd.getData().getJsonObj().get("contents").equals("five"));
		JSONObject loc = (JSONObject) sd.getLocation().getJsonObj();
		assertTrue("was: " +loc.get("longitude"),(loc.get("longitude")).equals(432.21));
	}
	
	@Test
	public void testToJson(){
		
	}

}
