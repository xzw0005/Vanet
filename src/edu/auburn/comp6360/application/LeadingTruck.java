package edu.auburn.comp6360.application;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

public class LeadingTruck extends Vehicle {	

	public static final double TRUCK_WIDTH = 4;
	public static final double TRUCK_LENGTH = 10;
	public static double INIT_X = 10;
	public static double INIT_Y = 0; // in the Right Lane
	public static double INIT_V = 30;
	
	private Queue<Node> roadTrain = (Queue<Node>) new ArrayList<Node>();
	
	
	public LeadingTruck(int nodeId) {
		super(nodeId);
		this.setGPS(new GPS(INIT_X, INIT_Y));
		this.setWidth(TRUCK_WIDTH);
		this.setLength(TRUCK_LENGTH);
		this.setVelocity(INIT_V);
		this.roadTrain.addLast(nodesMap.get(nodeId));
	}

//	public void formRoadTrain() {
//		inRoadTrain = true;		
//	}
	
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
