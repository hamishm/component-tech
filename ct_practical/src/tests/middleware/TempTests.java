package tests.middleware;

import static org.junit.Assert.*;

import java.util.ArrayList;

import methods.Handshake;

import org.junit.Test;

import client.ClientCollection;
import tests.dataTypes.TestUtils;

public class TempTests {

	@Test
	public void testHandshake() throws InterruptedException{
		ArrayList<SimpleConsumer> arr = new ArrayList<SimpleConsumer>();
		for(int i = 0; i < 100; i++){
			arr.add(new SimpleConsumer());
			arr.get(i).myThread.start();
		}
		Thread.currentThread().sleep(5000);
		for(int i = 0; i < 100; i++){
			arr.get(i).myThread.interrupt();
			assertTrue(!arr.get(i).failed);
		}
	}
	
	@Test
	public void testSeveral() throws InterruptedException{
		ClientCollection cc = new ClientCollection();
		cc.staggerStart();
		Thread.currentThread().sleep(10000);
		cc.stopAll();
	}
	
	class SimpleConsumer implements Runnable {

		Thread myThread = new Thread(this);
		boolean failed = false;
		boolean running = false;
		String s;
		
		@Override
		public void run() {
			running = true;
			while(running){
				try{
					s = Handshake.call("localhost", TestUtils.getLocation(), 10);
					if(s == null){
						failed = true;
					}
					Thread.currentThread().sleep(1000);
				} catch (InterruptedException e){
					running = false;
					Thread.currentThread().interrupt();
				}
			}
		}
		
	}
	
}
