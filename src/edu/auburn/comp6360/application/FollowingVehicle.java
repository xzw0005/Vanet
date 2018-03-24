package edu.auburn.comp6360.application;

public class FollowingVehicle extends Vehicle {
	
	public enum VType {LEAD, FOLLOW};
//	public enum Lane {LEFT, RIGHT};
	public static final double CAR_WIDTH = 3;
	public static final double CAR_LENGTH = 5;	
	public double RANDOM_X = Math.random() * 300;
	public double INIT_Y = 5; // in the Left Lane
	public double RANDOM_V = 25 + Math.random() * 10;
	
	private int prev;
	private int post;
	private boolean isInRoadTrain;
	
	public FollowingVehicle(int nodeId) {
		super(nodeId);
		this.setGPS(new GPS(RANDOM_X, INIT_Y));
		this.setWidth(CAR_WIDTH);
		this.setLength(CAR_LENGTH);
		this.setVelocity(RANDOM_V);
		this.setAcceleration();
	}
	
	public FollowingVehicle(int nodeId, double init_x) {
		super(nodeId);
		this.setGPS(new GPS(init_x, INIT_Y));
		this.setWidth(CAR_WIDTH);
		this.setLength(CAR_LENGTH);
		this.setVelocity(RANDOM_V);
		this.setAcceleration();
	}
	
	public FollowingVehicle(int nodeId, double init_x, double init_v) {
		super(nodeId);
		this.setGPS(new GPS(init_x, INIT_Y));
		this.setWidth(CAR_WIDTH);
		this.setLength(CAR_LENGTH);
		this.setVelocity(init_v);
		this.setAcceleration();
	}		
	
	@Override
	public void setAcceleration() {
		this.setAcceleration(this.getAcceleration());
	}	
	
	
//	public void setLane(Lane lane) {
//		if (lane == Lane.LEFT)
//			gps.setY(5);
//		else if (lane == Lane.RIGHT) 
//			gps.setY(0);
//		else
//			System.err.println("Invalid lane setting.");
//	}
	
	public void joinRoadTrain() {
		// TODO
		
		gps.setY(0); 	// merge to the right lane
		isInRoadTrain = true;
	}
	
	public void leaveRoadTrain() {
		// TODO
		
		gps.setY(5);	// switch to the left lane
		isInRoadTrain = false;
	}
	
	
}
