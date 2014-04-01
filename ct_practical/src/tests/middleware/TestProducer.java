package tests.middleware;

import static org.junit.Assert.assertFalse;

import org.junit.Test;

import client.MovingProducer;
import client.ProducerCollection;
import dataTypes.Location;

public class TestProducer {

	@Test
	public void testMovingProducer() throws InterruptedException {
		MovingProducer p = new MovingProducer();
		Location l1 = p.getLocation();
		Thread.currentThread().sleep(1000);
		Location l2 = p.getLocation();
		System.out.printf("%s versus %s\n",l1,l2);
		assertFalse(l1.getLatitude() == l2.getLatitude());
	}
	
	@Test
	public void testProducerCollection() throws InterruptedException {
		ProducerCollection p = new ProducerCollection();
		p.staggerStart();
		Thread.currentThread().sleep(1000);
		p.stopAll();
	}
	
}
