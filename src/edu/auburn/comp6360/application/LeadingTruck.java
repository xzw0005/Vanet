package edu.auburn.comp6360.application;

public class LeadingTruck extends Vehicle {	

	public static final double TRUCK_WIDTH = 4;
	public static final double TRUCK_LENGTH = 10;
	public double INIT_X = 10;
	public double INIT_Y = 5; // in the Right Lane
	public double INIT_V = 30;
	
	public LeadingTruck(int nodeId) {
		super(nodeId);
		this.setGPS(new GPS(INIT_X, INIT_Y));
		this.setWidth(TRUCK_WIDTH);
		this.setLength(TRUCK_LENGTH);
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
