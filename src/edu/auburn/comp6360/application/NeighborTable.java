package edu.auburn.comp6360.application;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.ConcurrentSkipListSet;

public class NeighborTable {
	
	private int neighborhoodSequenceNumber;
	private ConcurrentSkipListMap<Integer, String> oneHopNeighbors;
	private ConcurrentSkipListMap<Integer, ConcurrentSkipListSet<Integer>> twoHopNeighbors;
	private HashMap<Integer, Integer> twoHopNeighborsThroughMPR;
	
	public NeighborTable() {
		this.neighborhoodSequenceNumber = 0;
		this.oneHopNeighbors = new ConcurrentSkipListMap<Integer, String>();
		this.twoHopNeighbors = new ConcurrentSkipListMap<Integer, ConcurrentSkipListSet<Integer>>();
		this.twoHopNeighborsThroughMPR = new HashMap<Integer, Integer>();
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
	
	public int increaseSequenceNumber() {
		return ++neighborhoodSequenceNumber;
	}
	
	public ConcurrentSkipListSet<Integer> getMPRs() {
		ConcurrentSkipListSet<Integer> selfMPRs = new ConcurrentSkipListSet<Integer>();
		for (Integer nb : oneHopNeighbors.keySet()) {
			if (oneHopNeighbors.get(nb).equalsIgnoreCase("MPR"))
				selfMPRs.add(nb);
		}
		return selfMPRs;
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
	
	public void updateTwoHopNeighbors(int source, ConcurrentSkipListMap<Integer, String> neighborsOfSource) {
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
			if (neighborsOfSource.get(nid).equalsIgnoreCase("BI") || neighborsOfSource.get(nid).equalsIgnoreCase("MPR")) {
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
			if (twoHopNeighbors.get(twoHopNb).size() == 0) 
				toRemove.add(twoHopNb);
		}
		for (int i : toRemove)
			twoHopNeighbors.remove(i);
		if (updated) 
			updateMPRs();
	}
	
	
	public void updateMPRs() {
		
		
		
		return;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
