package edu.auburn.comp6360.application;

//import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.ConcurrentSkipListSet;

import edu.auburn.comp6360.network.TCMessage;

public class TopologyTable {
	
	private final int HOLDING_TIME = 5000;
	private ConcurrentSkipListMap<Integer, TpTabEntry> topologyMap;
	
	public TopologyTable() {
		topologyMap = new ConcurrentSkipListMap<Integer, TpTabEntry>();
	}
	
	public ConcurrentSkipListSet<Integer> getDestMPRs(int nid) {
		return topologyMap.get(nid).getDestMPRs();
	}
	
	public boolean updateTopologyTable(int nid, int tcSn, TCMessage tc) {
		boolean updated = true;
		ConcurrentSkipListSet<Integer> tcMPRs = tc.getMprSelectorTable();
		TpTabEntry entry = new TpTabEntry(tcSn, tcMPRs);
		if ((!topologyMap.containsKey(nid)) || (tcSn > entry.getTTsn()))
			topologyMap.put(nid, entry);
		else if (tcSn == entry.getTTsn())
			topologyMap.get(nid).refreshHolding();
		else
			updated = false;
		return updated;
	}
	
	public boolean timeoutRemoveEntry(int nid) {
		boolean isTimeout = false;
		if (topologyMap.containsKey(nid)) {
			if (topologyMap.get(nid).getHoldUntil() <= System.currentTimeMillis()) {
				isTimeout = true;
				topologyMap.remove(nid);
			}
		}
		return isTimeout;
	}
	
	public boolean removeAllTimeoutEntries() {
		boolean removed = false;
		Set<Entry<Integer, TpTabEntry>> set = topologyMap.entrySet();
//		ArrayList<Entry<Integer, TTcontent>> entryList = new ArrayList<Entry<Integer, TTcontent>>(set);
//		for (Entry<Integer, TTcontent> entry : entryList) {
//			int nid = entry.getKey();
//			if (this.topologyMap.get(nid).getHoldUntil() <= System.currentTimeMillis()) {
//				removed = true;
//				topologyMap.remove(nid);
//			}
//		}
		Iterator<Entry<Integer, TpTabEntry>> iter = set.iterator();
		while (iter.hasNext()) {
			Entry<Integer, TpTabEntry> entry = iter.next();
			int nid = entry.getKey();
			if (topologyMap.get(nid).getHoldUntil() <= System.currentTimeMillis()) {
				removed = true;
				iter.remove();
			}
		}
		return removed;
	}
	
	
	public class TpTabEntry {
		public ConcurrentSkipListSet<Integer> destMPRs;
		public int sn;
		public long holdUntil;
		
		public TpTabEntry(int newSn, ConcurrentSkipListSet<Integer> newDestMPRs) {
			sn = newSn;
			destMPRs = newDestMPRs;
			holdUntil = System.currentTimeMillis() + HOLDING_TIME;
		}
		
		public int getTTsn() {
			return sn;
		}
		
		public ConcurrentSkipListSet<Integer> getDestMPRs() {
			return destMPRs;
		}
		
		public void refreshHolding() {
			holdUntil = System.currentTimeMillis() + HOLDING_TIME;
		}
		
		public long getHoldUntil() {
			return holdUntil;
		}
	}

}
