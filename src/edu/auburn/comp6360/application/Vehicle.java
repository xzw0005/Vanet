package edu.auburn.comp6360.application;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.ExecutorService;

import edu.auburn.comp6360.network.ClientThread;
import edu.auburn.comp6360.network.Packet;
import edu.auburn.comp6360.network.VehicleInfo;
import edu.auburn.comp6360.network.Header;
import edu.auburn.comp6360.utilities.ConfigFileHandler;
import edu.auburn.comp6360.utilities.PacketHandler;
import edu.auburn.comp6360.utilities.VehicleHandler;

public abstract class Vehicle {
	
	public static final String FILENAME = "config.txt";
	public static final int SERVER_PORT = 10120;
	protected boolean SET_BROADCAST;
	
	protected double length;
	protected double width;

	protected GPS gps;
	protected double velocity;
	protected double acceleration;
	protected long timeStamp;	
	
	protected String hostName;
	protected int serverPort;
//	protected int packetSeqNum;
	protected ConcurrentHashMap<String, Integer> snMap;
	
	protected int nodeID;	
	protected ConcurrentSkipListMap<Integer, Node> nodesMap;	// from config file
	protected ConcurrentSkipListSet<Integer> neighborSet;
	protected ConcurrentLinkedQueue<Packet> forwardQueue;
	
	protected RbaCache cache;
	protected int front;
	protected VehicleInfo frontVinfo;
	
	// States for Sending & Receiving packet types
	protected String pType;
	protected int waitJoinReply;
	protected int waitOK;
	
//	private boolean waitingAckLeave;
	
	
	protected int letCarIn;
	
	protected ExecutorService executor;

	protected SendingThread send_thread;
	protected ConfigThread config_thread;
	protected ReceivingThread recv_thread;
	protected ForwardingThread fwd_thread;
	protected BroadcastThread brcst_thread;
	
	protected long initialTime;
	protected int numPacketReceived;
	protected int numPacketLost;
	protected int numLatencyRecord;
	protected double avgLatency;
	
//	public Vehicle() {
//		
//		gps = new GPS();
//		timeStamp = System.currentTimeMillis();
//		snMap = VehicleHandler.initializeSequenceNumbers();
//		try {
//			hostName = InetAddress.getLocalHost().getHostName();//.substring(0, 6);
//			if (hostName.indexOf(".") > -1)
//				hostName = hostName.substring(0, hostName.indexOf("."));
//		} catch (UnknownHostException e) {			
//			e.printStackTrace();
//		}
//	}
	
