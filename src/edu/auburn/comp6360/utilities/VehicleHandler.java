package edu.auburn.comp6360.utilities;

import edu.auburn.comp6360.application.GPS;

public class VehicleHandler {
	
	public static GPS computeGPS(GPS gps, double velocity, double acceleration, double dt) {
		double x = gps.getX();
		double distance = velocity * dt + .5 * acceleration * Math.pow(dt, 2);
		return new GPS(x + distance, gps.getY());
	}
	
	public static double computeVelocity(double velocity, double acceleration, double dt) {
		return velocity + acceleration * dt;
	}

}
