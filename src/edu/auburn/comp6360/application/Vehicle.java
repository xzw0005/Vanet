package edu.auburn.comp6360.application;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.SortedMap;
import java.util.concurrent.ExecutorService;

import edu.auburn.comp6360.network.ClientThread;
import edu.auburn.comp6360.network.Packet;
import edu.auburn.comp6360.network.PacketHeader;
import edu.auburn.comp6360.network.ServerThread;
import edu.auburn.comp6360.network.VehicleInfo;
import edu.auburn.comp6360.utilities.ConfigFileHandler;
import edu.auburn.comp6360.utilities.VehicleHandler;

public abstract class Vehicle {
	
	public static final int SERVER_PORT = 10121;
	
	protected double length;
	protected double width;

	protected GPS gps;
	protected double velocity;
	protected double acceleration;
	protected long timeStamp;	
	
//	private byte[] address;
	protected String hostName;
	protected int packetSeqNum;
	
	protected int nodeID;	
	protected Node selfNode; 
	protected SortedMap<Integer, Node> nodesMap; // from config file
	
	protected RbaCache cache;
	protected List<Node> neighbors;

	protected boolean inRoadTrain;
	
	private ExecutorService executor;

	private BroadcastThread bt;
	private ConfigThread ct;
	private ServerThread st;
	
	
	public Vehicle() {
		gps = new GPS();
		timeStamp = System.currentTimeMillis();
		packetSeqNum = 0;
		try {
			hostName = InetAddress.getLocalHost().getHostName();//.substring(0, 6);
			if (hostName.indexOf(".") > -1)
				hostName = hostName.substring(0, hostName.indexOf("."));
		} catch (UnknownHostException e) {			
			e.printStackTrace();
		}
	}
	
	public Vehicle(int nodeID) {
		this.nodeID = nodeID;
		this.gps = new GPS();
		this.timeStamp = System.currentTimeMillis();
		this.packetSeqNum = 0;
		try {
			hostName = InetAddress.getLocalHost().getHostName();//.substring(0, 6);
			if (hostName.indexOf(".") > -1)
				hostName = hostName.substring(0, hostName.indexOf("."));
			System.out.println(hostName);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		this.selfNode = new Node(nodeID, hostName, SERVER_PORT, gps.getX(), gps.getY());
		this.nodesMap.put(nodeID, selfNode);
	}
	
	public void setLength(double length) {
		this.length = length;
	}
	
	public void setWidth(double width) {
		this.width = width;
	}
	
//	public void setAddr(byte[] localAddr) {
//		this.address = localAddr;
//	}
	
	
	public void setGPS(GPS newGps) {
		this.gps.setX(newGps.getX());
		this.gps.setY(newGps.getY());
	}
	
	public void setVelocity(double newSpeed) {
		this.velocity = newSpeed;
	}
	
	public void setAcceleration() {

	}
	
	public void setAcceleration(double newAcc) {
		this.acceleration = newAcc;
	}
	
	public double getLength() {
		return this.length;
	}
	
	public double getWidth() {
		return this.width;
	}
	
	public String getHostName() {
		return this.hostName;
	}
	
	public GPS getGPS() {
		return this.gps;
	}
	
	public double getVelocity() {
		return this.velocity;
	}
	
	public double getAcceleration() {
		return this.acceleration;
	}
	
	public int getNodeID() {
		return this.nodeID;
	}
	
	
	public void inreaseSeqNum() {
		++this.packetSeqNum;
	}
	
	
	synchronized public void initPacket() {
		int source = this.nodeID;
		int prevHop = this.nodeID;
		int sn = ++this.packetSeqNum;
		PacketHeader header = new PacketHeader(source, prevHop, sn, 1);
		VehicleInfo vInfo = new VehicleInfo(gps, velocity, acceleration);
		Packet newPacket = new Packet(header, vInfo);
//		sendPacket(newPacket, neighbors);
	}
	
	
	public void receivePacket() {
		
	}
	
	/*
	 * Upon received packet, forward to its neighbors (except previous hop)
	 */
	synchronized public void sendPacket(Packet packet, List<Node> receivers) {
		PacketHeader header = packet.getHeader();
		int source = header.getSource();
		int sn = header.getSeqNum();
		int prevHop = header.getPrevHop();
		
		if (cache.updatePacketSeqNum(source, sn)) {
			for (Node nb : receivers) {
				if (nb.getNodeID() != prevHop) {
					String nbHostname = nb.getHostname();
					int nbPort = nb.getPortNumber();
					packet.getHeader().setPrevHop(this.nodeID);
					executor.execute(new ClientThread(nbHostname, nbPort, packet));
				}
			}
		}
	}
	
	public void sensorUpdate() {
		long currentTime = System.currentTimeMillis();
		double dt = (currentTime - this.timeStamp) / 1000.0; // in seconds
		this.timeStamp = currentTime;
//		System.out.println(dt);
		setGPS(VehicleHandler.computeGPS(gps, velocity, acceleration, dt));
		setVelocity(VehicleHandler.computeVelocity(velocity, acceleration, dt));
		setAcceleration();
	}
	
	public void startAll() {
		bt = new BroadcastThread();
		ct = new ConfigThread();
		st = new ServerThread(SERVER_PORT);
		bt.start();
		ct.start();
		st.run();
//		System.out.println("????");
	}

	public class BroadcastThread extends Thread {
		
		@Override
		public void run() {
			while (true) {
				try {
					initPacket();
					Thread.sleep(10);
					sensorUpdate();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		
	}
	
	public class ConfigThread extends Thread {
		@Override
		public void run() {
			String filename = "config.txt";
			ConfigFileHandler config = new ConfigFileHandler(filename);
			while (true) {
				try {
					selfNode.setGPS(gps);
					nodesMap = config.writeConfigFile(selfNode);
					Thread.sleep(100);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	
	
}
