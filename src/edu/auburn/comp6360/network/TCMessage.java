package edu.auburn.comp6360.network;

import java.io.Serializable;
import java.util.concurrent.ConcurrentSkipListSet;

public class TCMessage implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8257502186256118705L;
	private ConcurrentSkipListSet<Integer> mprSelectorTable;
	
	public TCMessage() {
		this.mprSelectorTable = new ConcurrentSkipListSet<Integer>();
	}
		
	public TCMessage(ConcurrentSkipListSet<Integer> mprSelectors) {
		this.mprSelectorTable = mprSelectors;
	}	
	
	public void setMPRs(ConcurrentSkipListSet<Integer> mprSelectors) {
		this.mprSelectorTable = mprSelectors;
	}
	
	public ConcurrentSkipListSet<Integer> getMprSelectorTable() {
		return mprSelectorTable;
	}

}
