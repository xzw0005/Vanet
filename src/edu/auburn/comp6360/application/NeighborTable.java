package edu.auburn.comp6360.application;

import java.util.concurrent.ConcurrentSkipListMap;

public class NeighborTable {
	
	private ConcurrentSkipListMap<Integer, String> oneHopNeighbors;
	private ConcurrentSkipListMap<Integer, Integer> twoHopNeighbors;
	
	public NeighborTable() {
		this.oneHopNeighbors = new ConcurrentSkipListMap<Integer, String>();
		this.twoHopNeighbors = new ConcurrentSkipListMap<Integer, Integer>();
	}

	public ConcurrentSkipListMap<Integer, String> getOneHopNeighbors() {
		return oneHopNeighbors;
	}

	public ConcurrentSkipListMap<Integer, Integer> getTwoHopNeighbors() {
		return twoHopNeighbors;
	}
	
	public String getLinkStatus(int nid) {
		return oneHopNeighbors.get(nid);
	}
	
	public int getAccessThrough(int twoHopNb) {
		return twoHopNeighbors.get(twoHopNb);
	}
	
	public void setLinkStatus(int nid, String status) {
		oneHopNeighbors.put(nid, status);
	}
	
	public void setAccessThrough(int twoHopNb, int accessThrough) {
		twoHopNeighbors.put(twoHopNb, accessThrough);
	}
	
}
