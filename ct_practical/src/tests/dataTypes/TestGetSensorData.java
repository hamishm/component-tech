package tests.dataTypes;

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
		GetSensorData get = new GetSensorData("www.google.com","sdlkgndflkgn2324");
		Response r = null;
		try {
			r = get.call();
		} catch (IOException | URISyntaxException e) {
			e.printStackTrace();
		}
		assertTrue(r == null);
	}

}
