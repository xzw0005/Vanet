package edu.auburn.comp6360.application;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.Map.Entry;
import java.util.Set;

public class NeighborTable {
	
	private int neighborhoodSequenceNumber;
	private ConcurrentSkipListMap<Integer, String> oneHopNeighbors;
	private ConcurrentSkipListMap<Integer, ConcurrentSkipListSet<Integer>> twoHopNeighbors;
	private ConcurrentSkipListMap<Integer, Integer> twoHopNeighborsThroughMPR;
	private ConcurrentSkipListSet<Integer> mprSet;
	private ConcurrentSkipListSet<Integer> mprSelectorTable;

	public NeighborTable() {
		this.neighborhoodSequenceNumber = 0;
		this.oneHopNeighbors = new ConcurrentSkipListMap<Integer, String>();
		this.twoHopNeighbors = new ConcurrentSkipListMap<Integer, ConcurrentSkipListSet<Integer>>();
		this.twoHopNeighborsThroughMPR = new ConcurrentSkipListMap<Integer, Integer>();
		this.mprSelectorTable = new ConcurrentSkipListSet<Integer>();
	}
	
	public ConcurrentSkipListSet<Integer> getMprSelectorTable() {
		return mprSelectorTable;
	}

	public ConcurrentSkipListMap<Integer, String> getOneHopNeighbors() {
		return oneHopNeighbors;
	}

	public ConcurrentSkipListMap<Integer, ConcurrentSkipListSet<Integer>> getTwoHopNeighbors() {
		return twoHopNeighbors;
	}
	
	public String getLinkStatus(int nid) {
		return oneHopNeighbors.get(nid);
	}
	
	public ConcurrentSkipListSet<Integer> getAccessThrough(int twoHopNb) {
		return twoHopNeighbors.get(twoHopNb);
	}
	
	public void setLinkStatus(int nid, String status) {
		oneHopNeighbors.put(nid, status);
	}
	
	public void setAccessThrough(int twoHopNb, ConcurrentSkipListSet<Integer> accessThroughSet) {
		twoHopNeighbors.put(twoHopNb, accessThroughSet);
	}
	
	public int getNeighborhoodSequenceNumber() {
		return neighborhoodSequenceNumber;
	}
	
//	public ConcurrentSkipListSet<Integer> getMPRs() {
//		ConcurrentSkipListSet<Integer> selfMPRs = new ConcurrentSkipListSet<Integer>();
//		for (Integer nb : oneHopNeighbors.keySet()) {
//			if (oneHopNeighbors.get(nb).equalsIgnoreCase("MPR"))
//				selfMPRs.add(nb);
//		}
//		return selfMPRs;
//	}

	public ConcurrentSkipListSet<Integer> getMPRs() {
		return this.mprSet;
	}
	
	
	public ConcurrentSkipListSet<Integer> getBiLinks(ConcurrentSkipListMap<Integer, String> neighbors) {
		ConcurrentSkipListSet<Integer> biLinks = new ConcurrentSkipListSet<Integer>();
		for (int nid : neighbors.keySet()) {
			if (neighbors.get(nid).equalsIgnoreCase("BI"))
				biLinks.add(nid);
		}
		return biLinks;
	}
	 
	
	public boolean isOneHopNeighbor(int source) {
		return oneHopNeighbors.containsKey(source);
	}
	
	public boolean isTwoHopNeighbor(int source) {
		return twoHopNeighbors.containsKey(source);
	}
	
	public void removeTwoHopNeighbor(int source) {
		this.twoHopNeighbors.remove(source);
	}
	
	public boolean updateTwoHopNeighbors(int selfId, int source, ConcurrentSkipListMap<Integer, String> neighborsOfSource) {
		boolean updated = false;
		for (int twoHopNb : twoHopNeighbors.keySet()) {
			if (twoHopNeighbors.get(twoHopNb).contains(source)) {
				if ((!neighborsOfSource.containsKey(twoHopNb)) || (neighborsOfSource.get(twoHopNb).equalsIgnoreCase("UNI"))) {
					twoHopNeighbors.get(twoHopNb).remove(Integer.valueOf(source));
					updated = true;
				}
			}
		}
		for (int nid : neighborsOfSource.keySet()) {
			if (nid == selfId) {
				if (neighborsOfSource.get(selfId).equals("MPR"))
					this.mprSelectorTable.add(source);
				else
					this.mprSelectorTable.remove(source);
			}
			else if (neighborsOfSource.get(nid).equalsIgnoreCase("BI") || neighborsOfSource.get(nid).equalsIgnoreCase("MPR")) {
				ConcurrentSkipListSet<Integer> accessThroughSet = new ConcurrentSkipListSet<Integer>();
				if (twoHopNeighbors.containsKey(nid))
					accessThroughSet = twoHopNeighbors.get(nid);
				boolean added = accessThroughSet.add(source);
				if (added)
					updated = true;
				twoHopNeighbors.put(nid, accessThroughSet);
			}
		}
		ArrayList<Integer> toRemove = new ArrayList<Integer>();
		for (int twoHopNb : twoHopNeighbors.keySet()) {
			if (twoHopNeighbors.get(twoHopNb).isEmpty()) 
				toRemove.add(twoHopNb);
		}
		for (int i : toRemove)
			twoHopNeighbors.remove(i);
		if (updated) {
			updateMPRs();
			++neighborhoodSequenceNumber;
		}
		return updated;
	}
	
	
	public void updateMPRs() {
		this.twoHopNeighborsThroughMPR = new ConcurrentSkipListMap<Integer, Integer>();
		mprSet = new ConcurrentSkipListSet<Integer>();
		Map<Integer, Integer> accessThroughSizes = new HashMap<Integer, Integer>();
		for (int twoHopNb : twoHopNeighbors.keySet()) {
			int size = twoHopNeighbors.get(twoHopNb).size();
			accessThroughSizes.put(twoHopNb, size);
		}
		
		Set<Entry<Integer, Integer>> set = accessThroughSizes.entrySet();
		ArrayList<Entry<Integer, Integer>> list = new ArrayList<Entry<Integer, Integer>>(set);
		Collections.sort(list, new Comparator<Map.Entry<Integer, Integer>>() {
			public int compare(Map.Entry<Integer, Integer> o1, Map.Entry<Integer, Integer> o2) {
				return (o1.getValue()).compareTo(o2.getValue());
			}
		} );
		
		for (Entry<Integer, Integer> entry : list) {
			int twoHopNb = entry.getKey();
			ConcurrentSkipListSet<Integer> accessThroughSet = twoHopNeighbors.get(twoHopNb);
			boolean addMPR = true;
			for (int mpr : mprSet) {
				if (accessThroughSet.contains(mpr)) {
					this.twoHopNeighborsThroughMPR.put(twoHopNb, mpr);
					addMPR = false;
				}
			}
			if (addMPR) {
				int mpr = accessThroughSet.first();
				mprSet.add(mpr);
				this.twoHopNeighborsThroughMPR.put(twoHopNb, mpr);
			}
		}

		for (int mpr : mprSet) {
			this.setLinkStatus(mpr, "MPR");
		}
	}
	
	
}
