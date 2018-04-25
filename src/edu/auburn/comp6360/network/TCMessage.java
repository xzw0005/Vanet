package edu.auburn.comp6360.network;

import java.io.Serializable;
import java.util.concurrent.ConcurrentSkipListSet;

public class TCMessage implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8257502186256118705L;
	private ConcurrentSkipListSet<Integer> MPRs;
	
	public TCMessage() {
		this.MPRs = new ConcurrentSkipListSet<Integer>();
	}
		
	public TCMessage(ConcurrentSkipListSet<Integer> MPRs) {
		this.MPRs = MPRs;
	}	
	
	public void setMPRs(ConcurrentSkipListSet<Integer> MPRs) {
		this.MPRs = MPRs;
	}
	
	public ConcurrentSkipListSet<Integer> getMPRs() {
		return MPRs;
	}

}
