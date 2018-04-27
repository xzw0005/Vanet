package edu.auburn.comp6360.application;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Scanner;

import edu.auburn.comp6360.utilities.VehicleHandler;


public class FollowingVehicle extends Vehicle {
	
//	public enum VType {LEAD, FOLLOW};
//	public enum Lane {LEFT, RIGHT};
	public static final double CAR_WIDTH = 3;
	public static final double CAR_LENGTH = 5;	
	public double INIT_Y = 5; // in the Left Lane
	public double RANDOM_X = Math.random() * 300;
	public double RANDOM_V = 25 + Math.random() * 10;
	

	private KeyboardListenerThread kt;
	
	public FollowingVehicle(int nodeId) {
		super(nodeId);
		double randomX = Math.random() * 300;
		this.setGPS(new GPS(randomX, INIT_Y));
		this.setWidth(CAR_WIDTH);
		this.setLength(CAR_LENGTH);
		this.setVelocity(RANDOM_V);
		this.setAcceleration();
	}
	
	public FollowingVehicle(int nodeId, double init_x) {
//		FollowingVehicle(nodeID, init_x, RANDOM_V);		
		super(nodeId);
		this.setGPS(new GPS(init_x, INIT_Y));
		this.setWidth(CAR_WIDTH);
		this.setLength(CAR_LENGTH);
		this.setVelocity(RANDOM_V);
		this.setAcceleration();	
	}

	public FollowingVehicle(int nodeId, double init_x, double init_v) {
		super(nodeId);
		this.setGPS(new GPS(init_x, INIT_Y));
		this.setWidth(CAR_WIDTH);
		this.setLength(CAR_LENGTH);
		this.setVelocity(init_v);
		this.setAcceleration();		
	}		

	
//	@Override
	public void startAll() {
//		executor.execute(new KeyboardListenerThread());			
		kt = new KeyboardListenerThread();
		kt.setPriority(Thread.MAX_PRIORITY);
		if (this.SET_BROADCAST == true) {
			brcst_thread = new BroadcastHelloThread();
			brcst_thread.setPriority(Thread.MAX_PRIORITY);
		} else {
			hello_thread = new P2PHelloThread();
			hello_thread.setPriority(Thread.MAX_PRIORITY);
		}	
		send_thread = new SendVehInfoThread();
		recv_thread = new ReceiveThread(serverPort);		
		config_thread = new ConfigThread();
		
		send_thread.setPriority(Thread.MAX_PRIORITY);
		recv_thread.setPriority(Thread.MAX_PRIORITY);
		config_thread.setPriority(Thread.MAX_PRIORITY);
		
		config_thread.start();
		if (this.SET_BROADCAST == true) {
			brcst_thread.start();
		} else {
			hello_thread.start();
		}
		send_thread.start();
		recv_thread.start();
		kt.start();			
	}
	
	@Override
	public void setAcceleration() {
//		this.setAcceleration(Math.random() * 2 - 1);
		this.setAcceleration(this.getAcceleration());
	}	
	
	
	@Override	
	public void processAckJoin(int source, int toFollow) {
		if (waitJoinReply > 0) {
			waitJoinReply = 0;
			this.front = toFollow;
		}
	}
	
	/*
	 * The notifyLeave must be sent from the leading vehicle
	 * to notify this vehicle to change the vehicle ahead and catch up
	 */
	@Override
	public void processAckLeave(int source, int toFollow) {
		if (front > 0) {
//			int toDelete = front;
			front = toFollow;
//			sendSpecificPacket("ackLeave", source, toDelete);
		}
	}
		
	public class KeyboardListenerThread extends Thread {
		private Scanner sc;

		@Override
		public void run() {
			int dest = 1;
			while (true) {
				System.out.println("Keyboard Thread Listening...");
				sc = new Scanner(System.in);
				String request = sc.next();
//				int counter = 0;
				if ((!VehicleHandler.isInRoadTrain(gps)) && (request.equalsIgnoreCase("join"))) {
					System.out.println("Node " + nodeID +  " is sending JOIN request...");
					waitJoinReply = 1;
//					while (waitingAckJoin && counter < 3) {
						// send JOIN request to the leading truck
//						Packet joinRequest = initPacket("join", dest, 0);
//						sendToLead(joinRequest);
						sendSpecificPacket("join", dest, 0);
//						counter++;
						
//					}
				} else if ((VehicleHandler.isInRoadTrain(gps)) && (request.equalsIgnoreCase("leave"))) {
					System.out.println("Node " + nodeID +  " is sending LEAVE message...");
//					waitingAckLeave = true;
//					while (counter < 3) {
						// send LEAVE request to the leading truck
						sendSpecificPacket("leave", dest, front);
						if (front > 0) {
							front = 0;
							frontVinfo = null;
							gps.setY(5);
							setVelocity(25 + Math.random() * 10);
							setAcceleration(0);
						}

//					}
				}
			}			
		}
	}
	
	@Override
	public void writeCalculationResults() {
		long running_time = System.currentTimeMillis() - this.initialTime;
		if (running_time >= 2.5 * 60 * 1000) {
			String fname = "result_" + this.nodeID + ".txt";
			try {
				PrintWriter pw = new PrintWriter(fname);
				pw.println("Running Time: " + running_time);
				pw.println("Total Number of Packets should be received by this vehicle: " + this.numPacketReceived);
				pw.println("Number of lost packets: " + this.numPacketLost);
				pw.println("Average latency = " + this.avgLatency + "\t calculated upon " + this.numLatencyRecord + "packets.");
				pw.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			
		}
	}
	
	
}
