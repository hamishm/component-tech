package tests.middleware;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import methods.Handshake;

import org.junit.Test;

import tests.dataTypes.TestUtils;
import client.Consumer;
import client.ProducerCollection;

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
		ArrayList<ProducerCollection> group = new ArrayList<ProducerCollection>();
		for(int i = 0; i < 20; i++){
			group.add(new ProducerCollection());
		}
		for(ProducerCollection cc : group){
			cc.start();
			Thread.currentThread().sleep((long) (Math.random()*100));
		}
		Thread.currentThread().sleep(10000);
		for(ProducerCollection cc : group){
			cc.stop();
		}
	}
	
	@Test
	public void testKillConsumers() throws InterruptedException{
		System.out.println("Start test memory");
		ArrayList<ProducerCollection> group = new ArrayList<ProducerCollection>();
		ArrayList<Consumer> consumers = new ArrayList<Consumer>();
		//1000 prod
		for(int i = 0; i < 10; i++){
			group.add(new ProducerCollection(50,50));
		}
		System.out.println("Starting 1000 producers");
		for(ProducerCollection cc : group){
			cc.start();
		}
		System.out.println("Starting Starting 10 cycles of removing and adding consumers:");
		for(int i = 0; i < 10; i++){
			System.out.println("Cycle: "+i);
			for( int c = 0; c< 100; c++){
				consumers.add(new Consumer());
				consumers.get(c).start();
			}
			Thread.currentThread().sleep(5000);
			for( int c = 0; c< 100; c++){
				consumers.get(c).stop();
			}
			consumers.clear();
		}
		System.out.println("Finished Cycling, waiting 5 seconds:");
		Thread.currentThread().sleep(5000);
		for(ProducerCollection cc : group){
			cc.stop();
		}
		System.out.println("Done test memory");
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
