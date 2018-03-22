package edu.auburn.comp6360.application;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

//import edu.auburn.comp6360.network.Packet;


public class RbaCache {
	
	private int nodeID;
	private Map<Integer, CacheContent> packetHistory = new ConcurrentHashMap<Integer, CacheContent>();
	
	public RbaCache(int nodeID) {
		packetHistory = new ConcurrentHashMap<Integer, CacheContent>();
	}
	
	public int getPacketSeqNum(int source) {
		if (packetHistory.get(source) == null)
			return -1;
		return packetHistory.get(source).sequenceNumber;
	}
	
	public boolean updatePacketSeqNum(int source, int sn) {
		if (this.getPacketSeqNum(source) < sn) {
			packetHistory.put(source, new CacheContent(sn, 0));
			return true;
		} else if ((this.getPacketSeqNum(source) == sn) && (source != this.nodeID)) {
			int bn = this.getBroadcastNumber(source);
			if ((bn == 0) && (Math.random() <= 1./Math.pow(bn, 2))) {
				packetHistory.get(source).broadcastNumber++;
				return true;
			}
		}
		return false;
	}
	
	public int getBroadcastNumber(int source) {
		return packetHistory.get(source).broadcastNumber;
	}

	public void incrementBroadcastNumber(int source) {
		++packetHistory.get(source).broadcastNumber;
	}


	public class CacheContent {
		public int sequenceNumber;
		public int broadcastNumber;
		
		public CacheContent(int sn, int bn) {
			this.sequenceNumber = sn;
			this.broadcastNumber = bn;
		}
	}
	
}