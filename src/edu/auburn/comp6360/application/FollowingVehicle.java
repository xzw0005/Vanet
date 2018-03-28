package edu.auburn.comp6360.application;

import java.util.Scanner;

//import edu.auburn.comp6360.network.Header;
//import edu.auburn.comp6360.network.Packet;
//import edu.auburn.comp6360.network.VehicleInfo;
import edu.auburn.comp6360.utilities.VehicleHandler;


public class FollowingVehicle extends Vehicle {
	
//	public enum VType {LEAD, FOLLOW};
//	public enum Lane {LEFT, RIGHT};
	public static final double CAR_WIDTH = 3;
	public static final double CAR_LENGTH = 5;	
	public double INIT_Y = 5; // in the Left Lane
	public double RANDOM_X = Math.random() * 300;
	public double RANDOM_V = 20 + Math.random() * 10;
	
	private boolean waitingAckJoin;
//	private boolean waitingAckLeave;
	private KeyboardListenerThread kt;
	
	public FollowingVehicle(int nodeId) {
		super(nodeId);
		double randomX = Math.random() * 300;
		this.setGPS(new GPS(randomX, INIT_Y));
		this.setWidth(CAR_WIDTH);
		this.setLength(CAR_LENGTH);
		this.setVelocity(RANDOM_V);
		this.setAcceleration();
		
		this.waitingAckJoin = false;
//		this.waitingAckLeave = false;
	}
	
	public FollowingVehicle(int nodeId, double init_x) {
//		FollowingVehicle(nodeID, init_x, RANDOM_V);		
		super(nodeId);
		this.setGPS(new GPS(init_x, INIT_Y));
		this.setWidth(CAR_WIDTH);
		this.setLength(CAR_LENGTH);
		this.setVelocity(RANDOM_V);
		this.setAcceleration();
		
		this.waitingAckJoin = false;
//		this.waitingAckLeave = false;		
	}

	public FollowingVehicle(int nodeId, double init_x, double init_v) {
		super(nodeId);
		this.setGPS(new GPS(init_x, INIT_Y));
		this.setWidth(CAR_WIDTH);
		this.setLength(CAR_LENGTH);
		this.setVelocity(init_v);
		this.setAcceleration();		
		this.waitingAckJoin = false;
//		this.waitingAckLeave = false;	
	}		

	
//	@Override
	public void startAll() {
//		super.startAll();
		
//		executor.execute(new KeyboardListenerThread());			
		kt = new KeyboardListenerThread();
		
		bt = new BroadcastThread();
		ct = new ConfigThread();
		st = new ServerThread(SERVER_PORT+nodeID);

		kt.start();		
		bt.start();
		ct.start();
		st.run();
		
	}
	
	@Override
	public void setAcceleration() {
		this.setAcceleration(this.getAcceleration());
	}	
	
	
//	public void receivePacket(Packet packetReceived) {
//		Header header = packetReceived.getHeader();
//		int source = header.getSource();
//		int sn = header.getSeqNum();
//		int prevHop = header.getPrevHop();
//		String packetType = header.getPacketType();
//		if (packetType.equals("normal"))  {
//			if (cache.updatePacketSeqNum(source, packetType, sn, getNodeID())) {
//				VehicleInfo vInfo = packetReceived.getVehicleInfo();
//				selfNode = VehicleHandler.updateNeighborsFromPacket(selfNode, source, vInfo.getGPS());			
//				sendPacket(packetReceived, source, sn, prevHop);
//			}
//		} else {		// in the case that of not normal packets
//			if ( (header.getDest() != this.nodeID) && (cache.updatePacketSeqNum(source, packetType, sn, getNodeID())) )
//				sendPacket(packetReceived, source, sn, prevHop);
//		}
//	}
	
	public void processAckJoin(int source, int toFollow) {
		if (waitingAckJoin) {
			waitingAckJoin = false;
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
				int counter = 0;
				if ((!VehicleHandler.isInRoadTrain(gps)) && (request.equalsIgnoreCase("join"))) {
					System.out.println("Node " + nodeID +  " is sending JOIN request...");
					waitingAckJoin = true;
//					while (waitingAckJoin && counter < 3) {
						// send JOIN request to the leading truck
//						Packet joinRequest = initPacket("join", dest, 0);
//						sendToLead(joinRequest);
						sendSpecificPacket("join", dest, 0);
						counter++;
						
//					}
				} else if ((VehicleHandler.isInRoadTrain(gps)) && (request.equalsIgnoreCase("leave"))) {
					System.out.println("Node " + nodeID +  " is sending LEAVE message...");
//					waitingAckLeave = true;
//					while (counter < 3) {
						// send LEAVE request to the leading truck
//						Packet leaveRequest = initPacket("leave", dest, ahead);	
//						sendToLead(leaveRequest);
						if (front > 0) {
							front = 0;
							frontVinfo = null;
							gps.setY(5);
							setAcceleration(0);
						}
						sendSpecificPacket("leave", dest, 0);

//					}
				}
			}			
		}
	}
	
	
	
}
