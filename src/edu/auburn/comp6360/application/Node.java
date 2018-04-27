package edu.auburn.comp6360.application;

//import java.util.SortedSet;
//import java.util.TreeSet;
import java.util.concurrent.ConcurrentSkipListSet;

public class Node {

	private int nodeID;
	private String hostname;
	private int port;
	private double x, y;
	private ConcurrentSkipListSet<Integer> links;
	
	public Node(int nodeID, String hostName, int portNumber, double xCoord, double yCoord) {
		this.nodeID = nodeID;
		this.hostname = hostName;
		this.port = portNumber;
		this.x = xCoord; 
		this.y = yCoord; 
		this.links = new ConcurrentSkipListSet<Integer>();
	}
	
	public void setLinks(ConcurrentSkipListSet<Integer> neighborSet) {
		this.links = neighborSet;
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
	
	public GPS getGPS() {
		return new GPS(this.x, this.y);
	}
	
	public void setGPS(GPS gps) {
		this.x = gps.getX();
		this.y = gps.getY();
	}

	synchronized public ConcurrentSkipListSet<Integer> getLinks() {
		return links;
	}
	
	public void addLink(int link) {
		links.add(link);
	}
	
	public boolean removeLink(int link) {
		return links.remove(Integer.valueOf(link));
	}
	
	@Override
	synchronized public String toString() {
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
