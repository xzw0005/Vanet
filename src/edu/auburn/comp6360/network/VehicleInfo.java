package edu.auburn.comp6360.network;

import java.io.Serializable;

import edu.auburn.comp6360.application.GPS;

public class VehicleInfo implements Serializable {
	
	private double x, y;
	private double velocity;
	private double acceleration;
	private int brake;
	private double gas;
	
	public VehicleInfo(GPS gps, double velocity, double acceleration) {
		x = gps.getX();
		y = gps.getY();
		velocity = velocity;
		acceleration = acceleration;
		brake = 1;
		gas = 100;
	}
	
	public double getX() {
		return this.x;
	}
	
	public double getY() {
		return this.y;
	}
	
	public double getVelocity() {
		return this.velocity;
	}
	
	public double getAcceleration() {
		return this.acceleration;
	}
	
}
