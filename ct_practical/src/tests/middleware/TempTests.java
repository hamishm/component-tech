package tests.middleware;

import static org.junit.Assert.assertTrue;
import methods.Handshake;

import org.junit.Test;

import tests.dataTypes.TestUtils;
import client.Consumer;

public class TempTests {

	@Test
	public void testHandshake(){
		String r = Handshake.call("localhost", TestUtils.getLocation(), 10);
		System.out.println(r);
		assertTrue(r!=null);
	}
	
	@Test
	public void testSingleProducerConsumer(){
	}
	
}
