package tests.dataTypes;

import static org.junit.Assert.*;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.junit.Test;

import dataTypes.Data;
import dataTypes.Date;
import dataTypes.Location;
import dataTypes.Response;
import dataTypes.SensorData;

public class TestResponse {

	@Test
	public void testToJSON() {
		SensorData d1 = getSensorData();
		SensorData d2 = getSensorData();
		JSONObject obj = new JSONObject();
		obj.put("errors", null);
		obj.put("meta", null);
		JSONArray dataArr = new JSONArray();
		dataArr.add(d1.getJsonObj());
		dataArr.add(d2.getJsonObj());
		obj.put("data",dataArr);
		Response r = new Response(obj);
		System.out.println(r.getJsonObj().toJSONString());
	}
	
	@Test
	public void testFromJSON() {
		SensorData d1 = getSensorData();
		SensorData d2 = getSensorData();
		JSONObject obj = new JSONObject();
		obj.put("errors", null);
		obj.put("meta", null);
		JSONArray dataArr = new JSONArray();
		dataArr.add(d1.getJsonObj());
		dataArr.add(d2.getJsonObj());
		obj.put("data",dataArr);
		Response r = new Response(obj);
		assertTrue(r.getError().getValue() == null);
		assertTrue(r.getData().size() == 2);
		assertTrue(r.getData().get(0).getLocation().getLatitude() == getLocation().getLatitude());
	}
	
	private SensorData getSensorData(){
		SensorData sd = new SensorData(getDate(), getLocation(), getData());
		return sd;
	}
	
	private Location getLocation(){
		final double lati = 123.12;
		final double longi = 456.6;
		Location loc = new Location(lati,longi);
		return loc;
	}
	
	private Date getDate(){
		String dateS = "2003-01-1712:12:12.342-0800";
		Date d = new Date(dateS);
		return d;
	}
	
	private Data getData(){
		Data d = new Data("ExampleDataString");
		return d;
	}
}

