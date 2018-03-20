package edu.auburn.comp6360.application;

import java.util.List;
import java.util.ArrayList;

public class Node {

	private int nodeID;
	private String host;
	private int port;
	private double x, y;
	private List<Node> neighbors;
	
	public Node(int nodeID, String host, int port, double xCoord, double yCoord) {
		this.nodeID = nodeID;
		this.host = host;
		this.port = port;
		this.x = xCoord; //gps.getX();
		this.y = yCoord; //gps.getY();
		this.neighbors = new ArrayList<Node>();
	}
	
	public int getNodeID() {
		return nodeID;
	}
	
	public String getHostname() {
		return host;
	}
	
	public int getPortNumber() {
		return port;
	}
	
	public double getXcoord() {
		return this.x;
	}
	
	public double getYcoord() {
		return this.y;
	}
	
	synchronized public List<Node> getNeighbors() {
		return neighbors;
	}
	
	public boolean addNeighbor(Node nb) {
		return neighbors.add(nb);
	}
	
	public boolean removeNeighbor(Node nb) {
		return neighbors.remove(nb);
	}
	
	public void setGPS(GPS gps) {
		this.x = gps.getX();
		this.y = gps.getY();
	}
	
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer("Node ");
		sb.append(this.nodeID);		
		sb.append(" ");
		sb.append(this.host);
		sb.append(", ");
		sb.append(this.port);
		sb.append(" ");
		sb.append(this.x);
		sb.append(" ");
		sb.append(this.y);
		sb.append(" links");
		for (Node link: this.neighbors) {
			sb.append(" ");
			sb.append(link.getNodeID());
		}
		return sb.toString(); 
	}
	
	public boolean equals(Node other) {
		return this.nodeID == other.nodeID;
	}
	
}
