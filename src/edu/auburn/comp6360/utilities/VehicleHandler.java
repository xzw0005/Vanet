package edu.auburn.comp6360.utilities;

//import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
//import java.util.HashMap;
//import java.util.Map;
//import java.util.SortedMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.ConcurrentSkipListSet;

import edu.auburn.comp6360.application.GPS;
import edu.auburn.comp6360.application.Node;
//import edu.auburn.comp6360.network.PacketHeader;

public class VehicleHandler {

	public static final String[] PACKET_TYPES = {"normal", "join", "leave", "ackJoin", "ackLeave", "notify", "ok", "update"};

	public static ConcurrentHashMap<String, Integer> initializeSequenceNumbers() {
		ConcurrentHashMap<String, Integer> snMap= new ConcurrentHashMap<String, Integer>();
		for (String type : PACKET_TYPES) {
			snMap.put(type, 0);
		}
		return snMap;
	}
	
	public static GPS computeGPS(GPS gps, double velocity, double acceleration, double dt) {
		double x = gps.getX();
		double distance = velocity * dt + .5 * acceleration * Math.pow(dt, 2);
		return new GPS(x + distance, gps.getY());
	}
	
	public static double computeVelocity(double velocity, double acceleration, double dt) {
		return velocity + acceleration * dt;
	}
	
	public static boolean ifPacketLoss(double distance) {
		double jitter = (95 + Math.random() * 10) / 100.;
		double possibility = 90.158730 - (0.00873 * distance * distance) + (0.571428 * distance);
	    double lossRate = jitter * possibility / 100.; 
	    double toss = lossRate * ((50 + Math.random() * 50) / 100.);
	    if (toss < 0.5)
	    	return true;
	    return false;
	}
	
	public static boolean ifPacketLoss(GPS gps1, GPS gps2) {
		return ifPacketLoss(computeDistance(gps1, gps2));
	}

	public static boolean ifPacketLoss(Node n1, Node n2) {
		return ifPacketLoss(computeDistance(n1, n2));
	}
	
	public static double computeDistance(double x1, double y1, double x2, double y2) {
		double dxSq = Math.pow(x1 - x2, 2);
		double dySq = Math.pow(y1 - y2, 2);
		return Math.sqrt(dxSq + dySq);
	}
	
	public static double computeDistance(Node n1, Node n2) {
		return computeDistance(n1.getX(), n1.getY(), n2.getX(), n2.getY());
	}
	
	public static double computeDistance(GPS gps1, GPS gps2) {
		return computeDistance(gps1.getX(), gps1.getY(), gps2.getX(), gps2.getY());
	}
	
	public static boolean inTransmissionRange(double x1, double y1, double x2, double y2) {
		return computeDistance(x1, y1, x2, y2) <= 100;
	}
	
	public static boolean inTransmissionRange(Node n1, Node n2) {
		return computeDistance(n1, n2) <= 100;
	}
	
	public static boolean inTransmissionRange(GPS gps1, GPS gps2) {
		return computeDistance(gps1, gps2) <= 100;
	}	
	
//	public static ConcurrentSkipListSet<Integer> updateNeighborsFromPacket(int selfNodeID, GPS selfGPS, ConcurrentSkipListSet<Integer> neighborSet, int otherNodeID, GPS otherGPS) {
//		if (selfNodeID != otherNodeID) {
//			if (inTransmissionRange(selfGPS, otherGPS)) 
//				neighborSet.add(otherNodeID);
//			else
//				neighborSet.remove(Integer.valueOf(otherNodeID));
//		}
//		return neighborSet;
//	}
//	
//	/*
//	 * Only add neighbor according to the config file, if the link has not been there already.
//	 */
//	public static Node updateNeighborsFromFile(Node selfNode, ConcurrentSkipListMap<Integer, Node> nodesMap) {
//		for (ConcurrentSkipListMap.Entry<Integer, Node> entry: nodesMap.entrySet()) {
//			int i = entry.getKey();
//			if (i != selfNode.getNodeID()) {
//				if (inTransmissionRange(entry.getValue(), selfNode))
//					selfNode.addLink(i);
//				else
//					selfNode.removeLink(i);
//			}
//		}
//		return selfNode;
//	}
	
	public static boolean isInRoadTrain(GPS gps) {
		return gps.getY() == 0;
	}	
	
}
