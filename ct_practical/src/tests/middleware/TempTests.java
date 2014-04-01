package tests.middleware;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import tests.dataTypes.TestUtils;
import methods.Handshake;

public class TempTests {

	@Test
	public void testHandshake(){
		String r = Handshake.call("localhost", TestUtils.getLocation(), 10);
		System.out.println(r);
		assertTrue(r!=null);
	}
	
}
