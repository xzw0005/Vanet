package edu.auburn.comp6360.application;

public class LeadingTruck extends Vehicle {	
	
	public LeadingTruck() {
		super();
	}
	
	public LeadingTruck(int nodeId) {
		super(nodeId);
		this.setWidth(Vehicle.TRUCK_WIDTH);
		this.setLength(Vehicle.TRUCK_LENGTH);
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
