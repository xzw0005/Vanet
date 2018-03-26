package edu.auburn.comp6360.application;

import java.util.Scanner;

import edu.auburn.comp6360.network.Header;
import edu.auburn.comp6360.network.Packet;
import edu.auburn.comp6360.network.VehicleInfo;
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
	private boolean waitingAckLeave;
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
		this.waitingAckLeave = false;
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
		this.waitingAckLeave = false;		
	}

	public FollowingVehicle(int nodeId, double init_x, double init_v) {
		super(nodeId);
		this.setGPS(new GPS(init_x, INIT_Y));
		this.setWidth(CAR_WIDTH);
		this.setLength(CAR_LENGTH);
		this.setVelocity(init_v);
		this.setAcceleration();		
		this.waitingAckJoin = false;
		this.waitingAckLeave = false;	
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
	
	
	public void receivePacket(Packet packetReceived) {
		Header header = packetReceived.getHeader();
		int source = header.getSource();
		int sn = header.getSeqNum();
		int prevHop = header.getPrevHop();
		String packetType = header.getPacketType();
		if (packetType.equals("normal"))  {
			if (cache.updatePacketSeqNum(source, packetType, sn, getNodeID())) {
				VehicleInfo vInfo = packetReceived.getVehicleInfo();
				selfNode = VehicleHandler.updateNeighborsFromPacket(selfNode, source, vInfo.getGPS());			
				sendPacket(packetReceived, source, sn, prevHop);
			}
		} else {		// in the case that of not normal packets
			if ( (header.getDest() != this.nodeID) && (cache.updatePacketSeqNum(source, packetType, sn, getNodeID())) )
				sendPacket(packetReceived, source, sn, prevHop);
		}
	}
	
	
	
	
	
	public void joinRoadTrain() {
		// TODO
		
		gps.setY(0); 	// merge to the right lane, & join the road train
	}
	
	public void leaveRoadTrain() {
		// TODO
		
		gps.setY(5);	// switch to the left lane, & leave the road train
	}
	
	
	public void processAckJoin(int source, int ahead) {
		if (waitingAckJoin) {
			waitingAckJoin = false;
			this.ahead = ahead;
			initPacket("ackJoin", 1, ahead);
			joinRoadTrain();
		}
	}
	
	@Override
	public void processAckLeave(int source, int ahead) {
		if (waitingAckLeave) {
			waitingAckLeave = false;
			ahead = 0;
			aheadInfo = null;
			initPacket("ackLeave", 1, 0);
			leaveRoadTrain();	
		}
	}
	
	/*
	 * The notifyLeave must be sent from the leading vehicle
	 * to notify this vehicle to change the vehicle ahead and catch up
	 */
	public void processNotifyLeave(int source, int newAhead) {
		if (VehicleHandler.isInRoadTrain(gps)) {
			ahead = newAhead;
			// CATCHUP PREV
		}
	}
	
	public class KeyboardListenerThread extends Thread {
		private Scanner sc;

		@Override
		public void run() {
			while (true) {
				System.out.println("Keyboard Thread Listening...");
				sc = new Scanner(System.in);
				String request = sc.next();
				System.out.println("@@" + request);

				if ((!VehicleHandler.isInRoadTrain(gps)) && (request.equalsIgnoreCase("join"))) {
					System.out.println("Node " + nodeID +  " is sending JOIN request...");
					waitingAckJoin = true;
					while (waitingAckJoin) {
						initPacket("join", 1, 0);	// send JOIN request to the leading truck
						try {
							Thread.sleep(750);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						
					}
				} else if ((VehicleHandler.isInRoadTrain(gps)) && (request.equalsIgnoreCase("leave"))) {
					System.out.println("Node " + nodeID +  " is sending LEAVE request...");
					waitingAckLeave = true;
					while (waitingAckLeave) {
						initPacket("leave", 1, ahead);	// send LEAVE request to the leading truck
						try {
							Thread.sleep(750);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
			}			
		}
	}
	
	
}
