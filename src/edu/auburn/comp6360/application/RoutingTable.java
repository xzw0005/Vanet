package edu.auburn.comp6360.application;

import java.util.concurrent.ConcurrentSkipListMap;

public class RoutingTable {
	
	private ConcurrentSkipListMap<Integer, RtEntry> routingMap;
	
	public RoutingTable() {
		routingMap = new ConcurrentSkipListMap<Integer, RtEntry>();
	}
	
	public void updateRoutingTable() {
		
	}
	
	
	public class RtEntry {
		public int nextHop;
		public int numHopsToDest;
		
		public RtEntry(int nid, int n) {
			nextHop = nid;
			numHopsToDest = n;
		}
		
	}

}
