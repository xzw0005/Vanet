package edu.auburn.comp6360.application;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import edu.auburn.comp6360.network.Packet;


public class VehicleCache {

	private Map<Integer, Integer> packetHistory = new ConcurrentHashMap<Integer, Integer>();
		
	public VehicleCache() {
		packetHistory = new ConcurrentHashMap<Integer, Integer>();
	}
	
	public int getPacketSeqNum(int source) {
		if (packetHistory.get(source) == null)
			return -1;
		return packetHistory.get(source);
	}
	
	public boolean updatePacketSeqNum(int source, int sn) {
		if ((packetHistory.get(source) == null) || (packetHistory.get(source) < sn))  {
			packetHistory.put(source, sn);
			return true;
		} 
		return false;
	}
	
	
	
}