	public Vehicle(int nodeId) {
		nodeID = nodeId;
		gps = new GPS();
		timeStamp = System.currentTimeMillis();
		snMap = VehicleHandler.initializeSequenceNumbers();
		try {
			hostName = InetAddress.getLocalHost().getHostName();//.substring(0, 6);
			if (hostName.indexOf(".") > -1)
				hostName = hostName.substring(0, hostName.indexOf("."));
			if (hostName.substring(0, 3).equals("tux"))
				this.SET_BROADCAST = true;
			else
				this.SET_BROADCAST = false;
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		if (SET_BROADCAST == true)
			serverPort = SERVER_PORT;
		else
			serverPort = SERVER_PORT + nodeID;
		
		forwardQueue = new ConcurrentLinkedQueue<Packet>();
		neighborSet = new ConcurrentSkipListSet<Integer>();
		nodesMap = new ConcurrentSkipListMap<Integer, Node>();
		nodesMap.put(nodeID, new Node(nodeID, hostName, serverPort, gps.getX(), gps.getY()));
		cache = new RbaCache();
		front = 0;
		frontVinfo = null;
		letCarIn = 0;
		pType = "normal";

		initialTime = timeStamp;
		numPacketReceived = 0;
		numPacketLost = 0;
		numLatencyRecord = 0;
		avgLatency = 0;
	
	}
	
	public void setLength(double length) {
		this.length = length;
	}
	
	public void setWidth(double width) {
		this.width = width;
	}	
	
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
	
	
	public int inreaseSeqNum(String packetType) {
		int sn = this.snMap.get(packetType);
		this.snMap.put(packetType, ++sn);
		return sn;
	}
	
	
	public Packet initPacket(String type, int dest, int extraInfo) {
		int sn = this.inreaseSeqNum(type);
		int source = this.nodeID;
		int prevHop = this.nodeID;
		Header header = new Header(type, source, sn, prevHop);
		if (!type.equals("normal")) {
			header.setDest(dest);
			header.setPiggyback(extraInfo);
		}
		Packet packetToSend = new Packet(header);
		if (type.equals("normal")) {
			VehicleInfo vInfo = new VehicleInfo(gps, velocity, acceleration);
			packetToSend.setVehicleInfo(vInfo);
		}
		this.cache.updatePacketSeqNum(source, type, sn, getNodeID());
//		sendPacket(packetToSend, source, sn, prevHop);
//		if (!type.equals("normal"))
//			System.out.println(packetToSend.toString());
		return packetToSend;
	}
	
	public Packet initPacket() {
		return initPacket("normal", -1, -1);
	}
	
	public void sendPacket(Packet packetToSend, int prevHop) {
		if (neighborSet.isEmpty()) 
			return;
		for (int nbID : neighborSet) {
			if (nodesMap.get(nbID) != null) {
				Node nb = nodesMap.get(nbID);
				if (nbID != prevHop) {
					String nbHostname = nb.getHostname();
					int nbPort = nb.getPortNumber();
//					System.out.println("Send to: " + nbHostname + ":" + nbPort);
//					System.out.println("\tPacket: " + packetToSend);
					ClientThread ct = new ClientThread(nbHostname, nbPort, packetToSend);
					ct.run();
//					executor.execute(new ClientThread(nbHostname, nbPort, packetToSend));			
				}
			}
		}				
	}
	
	/*
	 * Upon received packet:
	 * 		Update the sequence number and broadcast number in cache
	 * 		Update the neighbor list (between this node and the source node)
	 * 		
	 * 		Forward to its neighbors (except previous hop)
	 */
	public void receivePacket(Packet packetReceived) {
//		System.out.println(packetReceived.toString());
		Header header = packetReceived.getHeader();
		int prevHop = header.getPrevHop();
		
//		StringBuffer sb = new StringBuffer();
//		if (neighborSet.isEmpty())
//			sb.append("EMPTY!");
//		else {
//			for (Integer i : neighborSet)
//				sb.append(i);
//		}
//		System.out.println("Neighbor List: " + sb.toString() + "\tPacket from " + prevHop);
		
		int source = header.getSource();
		int sn = header.getSeqNum();
		String packetType = header.getPacketType();
		if (packetType.equals("normal"))  {
			if (!(neighborSet.contains(prevHop)) || prevHop==nodeID)
				return;
			
			if (VehicleHandler.ifPacketLoss(this.gps, nodesMap.get(prevHop).getGPS())) {
				++this.numPacketLost;
				return;
			}
			++this.numPacketReceived;
			
			// Received packet originated from itself, used to compute latency
			if ((source == nodeID) && (packetReceived.increasePathLength() == 1))  { 
				long packetInitTime = this.timeStamp - (this.snMap.get(packetType) - sn) * 10;
				long latency = System.currentTimeMillis() - packetInitTime;
				this.avgLatency = (avgLatency * numLatencyRecord + latency) / (numLatencyRecord + 1);
				this.numLatencyRecord++;
				return;
			}
			
//			if (sn % 300 == 0) {
//				System.out.println("Received packet " + packetReceived.toString());
//				System.out.println("Node " + nodeID + " is following " + front);
//			}
			if (!cache.updatePacketSeqNum(source, packetType, sn, getNodeID())) 
				return;

			VehicleInfo vInfo = packetReceived.getVehicleInfo();
			if (front == source) {
				frontVinfo = vInfo;
			}
			
			neighborSet = VehicleHandler.updateNeighborsFromPacket(nodeID, gps, neighborSet, source, vInfo.getGPS());
			packetReceived.increasePathLength();
			forwardQueue.add(packetReceived);
			
//			Packet packetToForward = PacketHandler.forwardCopy(packetReceived, nodeID);		// This is slow!!!
//			packetReceived.setPrevHop(nodeID);
//			if (SET_BROADCAST)
//				forwardQueue.add(packetReceived);
//			else
//				sendPacket(packetReceived, prevHop);
				
			
		} else {		// in the case that of not normal packets
			if ( (header.getDest() != this.nodeID) && (cache.updatePacketSeqNum(source, packetType, sn, getNodeID())) ) {
				packetReceived.setPrevHop(nodeID);
				sendPacket(packetReceived, prevHop);
			}
			else if (header.getDest() == this.nodeID) {
				System.out.println("\nReceived " + packetType + " message from Node " + source + ".\n");
				int info = header.getPiggyback();
				if (packetType.equals("join"))
					processJoinRequest(source);
				else if (packetType.equals("leave"))
					processLeaveRequest(source);
				else if (packetType.equals("ackJoin"))
					processAckJoin(source, info);
				else if (packetType.equals("ackLeave"))
					processAckLeave(source, info);
				else if (packetType.equals("notify"))
					letCarIn = header.getPiggyback();
				else if (packetType.equals("ok"))
					processOK(source, info);
				else if (packetType.equals("update")) {
					front = header.getPiggyback();
					waitOK = 0;
				}
			}
		}
	}
	
	
	public void processJoinRequest(int source) {
		
	}

	public void processLeaveRequest(int source) {
		
	}

	public void processAckLeave(int source, int info) {
		
	}

	public void processAckJoin(int source, int info) {
		
	}
	
	public void processOK(int source, int info) {
		
	}
	
	
	public void followAhead() {
		if (letCarIn > 0) {		// 
			if (frontVinfo.getX() - gps.getX() >= 30) {
				setVelocity(frontVinfo.getVelocity() + 5);	// increase speed to catch up
				setAcceleration(0);
			} else if (frontVinfo.getX() - gps.getX() <= 20) {
				setVelocity(frontVinfo.getVelocity() - 5);	// decrease speed to slow down
				setAcceleration(0);
			} else {
				setVelocity(frontVinfo.getVelocity());
				setAcceleration(frontVinfo.getAcceleration());
				sendSpecificPacket("ok", 1, letCarIn);
				waitOK = 1;
				letCarIn = 0;
			}				
		} else {
			if (frontVinfo.getX() - gps.getX() >= 20) {
				setVelocity(frontVinfo.getVelocity() + 5);	// increase speed to catch up
				setAcceleration(0);
			} else if (frontVinfo.getX() - gps.getX() <= 10) {
				setVelocity(frontVinfo.getVelocity() - 5);	// decrease speed to slow down
				setAcceleration(0);
			} else {
				setVelocity(frontVinfo.getVelocity());
				setAcceleration(frontVinfo.getAcceleration()); 
				if (gps.getY() != 0) {		// in the case of not in road train
					sendSpecificPacket("ackJoin", 1, front);
					gps.setY(0); 	// merge to the right lane, & join the road train						
				}
			}				
		}
		
		if (gps.getX() >= 4000 && gps.getX() <= 5000) {
			if (frontVinfo.getVelocity() > 26) {
				setVelocity(20);
				setAcceleration();
			}		
		}
	}
	
	public void driveSelf(double dt) {
		if (gps.getX() >= 4000 && gps.getX() <= 5000)
			setVelocity(20);
		else if ((gps.getX() > 5000) && (velocity < 21))
			setVelocity(30);
		else
			setVelocity(VehicleHandler.computeVelocity(velocity, acceleration, dt));
		setAcceleration();	
	}
	

	public void sensorUpdate() {
		long currentTime = System.currentTimeMillis();
		double dt = (currentTime - this.timeStamp) / 1000.0; // in seconds
		this.timeStamp = currentTime;
//		System.out.println(dt);
		setGPS(VehicleHandler.computeGPS(gps, velocity, acceleration, dt));
		if (frontVinfo != null)
			followAhead();
		else
			driveSelf(dt);
	}
	
	public void sendSpecificPacket(String pType, int dest, int info) {
		if ((nodesMap == null) || (nodesMap.get(dest) == null))
			return;
		Packet specialPacket = initPacket(pType, dest, info);
		Node destNode = nodesMap.get(dest);
//		for (Integer i : nodesMap.keySet()) {
//			Node n = nodesMap.get(i);
//			System.out.println(n);
//		}
//		System.out.println(specialPacket.toString() + " Destination: Node " + dest);
//		System.out.println(destNode.toString());
//		System.out.println(destNode.getHostname() + ":" + destNode.getPortNumber());
		// TODO: Send at most 3 times, but notice that this method is in ServerThread
		ClientThread tempClient = new ClientThread(destNode.getHostname(), destNode.getPortNumber(), specialPacket);
		tempClient.run();		
	}

	
	public void startAll() {
		if (this.SET_BROADCAST == true) {
			brcst_thread = new BroadcastThread();
		} else {
			send_thread = new SendingThread();
		}	
		fwd_thread = new ForwardingThread();
		recv_thread = new ReceivingThread(serverPort);		
		config_thread = new ConfigThread();
		
		if (this.SET_BROADCAST == true) {
			brcst_thread.start();
		} else {
			send_thread.start();
		}		
		fwd_thread.start();
		config_thread.start();
		recv_thread.run();
				
	}
	
	public void writeCalculationResults() {
		long running_time = System.currentTimeMillis() - this.initialTime;
		if (running_time >= 3 * 60 * 1000) {
			String fname = "result_" + this.nodeID + ".txt";
			try {
				PrintWriter pw = new PrintWriter(fname);
				pw.println("Running Time: " + running_time);
				pw.println("Total Number of Packets should be received by this vehicle: " + this.numPacketReceived);
				pw.println("Number of lost packets: " + this.numPacketLost);
				pw.println("Average latency = " + this.avgLatency + "\t calculated upon " + this.numLatencyRecord + " packets.");
				pw.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			
		}
	}
	

	public class SendingThread extends Thread {
		@Override
		public void run() {
			while (true) {
				try {
					Packet p = initPacket();
					sendPacket(p, nodeID); 
					Thread.sleep(10);
					sensorUpdate();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}		
	}

	
	
	public class BroadcastThread extends Thread {
		private DatagramSocket socket;

		public BroadcastThread() {
			try {
				socket = new DatagramSocket();
				socket.setBroadcast(true);
			} catch (SocketException e) {
				e.printStackTrace();
			}
		}
		
		@Override
		public void run() {
			while (true) {
				//System.out.println("GOGO BROADCASTING");
				Packet packetToSend = initPacket();
				PacketHandler.broadcastPacket(packetToSend, socket, serverPort);
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				sensorUpdate();			
			}
		}
	}
	
	public class ForwardingThread extends Thread {
		private DatagramSocket socket;
		
		public ForwardingThread() {
			if (SET_BROADCAST == true) {
				try {
					socket = new DatagramSocket();
					socket.setBroadcast(true);
				} catch (SocketException e) {
					e.printStackTrace();
				}				
			}
		}
		
		@Override
		public void run() {
			while (true) {
				if (!forwardQueue.isEmpty()) {
					Packet packetToForward = forwardQueue.poll();
					int prevHop = packetToForward.getPrevHop();
					packetToForward.setPrevHop(nodeID);
					if (SET_BROADCAST == true) {
						PacketHandler.broadcastPacket(packetToForward, socket, serverPort);						
					} else {
						sendPacket(packetToForward, prevHop);						
					}
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
					Node selfNode = new Node(nodeID, hostName, serverPort, gps.getX(), gps.getY());
					nodesMap = config.writeConfigFile(selfNode);
					neighborSet = nodesMap.get(nodeID).getLinks();		
					Thread.sleep(500);
					writeCalculationResults();
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	
	public class ReceivingThread implements Runnable {
		public final int MAX_PACKET_SIZE = 4096;
		private int port;
		private boolean listening;
		
		public ReceivingThread(int port) {
			this.port = port;
			this.listening = false;
		}
		
		@Override
		public void run() {
			try {
				DatagramSocket socket = new DatagramSocket(port);
				listening = true;
				while (listening) {
					byte[] packetData = new byte[MAX_PACKET_SIZE];
					DatagramPacket datagramPacketReceived = new DatagramPacket(packetData, MAX_PACKET_SIZE);
					try {
						socket.receive(datagramPacketReceived);
						packetData = datagramPacketReceived.getData();
						Packet packetReceived = PacketHandler.packetDessembler(packetData);
						receivePacket(packetReceived);
					} catch (IOException e) {
						e.printStackTrace();
						stopListening();
						socket.close();
					}
				}
			} catch (SocketException e) {
				e.printStackTrace();
			}
		}
		
		public void stopListening() {
			this.listening = false;
		}
	}

	
}
