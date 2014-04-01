package client;

import java.util.ArrayList;
import java.util.Iterator;

public class ClientCollection implements Iterable<Client>{

	private ArrayList<Client> collection = new ArrayList<Client>();
	
	private void init(){
		for(int i = 0; i < 1; i++){
			addProducer();
			addMovingProducer();
			addConsumer();
		}
	}
	
	/**
	 * Starts up all the producers at 
	 * slightly staggered, random intervals
	 */
	public void staggerStart(){
		if(collection.size()==0)
			init();
		for(Client c: collection){
			c.start();
			try {
				Thread.currentThread().sleep((long) (Math.random()*1000));
			} catch (InterruptedException e) {
				System.err.println("ProducerCollection interrupted");
			}
		}
	}
	
	public void addProducer(){
		collection.add(new Producer());
	}
	
	public void addMovingProducer(){
		collection.add(new MovingProducer());
	}
	
	public void addConsumer(){
		collection.add(new Consumer());
	}
	
	@Override
	public Iterator<Client> iterator() {
		return collection.iterator();
	}

	public void stopAll() {
		for(Client c: collection){
			c.stop();
		}
	}
	
}
