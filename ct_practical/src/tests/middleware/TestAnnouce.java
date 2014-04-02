package tests.middleware;

import static org.junit.Assert.assertTrue;
import methods.Annouce;
import methods.GetSensorData;
import methods.Handshake;
import methods.PostSensorData;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.junit.Test;

import client.TestTools;
import dataTypes.Location;
import dataTypes.Response;

public class TestAnnouce {

	@Test
	public void testAnnounceAsConsumer(){
		Response r = Annouce.callAsConsumer(null, null, TestTools.getRandomLocation(.1, .1), .1);
		JSONObject obj = (JSONObject) JSONValue.parse(r.body);
		String url  = (String)obj.get("broker_url");
		System.out.println("Test AnnounceAsConsumer got: " + r.body);
		assertTrue(url != null);
	}
	
	@Test
	public void testAnnounceAsProducer(){
		Response r = Annouce.callAsProducer(TestTools.getRandomLocation(.1, .1));
		System.out.println(r.body);
		JSONObject obj = (JSONObject) JSONValue.parse(r.body);
		String url  = (String)obj.get("broker_url");
		System.out.println("Test AnnounceAsProducer got: " + r.body);
		assertTrue(url != null);
	}
	
	@Test
	public void testAnnounceAsProducerWithBroker(){
		Location l = new Location(5,5);
		Response r = Annouce.callAsProducer(l);
		JSONObject obj = (JSONObject) JSONValue.parse(r.body);
		String url  = (String)obj.get("broker_url");
		url = url.substring(url.indexOf("http://")+7,url.length()-1);
		assertTrue(url != null);
		Response r2 = PostSensorData.call(url, TestTools.getData(l));
		assertTrue(r2!=null);
		
	}
	
	@Test
	public void testAnnounceAsConsumerWithBroker() throws InterruptedException{
		Location l = new Location(5,5);
		Response r = Annouce.callAsConsumer(null, null, l, 1);
		JSONObject obj = (JSONObject) JSONValue.parse(r.body);
		System.out.println(r.body);
		String url  = (String)obj.get("broker_url");
		assertTrue(url != null);
		url = url.substring(url.indexOf("http://")+7,url.length()-1);
		System.out.println(url);
		String sessionid = Handshake.call(url, l, 1);
		System.out.println(sessionid);
		assertTrue(sessionid != null);
		
		Response r4 = PostSensorData.call(url, TestTools.getData(l));
		System.out.println(r4.body);
		PostSensorData.call(url, TestTools.getData(l));
		PostSensorData.call(url, TestTools.getData(l));
		
		Thread.currentThread().sleep(1000);
		
		Response r2 = GetSensorData.call(url, sessionid);
		assertTrue(r2!=null);
		System.out.println(r2.body);
		
	}
	
}
