package client;

import java.util.ArrayList;
import java.util.Iterator;

public class ProducerCollection implements Iterable<Producer>{

	private ArrayList<Producer> collection = new ArrayList<Producer>();
	
	private void init(){
		for(int i = 0; i < 10; i++){
			addProducer();
			addMovingProducer();
		}
	}
	
	/**
	 * Starts up all the producers at 
	 * slightly staggered, random intervals
	 */
	public void staggerStart(){
		if(collection.size()==0)
			init();
		for(Producer p: collection){
			p.start();
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
	
	@Override
	public Iterator<Producer> iterator() {
		return collection.iterator();
	}

	public void stopAll() {
		for(Producer p: collection){
			p.stop();
		}
	}
	
}
