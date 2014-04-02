package client;

import methods.Handshake;
import tests.dataTypes.TestUtils;

public abstract class Runner implements Runnable {

	private Thread myThread = new Thread(this);
	private boolean running = false;
	private int interval = 1000;
	
	@Override
	public void run() {
		running = true;
		while (running) {
			try {
				long start = System.currentTimeMillis();
				onTick();
				myThread.sleep(interval - (System.currentTimeMillis() - start));
			} catch (InterruptedException e) {
				running = false;
				myThread.interrupt();
				myThread = null;
			}
		}
	}

	public void start(){
		if(myThread == null){
			myThread = new Thread(this);
			myThread.start();
		}
	}

	public void stop() {
		if(running){
			myThread.interrupt();
		}
	}
	
	protected void onTick(){
		
	}

	public synchronized int getInterval() {
		return interval;
	}

	public synchronized void setInterval(int interval) {
		this.interval = interval;
	}

	public boolean isRunning() {
		return running;
	}

}
