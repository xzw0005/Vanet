package edu.auburn.comp6360.network;

import java.io.Serializable;
import java.util.concurrent.ConcurrentSkipListMap;

public class HelloMessage implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7653657505279965021L;
	private ConcurrentSkipListMap<Integer, String> oneHopNeighbors;
	
	public HelloMessage(ConcurrentSkipListMap<Integer, String> neighborTable) {
		oneHopNeighbors = neighborTable;
	}
	
	public ConcurrentSkipListMap<Integer, String> getOneHopNeighbors() {
		return oneHopNeighbors;
	}	
	
}
