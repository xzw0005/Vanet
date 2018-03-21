package edu.auburn.comp6360.application;

public class GPS {
	
	private double x;
	private double y;
	
	public GPS() {

	}
	
	public GPS(double x, double y) {
		this.x = x;
		this.y = y;
	}
	
	public void setX(double x) {
		this.x = x;
	}
	
	public void setY(double y) {
		this.y = y;
	}
	
	public double getX() {
		return this.x;
	}
	
	public double getY() {
		return this.y;
	}
	
//	public long getTimestamp() {
//		return this.timeStamp;
//	}
//	
//	public void update(double vel, double acc) {
//		long currentTime = System.currentTimeMillis();
//		double dt = (currentTime - this.timeStamp) / 1000.0; // in seconds
//		double distance = vel + 0.5 * acc * Math.pow(dt, 2); 
//		this.timeStamp = currentTime;
//		this.x += distance;
//	}
//	

}
