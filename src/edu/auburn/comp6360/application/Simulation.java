package edu.auburn.comp6360.application;


import edu.auburn.comp6360.utilities.ConfigFileHandler;

public class Simulation {

	public static void main(String[] args) throws Exception {
		
		String filename = "config.txt";
		String type = args[0];
		ConfigFileHandler config = new ConfigFileHandler(filename);
		
		long currentTime = System.currentTimeMillis();
		if (type.equals("lead")) {
			config.reset();
			int nodeID = 1;
			LeadingTruck lv = new LeadingTruck(nodeID);
			lv.startAll();
			
			
		} else if (type.equals("follow")) {
			System.out.println("Running a " + type + "ing vehicle.");
			int nodeID = config.newNodeID(filename);
			FollowingVehicle fv = new FollowingVehicle(nodeID); 
			fv.startAll();
			
		} else {
			System.err.println("Please specify the type of this vehicle to be lead or follow!");
			return;
		}
		
	}
	
}
