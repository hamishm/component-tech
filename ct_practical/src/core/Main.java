package core;

import java.util.ArrayList;

import producer.Producer;

public class Main {
	ArrayList<Producer> producers = new ArrayList<Producer>();	
	
	public static void main(String[] args) throws InterruptedException {
		Producer p = new Producer();
		Thread thread = new Thread(p);
		System.out.println("Starting Producer thread");
		thread.start();
		System.out.println("Main thread sleeping");
		Thread.currentThread().sleep(5000);
		System.out.println("Interupting producer thread");
		thread.interrupt();
		System.out.println("End");
	}

}
