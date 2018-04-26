package edu.auburn.comp6360.application;

//import java.util.HashMap;
//import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RebroadcastCache {
		
//	private Map<Integer, Map<String, CacheContent>> cacheMap = new HashMap<Integer, Map<String, CacheContent>>();
	private ConcurrentHashMap<Integer, ConcurrentHashMap<String, CacheContent>> cacheMap = new ConcurrentHashMap<Integer, ConcurrentHashMap<String, CacheContent>>();
	
	public RebroadcastCache() {
		cacheMap = new ConcurrentHashMap<Integer, ConcurrentHashMap<String, CacheContent>>();
	}
	
	public int getPacketSeqNum(int source, String packetType) {
		if ((cacheMap.get(source) == null) || (cacheMap.get(source).get(packetType) == null))
			return -1;
		return cacheMap.get(source).get(packetType).sequenceNumber;
	}
	
	public int getPacketSeqNum(int source) {
		return getPacketSeqNum(source, "normal");
	}
	
	public boolean updatePacketSeqNum(int source, String type, int sn, int nid) {
		if (this.getPacketSeqNum(source, type) < sn) {
			CacheContent content = new CacheContent(sn, 0);
			ConcurrentHashMap<String, CacheContent> tempMap = new ConcurrentHashMap<String, CacheContent>();
			tempMap.put(type, content);
			cacheMap.put(source, tempMap);
			return true;
		} else if ((this.getPacketSeqNum(source, type) == sn)) {
			if (source == nid)
				cacheMap.get(source).get(type).broadcastNumber = 1;
			else {				
				int bn = this.getBroadcastNumber(source, type);
				if ((bn == 0) && (Math.random() <= 1./Math.pow(bn, 2))) {
					incrementBroadcastNumber(source, type);
					return true;
				}
			}
		}
		return false;
	}
	
	public boolean updatePacketSeqNum(int source, int sn, int nid) {
		return updatePacketSeqNum(source, "normal", sn, nid);

	}
	
	public int getBroadcastNumber(int source, String type) {
		return cacheMap.get(source).get(type).broadcastNumber;
	}

	public void incrementBroadcastNumber(int source, String type) {
		++cacheMap.get(source).get(type).broadcastNumber;
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