package tests.middleware;

import org.junit.Test;

import client.ProducerCollection;
import client.Consumer;

public class TestClient {
	
	@Test
	public void testClientCollection() throws InterruptedException {
		ProducerCollection c = new ProducerCollection();
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
