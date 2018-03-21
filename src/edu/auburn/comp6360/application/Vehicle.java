package edu.auburn.comp6360.application;

import java.util.List;
import java.util.concurrent.ExecutorService;

import edu.auburn.comp6360.network.ClientThread;
import edu.auburn.comp6360.network.Packet;
import edu.auburn.comp6360.network.PacketHeader;
import edu.auburn.comp6360.network.VehicleInfo;

public class Vehicle {
	
	public enum VType {LEAD, FOLLOW};
	public static final double TRUCK_WIDTH = 4;
	public static final double TRUCK_LENGTH = 10;
	public static final double CAR_WIDTH = 3;
	public static final double CAR_LENGTH = 5;
	public static final int LEFT = 1;
	public static final int RIGHT = 0;
	
	private double length;
	private double width;

	private GPS gps;
	private double velocity;
	private double acceleration;
	
	private byte[] address;
	private int packetSeqNum;
	
	private int nodeID;	// from config file
	
	private VehicleCache cache;
	private List<Node> neighbors;
	
	
	private ExecutorService executor;
	
	public Vehicle() {
		gps = new GPS();
		packetSeqNum = 0;
	}
	
	public Vehicle(int nodeID) {
		this.nodeID = nodeID;
		this.gps = new GPS();
		this.packetSeqNum = 0;
	}
	
	public void setLength(double length) {
		this.length = length;
	}
	
	public void setWidth(double width) {
		this.width = width;
	}
	
	public void setAddr(byte[] localAddr) {
		this.address = localAddr;
	}
	
	public void setGps(GPS newGps) {
		this.gps.setX(newGps.getX());
		this.gps.setY(newGps.getY());
	}
	
	public void setVelocity(double newSpeed) {
		this.velocity = newSpeed;
	}
	
	public void setAcceleration(double newAcc) {
		this.acceleration = newAcc;
	}
	
	public double getLength() {
		return this.length;
	}
	
	public double getWidth() {
		return this.width;
	}
	
	public byte[] getAddr() {
		return this.address;
	}
	
	public GPS getGps() {
		return this.gps;
	}
	
	public double getVelocity() {
		return this.velocity;
	}
	
	public double getAcceleration() {
		return this.acceleration;
	}
	
	public int getNodeID() {
		return this.nodeID;
	}
	
	
	public void inreaseSeqNum() {
		++this.packetSeqNum;
	}
	
	
	synchronized public void initPacket() {
		int source = this.nodeID;
		int prevHop = this.nodeID;
		int sn = ++this.packetSeqNum;
		PacketHeader header = new PacketHeader(source, prevHop, sn, 1);
		VehicleInfo vInfo = new VehicleInfo(gps, velocity, acceleration);
		Packet newPacket = new Packet(header, vInfo);
		sendPacket(newPacket, neighbors);
	}
	
	
	/*
	 * Upon received packet, forward to its neighbors (except previous hop)
	 */
	synchronized public void sendPacket(Packet packet, List<Node> receivers) {
		PacketHeader header = packet.getHeader();
		int source = header.getSource();
		int sn = header.getSeqNum();
		int prevHop = header.getPrevHop();
		
		if (cache.updatePacketSeqNum(source, sn)) {
			for (Node nb : receivers) {
				if (nb.getNodeID() != prevHop) {
					String nbHostname = nb.getHostname();
					int nbPort = nb.getPortNumber();
					packet.getHeader().setPrevHop(this.nodeID);
					executor.execute(new ClientThread(nbHostname, nbPort, packet));
				}
			}
		}
	}
	
	public void start() {
		
	}
	
	
}
