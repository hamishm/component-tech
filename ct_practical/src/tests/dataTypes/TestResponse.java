package tests.dataTypes;

import static org.junit.Assert.assertTrue;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.junit.Test;

import dataTypes.Response;
import dataTypes.SensorData;

public class TestResponse {

	@Test
	public void testToJSON() {
		SensorData d1 = TestUtils.getSensorData();
		SensorData d2 = TestUtils.getSensorData();
		JSONObject obj = new JSONObject();
		obj.put("errors", null);
		obj.put("meta", null);
		JSONArray dataArr = new JSONArray();
		dataArr.add(d1.getJsonObj());
		dataArr.add(d2.getJsonObj());
		obj.put("data",dataArr);
		Response r = new Response(obj);
		assertTrue(r.toString().length()>10); //err it works
	}
	
	@Test
	public void testFromJSON() {
		SensorData d1 = TestUtils.getSensorData();
		SensorData d2 = TestUtils.getSensorData();
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
		assertTrue(r.getData().get(0).getLocation().getLatitude() == TestUtils.getLocation().getLatitude());
	}
	

}

