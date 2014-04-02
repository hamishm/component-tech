package tests.middleware;

import org.junit.Test;

import client.ClientCollection;
import client.Consumer;

public class TestClient {
	
	@Test
	public void testClientCollection() throws InterruptedException {
		ClientCollection c = new ClientCollection();
		c.start();
		Thread.currentThread().sleep(1000);
		c.stop();
	}
	
	public void testConsumer() throws InterruptedException{
		Consumer c = new Consumer();
		c.consume();
		c.consume();
		c.consume();
	}
}
