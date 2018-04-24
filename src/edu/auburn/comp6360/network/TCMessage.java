package edu.auburn.comp6360.network;

import java.io.Serializable;
import java.util.concurrent.ConcurrentSkipListSet;

public class TCMessage implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8257502186256118705L;
	private int seqNum;
	private ConcurrentSkipListSet<Integer> MPRs;
	
	public TCMessage(int tcSn) {
		this.seqNum = tcSn;
		this.MPRs = new ConcurrentSkipListSet<Integer>();
	}
		
	public TCMessage(int tcSn, ConcurrentSkipListSet<Integer> MPRs) {
		this.seqNum = tcSn;
		this.MPRs = MPRs;
	}	
	
	public void setMPRs(ConcurrentSkipListSet<Integer> MPRs) {
		this.MPRs = MPRs;
	}
	
	public ConcurrentSkipListSet<Integer> getMPRs() {
		return MPRs;
	}
	
	public int getTcSeqNum() {
		return seqNum;
	}

}
