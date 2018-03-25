package edu.auburn.comp6360.application;

import java.util.Scanner;

import edu.auburn.comp6360.network.ClientThread;


public class FollowingVehicle extends Vehicle {
	
//	public enum VType {LEAD, FOLLOW};
//	public enum Lane {LEFT, RIGHT};
	public static final double CAR_WIDTH = 3;
	public static final double CAR_LENGTH = 5;	
	public double RANDOM_X = Math.random() * 300;
	public double INIT_Y = 5; // in the Left Lane
	public double RANDOM_V = 20 + Math.random() * 10;
	
	private int prev;
	private int post;
	private boolean isInRoadTrain;
	private boolean waitingAck;
//	private KeyboardListenerThread kt;
	
	public FollowingVehicle(int nodeId) {
		super(nodeId);
		this.setGPS(new GPS(RANDOM_X, INIT_Y));
		this.setWidth(CAR_WIDTH);
		this.setLength(CAR_LENGTH);
		this.setVelocity(RANDOM_V);
		this.setAcceleration();
		this.isInRoadTrain = false;
		this.waitingAck = false;
	}
	
	public FollowingVehicle(int nodeId, double init_x) {
		super(nodeId);
		this.setGPS(new GPS(init_x, INIT_Y));
		this.setWidth(CAR_WIDTH);
		this.setLength(CAR_LENGTH);
		this.setVelocity(RANDOM_V);
		this.setAcceleration();
		this.isInRoadTrain = false;
		this.waitingAck = false;
	}
	
	public FollowingVehicle(int nodeId, double init_x, double init_v) {
		super(nodeId);
		this.setGPS(new GPS(init_x, INIT_Y));
		this.setWidth(CAR_WIDTH);
		this.setLength(CAR_LENGTH);
		this.setVelocity(init_v);
		this.setAcceleration();
		this.isInRoadTrain = false;
		this.waitingAck = false;
	}		

	
	@Override
	public void startAll() {
		super.startAll();
		
		executor.execute(new KeyboardListenerThread());			
//		kt = new KeyboardListenerThread();
//		kt.start();		
	}
	
	@Override
	public void setAcceleration() {
		this.setAcceleration(this.getAcceleration());
	}	
	
	
//	public void setLane(Lane lane) {
//		if (lane == Lane.LEFT)
//			gps.setY(5);
//		else if (lane == Lane.RIGHT) 
//			gps.setY(0);
//		else
//			System.err.println("Invalid lane setting.");
//	}
	
	public void joinRoadTrain() {
		// TODO
		
		gps.setY(0); 	// merge to the right lane
		isInRoadTrain = true;
	}
	
	public void leaveRoadTrain() {
		// TODO
		
		gps.setY(5);	// switch to the left lane
		isInRoadTrain = false;
	}
	
	
	public class KeyboardListenerThread extends Thread {
		private Scanner sc;

		@Override
		public void run() {
			while (true) {
				sc = new Scanner(System.in);
				String request = sc.next();
				System.out.println("@@" + request);
				if ((!isInRoadTrain) && (request.equalsIgnoreCase("join"))) {
					System.out.println("Node " + nodeID +  " is sending JOIN request...");
					waitingAck = true;
					while (waitingAck) {
						initPacket("join", 1);	// send JOIN request to the leading truck
						try {
							Thread.sleep(750);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				} else if ((isInRoadTrain) && (request.equalsIgnoreCase("leave"))) {
					System.out.println("Node " + nodeID +  " is sending LEAVE request...");
					waitingAck = true;
					while (waitingAck) {
						initPacket("leave", 1);	// send LEAVE request to the leading truck
						try {
							Thread.sleep(250);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
			}			
		}
	}
	
	
}
