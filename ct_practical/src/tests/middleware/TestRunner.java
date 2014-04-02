package tests.middleware;

import static org.junit.Assert.*;

import org.junit.Test;

import client.ProducerCollection;
import client.Runner;

public class TestRunner {

	
	@Test
	public void testRunner() throws InterruptedException {
		Runner r = new ProducerCollection();
		r.start();
		Thread.currentThread().sleep(10000);
		assertTrue(r.isRunning());
		r.stop();
		Thread.currentThread().sleep(50);
		assertTrue(!r.isRunning());
	}
}
