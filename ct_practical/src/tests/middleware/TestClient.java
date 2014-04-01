package tests.middleware;

import org.junit.Test;

import client.ClientCollection;

public class TestClient {
	
	@Test
	public void testClientCollection() throws InterruptedException {
		ClientCollection c = new ClientCollection();
		c.staggerStart();
		Thread.currentThread().sleep(1000);
		c.stopAll();
	}
}
