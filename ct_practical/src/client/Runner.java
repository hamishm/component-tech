package client;


public abstract class Runner implements Runnable {
	private Thread myThread = null;
	private boolean running = false;
	private int interval = 100;
	
	@Override
	public synchronized void run() {
		while (running) {
			try {
				this.wait(interval);
				onTick();
				//System.out.println("run in loop: "+this+Thread.currentThread().getName());
			} catch (InterruptedException e) {
				running = false;
				break;
			}
		}
	}

	public synchronized void start(){
		if(myThread == null){
			myThread = new Thread(this);
		}
		running = true;
		myThread.start();
	}

	public synchronized void stop() {
		if(running){
			running = false;
			this.notify();
		}
	}
	
	abstract void onTick();

	public synchronized int getInterval() {
		return interval;
	}

	public synchronized void setInterval(int interval) {
		this.interval = interval;
	}

	public boolean isRunning() {
		return running;
	}
	
	public Thread getThread(){
		return myThread;
	}

}
