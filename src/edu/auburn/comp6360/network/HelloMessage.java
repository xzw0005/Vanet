package edu.auburn.comp6360.network;

import java.io.Serializable;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.ConcurrentSkipListSet;

public class HelloMessage implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7653657505279965021L;
	private ConcurrentSkipListMap<Integer, String> neighbors;
	
	public HelloMessage(ConcurrentSkipListMap<Integer, String> oneHopNeighbors) {
		neighbors = oneHopNeighbors;
	}
	
	public ConcurrentSkipListMap<Integer, String> getOneHopNeighbors() {
		return neighbors;
	}	

	public ConcurrentSkipListSet<Integer> getBiLinks() {
		ConcurrentSkipListSet<Integer> biLinks = new ConcurrentSkipListSet<Integer>();
		for (int nid : neighbors.keySet()) {
			if (neighbors.get(nid).equalsIgnoreCase("BI") || (neighbors.get(nid).equalsIgnoreCase("MPR")))
				biLinks.add(nid);
		}
		return biLinks;
	}
	
	public ConcurrentSkipListSet<Integer> getSourceMPRs() {
		ConcurrentSkipListSet<Integer> sourceMPRs = new ConcurrentSkipListSet<Integer>();
		for (Integer nb : neighbors.keySet()) {
			if (neighbors.get(nb).equalsIgnoreCase("MPR"))
				sourceMPRs.add(nb);
		}
		return sourceMPRs;
	}
		
	
}
