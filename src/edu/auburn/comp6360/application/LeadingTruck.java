package edu.auburn.comp6360.application;

import java.util.LinkedList;
import java.util.List;

//import edu.auburn.comp6360.application.Vehicle.ConfigThread;
//import edu.auburn.comp6360.application.Vehicle.SendRegularPacketThread;
//import edu.auburn.comp6360.network.ServerThread;

public class LeadingTruck extends Vehicle {	

	public static final double TRUCK_WIDTH = 4;
	public static final double TRUCK_LENGTH = 10;
	public static double INIT_X = 10;
	public static double INIT_Y = 0; // in the Right Lane
	public static double INIT_V = 30;
	
	private List<Integer> roadTrainList;
	
	public LeadingTruck(int nodeID) {
		super(nodeID);
		this.setGPS(new GPS(INIT_X, INIT_Y));
		this.setWidth(TRUCK_WIDTH);
		this.setLength(TRUCK_LENGTH);
		this.setVelocity(INIT_V);
		
		roadTrainList = new LinkedList<Integer>();
		roadTrainList.add(nodeID);
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
	
	@Override
	public void startAll() {
		super.startAll();
		
		RoadTrainHandlerThread train = new RoadTrainHandlerThread();
		train.start();
	}
	
	
	public class RoadTrainHandlerThread extends Thread {
		
		@Override
		public void run() {
			
		}

	}
	
	
	
	
	
}
