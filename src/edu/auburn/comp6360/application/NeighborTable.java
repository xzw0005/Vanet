package edu.auburn.comp6360.application;

//import java.util.TreeSet;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.ConcurrentSkipListSet;

public class NeighborTable {
	
	private int neighborhoodSequenceNumber;
	private ConcurrentSkipListMap<Integer, String> oneHopNeighbors;
	private ConcurrentSkipListMap<Integer, ConcurrentSkipListSet<Integer>> twoHopNeighbors;
	
	public NeighborTable() {
		this.neighborhoodSequenceNumber = 0;
		this.oneHopNeighbors = new ConcurrentSkipListMap<Integer, String>();
		this.twoHopNeighbors = new ConcurrentSkipListMap<Integer, ConcurrentSkipListSet<Integer>>();
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
		for (int nb2 : this.twoHopNeighbors.keySet()) {
			
		}
		
		for (int nid : neighborsOfSource.keySet()) {
			if (neighborsOfSource.get(nid).equalsIgnoreCase("BI") || neighborsOfSource.get(nid).equalsIgnoreCase("MPR")) {
				ConcurrentSkipListSet<Integer> accessThroughSet = new ConcurrentSkipListSet<Integer>();
				if (twoHopNeighbors.containsKey(nid))
					accessThroughSet = twoHopNeighbors.get(nid);
				boolean added = accessThroughSet.add(source);
				if (added)
					updated = true;
			}
		}
	}
	
}
