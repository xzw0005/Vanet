package edu.auburn.comp6360.network;

import java.io.Serializable;

public class PacketHeader implements Serializable {

	private int seqNum;
	private int source;
	private int prevHop;
	private int packetType;
	
	public PacketHeader() {
		
	}
	
	public PacketHeader(int source, int prevHop, int sn, int type) {
		this.source = source;
		this.prevHop = prevHop;
		this.seqNum = sn;
		this.packetType = type;
	}
	
	public int getSeqNum() {
		return this.seqNum;
	}
	
	public int getSource() {
		return this.source;
	}
	
	public int getPrevHop() {
		return this.prevHop;
	}
	
	public int getPacketType() {
		return this.packetType;
	}
	
//	public void setSeqNum(int sn) {
//		this.seqNum = sn;
//	}
//	
//	public void setSource(int sourceNodeID) {
//		this.source = sourceNodeID;
//	}
	
	public void setPrevHop(int nodeID) {
		this.prevHop = nodeID;
	}
	
	public void setPacketType(int type) {
		this.packetType = type;
	}
	
}
