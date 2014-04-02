package client;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Contains 100 producers and consumers.
 * This is so we don't overload the system 
 * with too many threads during testing
 *
 */
public class ClientCollection extends Runner implements Iterable<Client>{

	private ArrayList<Client> collection = new ArrayList<Client>();
	
	private void init(){
		for(int i = 0; i < 100; i++){
			addProducer();
			//addMovingProducer();
			addConsumer();
		}
	}
	
	protected void onTick(){
		for(Client c: collection){
			c.onTick();
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

}
