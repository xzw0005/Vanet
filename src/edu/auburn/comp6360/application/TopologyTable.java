package edu.auburn.comp6360.application;

import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.ConcurrentSkipListSet;

import edu.auburn.comp6360.network.TCMessage;

public class TopologyTable {
	
	private final int HOLD = 5000;
	private ConcurrentSkipListMap<Integer, TTcontent> topologyMap;
	
	public TopologyTable() {
		topologyMap = new ConcurrentSkipListMap<Integer, TTcontent>();
	}
	
	public ConcurrentSkipListSet<Integer> getDestMPRs(int nid) {
		return topologyMap.get(nid).getDestMPRs();
	}
	
	public void updateTopologyTable(int nid, int tcSn, TCMessage tc) {
		ConcurrentSkipListSet<Integer> tcMPRs = tc.getMPRs();
		TTcontent entry = new TTcontent(tcSn, tcMPRs);
		if ((topologyMap.get(nid) == null) || (tcSn > entry.getTTsn()))
			topologyMap.put(nid, entry);
		else if (tcSn == entry.getTTsn())
			topologyMap.get(nid).refreshHoldingTime();
	}
	
	
	public class TTcontent {
		public ConcurrentSkipListSet<Integer> destMPRs;
		public int sn;
		public int holdingTime;
		
		public TTcontent(int newSn, ConcurrentSkipListSet<Integer> newDestMPRs) {
			sn = newSn;
			destMPRs = newDestMPRs;
			holdingTime = HOLD;
		}
		
		public int getTTsn() {
			return sn;
		}
		
		public ConcurrentSkipListSet<Integer> getDestMPRs() {
			return destMPRs;
		}
		
		public void refreshHoldingTime() {
			holdingTime = HOLD;
		}
	}

}
