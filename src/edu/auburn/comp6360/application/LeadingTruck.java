package edu.auburn.comp6360.application;

import java.util.LinkedList;

//import edu.auburn.comp6360.network.Header;
//import edu.auburn.comp6360.network.Packet;
//import edu.auburn.comp6360.network.VehicleInfo;
//import edu.auburn.comp6360.utilities.VehicleHandler;

public class LeadingTruck extends Vehicle {	

	public static final double TRUCK_WIDTH = 4;
	public static final double TRUCK_LENGTH = 10;
	public static double INIT_X = 10;
	public static double INIT_Y = 0; // in the Right Lane
	public static double INIT_V = 30;
	
	private LinkedList<Integer> roadTrainList;
	
	public LeadingTruck(int nodeID) {
		super(nodeID);
		this.setGPS(new GPS(INIT_X, INIT_Y));
		this.setWidth(TRUCK_WIDTH);
		this.setLength(TRUCK_LENGTH);
		this.setVelocity(INIT_V);
		
		roadTrainList = new LinkedList<Integer>();
		roadTrainList.add(nodeID);
	}

	
  /**
   * Obtain the acceleration for the Leading Vehicle (every 10 ms)
   * 
   * @param
   * @return a random number between -1 and 1
   */	
	@Override
	public void setAcceleration() {
		this.setAcceleration(Math.random() * 2 - 1);
	}
	
	public void startAll() {
//		super.startAll();
//		RoadTrainHandlerThread train = new RoadTrainHandlerThread();
//		train.start();
		
		bt = new BroadcastThread();
		ct = new ConfigThread();
		st = new ServerThread(SERVER_PORT+nodeID);

		bt.start();
		ct.start();
		st.run();
		
	}
	
	
//	public void receivePacket(Packet packetReceived) {
//		Header header = packetReceived.getHeader();
//		int prevHop = header.getPrevHop();
//		
//		int source = header.getSource();
//		int sn = header.getSeqNum();
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
	
	/*
	 * Better be implemented as binary search
	 */
	public int findToFollow(int toJoin) {
		int trainLen = roadTrainList.size();
		if (trainLen == 1)
			return nodeID;
		double toJoinX = nodesMap.get(toJoin).getX();
		int toFollow = roadTrainList.getLast();
		for (int i = trainLen - 2; i >= 0; i--) {
			if (nodesMap.get(toFollow).getX() > toJoinX)
				return toFollow;
			else
				toFollow = roadTrainList.get(i);				
		}
		return toFollow;
	}
	
	@Override
	public void processJoinRequest(int source) {
		if (!roadTrainList.contains(source)) {
//			int lastInTrain = roadTrainList.getLast();		// Tell the source (the one who wanna join) to follow the last in road train
			int toFollow = findToFollow(source);
			if (toFollow == roadTrainList.getLast()) 
				sendSpecificPacket("ackJoin", source, toFollow);
			else {
				int index = roadTrainList.indexOf(Integer.valueOf(toFollow));

				System.out.println("@@" + index + " leaving... Road train length = " + roadTrainList.size());
				for (int i = 0; i < roadTrainList.size(); i++)
					System.out.println(roadTrainList.get(i));
				
				int toNotify = roadTrainList.get(index + 1);
				sendSpecificPacket("notify", toNotify, source);
			}
		} else {
			System.out.println("Already in Road Train, Why Bother me????????????????");
//			int index = roadTrainList.indexOf(Integer.valueOf(source));
//			int toFollow = roadTrainList.get(index - 1);
//			sendSpecificPacket("ackJoin", source, toFollow);			
		}
	}
	
	@Override
	public void processAckJoin(int source, int toFollow) {
//		roadTrainList.addLast(source);
		int index = roadTrainList.indexOf(Integer.valueOf(toFollow));
//		// Inform the notified node update its ahead vehicle as the one joined road train recently
		if (index + 1 < roadTrainList.size()) {		// Make sure toFollow is not the last in road train list
			int toNotify = roadTrainList.get(index + 1);
			sendSpecificPacket("update", toNotify, source);	
		}
		if (!roadTrainList.contains(Integer.valueOf(source)))
			roadTrainList.add(index + 1, source);			
			
	}
		
	/*
	 * Send ACKJOIN to the one want to join, tell it which node to follow
	 */
	@Override
	public void processOK(int source, int toJoin) {
		int index = roadTrainList.indexOf(Integer.valueOf(source));
		int toFollow = roadTrainList.get(index - 1);
		sendSpecificPacket("ackJoin", toJoin, toFollow);	
	}
	
	@Override
	public void processLeaveRequest(int source) {
		if (roadTrainList.contains(source)) {
			int index = roadTrainList.indexOf(Integer.valueOf(source));
			if (index + 1 < roadTrainList.size()) {				
				int toNotify = roadTrainList.get(index + 1);	// The one behind i should be notified
				int toFollow = roadTrainList.get(index - 1);	// Tell it to follow the one previously before i
				sendSpecificPacket("ackLeave", toNotify, toFollow);				
			} 
			roadTrainList.remove(index);	// Update roadTrainList
//			else 	// in the case that the leaving vehicle is the last one in the road train
//				roadTrainList.removeLast();						// Update roadTrainList
		}
	}
	
//	@Override
//	public void processAckLeave(int source, int toDelete) {
////		int leftIndex = roadTrainList.indexOf(source) - 1;
////		if (roadTrainList.get(leftIndex) != toDelete)
////			System.err.println("Something is wrong with the road train list!");
//		if (roadTrainList.contains(toDelete))
//			roadTrainList.remove(Integer.valueOf(toDelete));				// Update roadTrainList
//	}
	
}
