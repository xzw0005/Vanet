package edu.auburn.comp6360.utilities;

import java.util.SortedMap;

import edu.auburn.comp6360.application.GPS;
import edu.auburn.comp6360.application.Node;
import edu.auburn.comp6360.network.PacketHeader;
import edu.auburn.comp6360.network.VehicleInfo;

public class VehicleHandler {
	
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
	
	
	public static Node updateNeighborsFromPacket(Node selfNode, int otherNodeID, GPS otherGPS) {
//		if (!(selfNode.getNodeID() == otherNodeID)) {
		GPS selfGPS = selfNode.getGPS();
		if (inTransmissionRange(selfGPS, otherGPS)) 
			selfNode.addLink(otherNodeID);
		else
			selfNode.removeLink(otherNodeID);			
//		}
		return selfNode;
	}
	
//	public static void updateInfoFromPacket(Node selfNode, SortedMap<Integer, Node> nodesMap, PacketHeader header, VehicleInfo vInfo) {
//		
//	}
	
	
	/*
	 * Only add neighbor according to the config file, if the link has not been there already.
	 * Won't remove neighbor according to the config file.
	 */
	public static Node updateNeighborsFromFile(Node selfNode, SortedMap<Integer, Node> nodesMap) {
		for (SortedMap.Entry<Integer, Node> entry: nodesMap.entrySet()) {	
			int i = entry.getKey();
			if ((i != selfNode.getNodeID()) && (!selfNode.getLinks().contains(i))) {
				if (inTransmissionRange(entry.getValue(), selfNode))
					selfNode.addLink(i);
			}
		}
		
//		for (Integer i : nodesMap.keySet()) {
//			if ((i != selfNode.getNodeID()) && (!selfNode.getLinks().contains(i)) ) {
//				Node candidate = nodesMap.get(i);
//				if (inTransmissionRange(selfNode, candidate))
//					selfNode.addLink(candidate.getNodeID());
//			}
//		}
		return selfNode;
	}
	
}
