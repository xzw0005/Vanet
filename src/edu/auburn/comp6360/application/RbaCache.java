package edu.auburn.comp6360.application;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

//import edu.auburn.comp6360.network.Packet;


public class RbaCache {

	private Map<Integer, CacheContent> packetHistory = new ConcurrentHashMap<Integer, CacheContent>();
	
	public RbaCache() {
		packetHistory = new ConcurrentHashMap<Integer, CacheContent>();
	}
	
	public int getPacketSeqNum(int source) {
		if (packetHistory.get(source) == null)
			return -1;
		return packetHistory.get(source).sequenceNumber;
	}
	
	public boolean updatePacketSeqNum(int source, int sn) {
		if ((packetHistory.get(source) == null) || (packetHistory.get(source).sequenceNumber < sn))  {
			packetHistory.put(source, new CacheContent(sn, 0));
			return true;
		} 
		return false;
	}
	
	public class CacheContent {
		public int sequenceNumber;
		public int broadcastNumber;
		
		public CacheContent(int sn, int bn) {
			this.sequenceNumber = sn;
			this.broadcastNumber = bn;
		}
		
		public void incrementBroadcastNumber() {
			++broadcastNumber;
		}
		
		public boolean ifForward() {
			if ((broadcastNumber >= 1) && (Math.random() <= 1.0/Math.pow(broadcastNumber, 2)))
				return false;
			return true;
		}
	}
	
}