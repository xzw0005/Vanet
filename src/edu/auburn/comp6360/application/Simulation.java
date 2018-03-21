package edu.auburn.comp6360.application;


import java.net.*;

import edu.auburn.comp6360.utilities.ConfigFileHandler;

public class Simulation {

	public static void main(String[] args) throws Exception {
		
		String filename = "config.txt";
		String type = args[0];
		if (type.equals("lead")) {
			ConfigFileHandler.reset(filename);
			int nodeID = 1;
			LeadingTruck lv = new LeadingTruck(nodeID);
			while (true) {
				
				String h = InetAddress.getLocalHost().getHostAddress();
				System.out.println(h);
				
				
			}
			
			
			
		} else if (type.equals("follow")) {
			System.out.println("Running a " + type + "ing vehicle.");
			int nodeID = ConfigFileHandler.newNodeID(filename);
			FollowingVehicle fv = new FollowingVehicle(nodeID); 
//			fv.start();
			
		} else {
			System.err.println("Please specify the type of this vehicle to be lead or follow!");
			return;
		}
		
	}
	
}
