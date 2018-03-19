package edu.auburn.comp6360.application;

public class Vehicle {
	
	private double length;
	private double width;

	private byte[] address;
	private GPS gps;
	private double velocity;
	private double acceleration;
	private int brake;
	private double gas;
	
	private int nodeID;
	
	
	public Vehicle() {
		gps = new GPS();
		brake = 1;
		gas = 100.0;
	}
	
	public Vehicle(byte[] addr, GPS initGps, double initSpeed, double initAcc, int nodeId) {
		this.address = addr;
		this.gps = initGps;
		this.velocity = initSpeed;
		this.acceleration = initAcc;
		this.nodeID = nodeId;
		
		this.brake = 1;
		this.gas = 100.0;
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
	
	
}
