package client;

import java.util.ArrayList;
import java.util.Iterator;

public class ProducerCollection extends Runner {

	private ArrayList<Producer> collection = new ArrayList<Producer>();
	
	public ProducerCollection(int numStill, int numMoving){
		init(numStill, numMoving);
	}
	
	public ProducerCollection(){
		init(100,100);
	}
	
	private void init(int numProducers, int numMovingProducers){
		for(int i = 0; i < numProducers; i++){
			addProducer();
		}
		for(int i = 0; i < numMovingProducers; i++){
			addMovingProducer();
		}
	}
	
	protected void onTick(){
		for(Client c: collection){
			c.onTick();
		}
	}
	
	public synchronized void addProducer(){
		collection.add(new Producer());
	}
	
	public synchronized void addMovingProducer(){
		collection.add(new MovingProducer());
	}
	
	
	public synchronized void removeAllProducers(){
		for(int i = 0; i < collection.size(); i++){
			if(collection.get(i) instanceof Producer){
				collection.remove(i);
				i--;
			}
		}
		for(int i = 0; i < collection.size(); i++){
			assert !(collection.get(i) instanceof Producer);
		}
	}
}
