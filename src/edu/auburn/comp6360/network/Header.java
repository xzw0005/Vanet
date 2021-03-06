package edu.auburn.comp6360.network;

import java.io.Serializable;

public class Header implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -645034248475856092L;	// Automatically generated by Eclipse 

	private int seqNum;
	private int source;
	private int prevHop;
	private double prevX;
	private String packetType; // "NORMAL", "JOIN", "LEAVE", "ACKJOIN", "ACKLEAVE"; "HELLO", "TC"
	private int dest;
	private int piggyback;
	
	private int pathLength;	// Used for computing latency
	
	public Header(String type, int source, int sn, int prevHop, double prevX) {
		this.packetType = type;
		this.source = source;
		this.seqNum = sn;		
		this.prevHop = prevHop;
		this.prevX = prevX;
		this.dest = -1;
		this.piggyback = -1;
		this.pathLength = 0;
	}

	public Header(String type, int source, int sn, int prevHop, double prevX, int dest, int extraInfo) {
		this.packetType = type;
		this.source = source;
		this.seqNum = sn;		
		this.prevHop = prevHop;
		this.prevX = prevX;
		this.dest = dest;
		this.piggyback = extraInfo;
		this.pathLength = 0;
	}
	
	public int getSeqNum() {
		return this.seqNum;
	}
	
	public int getSource() {
		return this.source;
	}
	
	public int getDest() {
		return this.dest;
	}
	
	public int getPrevHop() {
		return this.prevHop;
	}
	
	public String getPacketType() {
		return this.packetType;
	}
	
	public int getPiggyback() {
		return this.piggyback;
	}

	public void setPrevHop(int nodeID) {
		this.prevHop = nodeID;
	}
	
	public void setDest(int dest) {
		this.dest = dest;
	}
	
	public void setPiggyback(int extraInfo) {
		this.piggyback = extraInfo;
	}
	
	public int increasePathLength() {
		return this.pathLength++;
	}
	
	public boolean inTransmissionRange(double xCoord) {
		return Math.abs(xCoord - prevX) < 100;
	}

	public boolean shouldUnlink(double xCoord) {
		return Math.abs(xCoord - prevX) > 102.5;
	}
	
	public double getSenderX() {
		return this.prevX;
	}
	
//	public void setPacketType(int type) {
//		this.packetType = type;
//	}
	
}
