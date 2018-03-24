package edu.auburn.comp6360.application;


import edu.auburn.comp6360.utilities.ConfigFileHandler;

public class Simulation {

	public static void main(String[] args) throws Exception {
		
		String filename = "config.txt";
		String type = args[0];
		ConfigFileHandler config = new ConfigFileHandler(filename);
		
		if (type.equals("lead")) {
			config.reset();
			int nodeID = 1;
			LeadingTruck lv = new LeadingTruck(nodeID);
			lv.startAll();
			
			
		} else if (type.equals("follow")) {
			System.out.println("Running a " + type + "ing vehicle.");
			int nodeID = config.newNodeID(filename);
			if (args.length == 1) {
				FollowingVehicle fv = new FollowingVehicle(nodeID);	
				fv.startAll();
			} else if (args.length == 2) {
				double initX = Double.parseDouble(args[1]);
				FollowingVehicle fv = new FollowingVehicle(nodeID, initX);
				fv.startAll();
			} else if (args.length == 3) {
				double initX = Double.parseDouble(args[1]);
				double initY = Double.parseDouble(args[2]);
				FollowingVehicle fv = new FollowingVehicle(nodeID, initX, initY);
				fv.startAll();
			} else {
				System.err.println("There are at most 2 arguments besides the following vehicle type!");
				return;
			}	
			
		} else {
			System.err.println("Please specify the type of this vehicle to be lead or follow!");
			return;
		}
		
	}
	
}
