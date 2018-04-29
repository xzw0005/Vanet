package edu.auburn.comp6360.application;

import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.ConcurrentSkipListSet;

public class RoutingTable {
	
	private ConcurrentSkipListMap<Integer, RtEntry> routingMap;
	
	public RoutingTable() {
		routingMap = new ConcurrentSkipListMap<Integer, RtEntry>();
	}
	
	public void updateRoutingTable(int nid, ConcurrentSkipListMap<Integer, Node> nodesTopology, TopologyTable tt, ConcurrentSkipListSet<Integer> neighborSet) {
		routingMap = new ConcurrentSkipListMap<Integer, RtEntry>();
		for (int i : nodesTopology.keySet()) {
			if (i != nid) {
				RtEntry entry = getNextHop(nid, i, tt, neighborSet);
				if (entry.nextHop > 0) {
					routingMap.put(i, entry);
				}
			}
		}
	}
	
	public RtEntry getNextHop(int nid, int dest, TopologyTable tt, ConcurrentSkipListSet<Integer> neighborSet) {
		RtEntry entry = new RtEntry();
		// BFS or DFS could be used, search until nid's one-hop neighbor
		return entry;
	}
	
	public class RtEntry {
		public int nextHop;
		public int numHopsToDest;
		
		public RtEntry() {
			
		}		
		
		public RtEntry(int nid, int n) {
			nextHop = nid;
			numHopsToDest = n;
		}
		
	}

}
