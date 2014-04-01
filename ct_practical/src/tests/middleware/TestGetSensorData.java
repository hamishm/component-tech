package tests.middleware;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.URISyntaxException;

import methods.GetSensorData;

import org.apache.http.client.ClientProtocolException;
import org.junit.Test;

import dataTypes.Response;

public class TestGetSensorData {

	@Test
	public void testCall() {
		Response r = GetSensorData.call("www.google.com", "");
		assertTrue(r == null);
	}

}
