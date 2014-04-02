package tests.middleware;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import methods.Handshake;

import org.junit.Test;

import tests.dataTypes.TestUtils;
import client.Consumer;
import client.ProducerCollection;
import client.Runner;

public class TempTests {

	
	/**
	 * Starts up 20 consumers 
	 * @throws InterruptedException
	 */
	@Test
	public void testKillConsumers() throws InterruptedException{
		System.out.println("Start test memory");
		ArrayList<Consumer> consumers = this.spinUpConsumers(20);
		Thread.currentThread().sleep(1000); //time for them to register
		ArrayList<ProducerCollection> producers = this.spinUpProducers(10000);
		
		//let 10 seconds of activity happen
		Thread.currentThread().sleep(10000);
		
		for(Runner p : consumers){
			p.stop();
		}
		for(Runner p : producers){
			p.stop();
		}
		
		System.out.println("Done test memory");
	}
	
	private ArrayList<ProducerCollection> spinUpProducers(int num){
		ArrayList<ProducerCollection> group = new ArrayList<ProducerCollection>();
		for(int i = 0; i < num/10; i++){
			group.add(new ProducerCollection(num/10,0));
		}
		for(ProducerCollection cc : group){
			cc.start();
		}
		return group;
		
	}
	
	private ArrayList<Consumer> spinUpConsumers(int num) throws InterruptedException{
		ArrayList<Consumer> group = new ArrayList<Consumer>();
		for(int i = 0; i < num; i++){
			group.add(new Consumer());
		}
		for(Consumer c : group){
			c.setInterval(500);
			c.start();
			Thread.currentThread().sleep((int)Math.random()*100);
		}
		return group;
		
	}
	
}
