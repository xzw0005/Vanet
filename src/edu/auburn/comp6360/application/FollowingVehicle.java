package edu.auburn.comp6360.application;

public class FollowingVehicle extends Vehicle {
	
	public FollowingVehicle(int nodeID) {
		super();
	}
	
	@Override
	public void setAcceleration() {
		this.setAcceleration(this.getAcceleration());
	}	
}
