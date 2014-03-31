package tests.dataTypes;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.URISyntaxException;

import methods.PostSensorData;

import org.junit.Test;

import dataTypes.Response;

public class TestPostSensorData {

	@Test
	public void testCall() {
		PostSensorData post = new PostSensorData("www.google.com","sdlkgndflkgn2324");
		Response r = null;
		try {
			r = post.call(TestUtils.getLocation(), TestUtils.getSensorData());
		} catch (IOException | URISyntaxException e) {
			e.printStackTrace();
		}
		assertTrue(r == null);
	}
	
}
