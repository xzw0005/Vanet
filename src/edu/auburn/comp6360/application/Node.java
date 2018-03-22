package edu.auburn.comp6360.application;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

public class Node {

	private int nodeID;
	private String hostname;
	private int port;
	private double x, y;
	private List<Integer> links;
	
	public Node(int nodeID, String host, int portNumber, double xCoord, double yCoord) {
		this.nodeID = nodeID;
		this.hostname = host;
		this.port = portNumber;
		this.x = xCoord; 
		this.y = yCoord; 
		this.links = new ArrayList<Integer>();
	}
	
	public int getNodeID() {
		return nodeID;
	}
	
	public String getHostname() {
		return hostname;
	}
	
	public int getPortNumber() {
		return port;
	}
	
	public double getX() {
		return this.x;
	}
	
	public double getY() {
		return this.y;
	}
	
	synchronized public List<Integer> getLinks() {
		return links;
	}
	
	public void addLink(int link) {
		links.add(link);
		Collections.sort(links);
	}
	
	public boolean removeLink(int link) {
		return links.remove(Integer.valueOf(link));
	}
	
	public void setGPS(GPS gps) {
		this.x = gps.getX();
		this.y = gps.getY();
	}
	
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer("Node ");
		sb.append(getNodeID() + " " + getHostname() + ", " + getPortNumber() + " ");		
		sb.append(String.format("%.1f %.1f links", getX(), getY()));
		for (int link: getLinks()) {
			sb.append(" ");
			sb.append(link);
		}
		return sb.toString(); 
	}
	
	public boolean equals(Node other) {
		return this.nodeID == other.nodeID;
	}
	
}
