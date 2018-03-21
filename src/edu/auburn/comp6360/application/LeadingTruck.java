package edu.auburn.comp6360.application;

public class LeadingTruck extends Vehicle {	

	public double INIT_X = 10;
	public double INIT_Y = 0; // in the right lane
	public double INIT_V = 30;
	
	public LeadingTruck() {
		super();
	}
	
	public LeadingTruck(int nodeId) {
		super(nodeId);
		this.setGPS(new GPS(INIT_X, INIT_Y));
		this.setWidth(Vehicle.TRUCK_WIDTH);
		this.setLength(Vehicle.TRUCK_LENGTH);
		this.setVelocity(INIT_V);
	}
	
  /**
   * Obtain the acceleration for the Leading Vehicle (every 10 ms)
   * 
   * @param
   * @return a random number between -1 and 1
   */	
	@Override
	public void setAcceleration() {
		this.setAcceleration(Math.random() * 2 - 1);
	}
	
	
	public void start() {
		
	}
	
	
	
	
	
	
}
