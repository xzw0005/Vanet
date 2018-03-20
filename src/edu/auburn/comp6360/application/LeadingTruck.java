package edu.auburn.comp6360.application;

public class LeadingTruck extends Vehicle {

	public LeadingTruck() {
		super();
	}
	
	public LeadingTruck(byte[] addr, GPS initGps, double initSpeed, double initAcc, int nodeId) {
		super(addr, initGps, initSpeed, initAcc, nodeId);
		this.setWidth(ConfigConstants.TRUCK_WIDTH);
		this.setLength(ConfigConstants.TRUCK_LENGTH);
	}
	
  /**
   * Obtain the acceleration for the Leading Vehicle (every 10 ms)
   * 
   * @param
   * @return a random number between -1 and 1
   */	
	public void setAcceleration() {
		this.setAcceleration(Math.random() * 2 - 1);
	}
	
	
	
	
	
	
	
	
	
	
	
	
}
