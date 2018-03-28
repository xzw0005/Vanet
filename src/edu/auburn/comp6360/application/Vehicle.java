package edu.auburn.comp6360.application;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
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
	
	
	protected double length;
	protected double width;

	protected GPS gps;
	protected double velocity;
	protected double acceleration;
	protected long timeStamp;	
	
	protected String hostName;
//	protected int packetSeqNum;
	protected Map<String, Integer> snMap;
	
	protected int nodeID;	
	protected Node selfNode; 
	protected SortedMap<Integer, Node> nodesMap; // from config file
	
	protected RbaCache cache;
	protected int front;
	protected VehicleInfo frontVinfo;
	protected int letCarIn;
	
	protected ExecutorService executor;

	protected BroadcastThread bt;
	protected ConfigThread ct;
	protected ServerThread st;
	
	
	public Vehicle() {
		
		gps = new GPS();
		timeStamp = System.currentTimeMillis();
		snMap = VehicleHandler.initializeSequenceNumbers();
		try {
			hostName = InetAddress.getLocalHost().getHostName();//.substring(0, 6);
			if (hostName.indexOf(".") > -1)
				hostName = hostName.substring(0, hostName.indexOf("."));
		} catch (UnknownHostException e) {			
			e.printStackTrace();
		}
	}
	
	public Vehicle(int nodeId) {
		nodeID = nodeId;
		gps = new GPS();
		timeStamp = System.currentTimeMillis();
		snMap = VehicleHandler.initializeSequenceNumbers();
		try {
			hostName = InetAddress.getLocalHost().getHostName();//.substring(0, 6);
			if (hostName.indexOf(".") > -1)
				hostName = hostName.substring(0, hostName.indexOf("."));
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		selfNode = new Node(nodeID, hostName, SERVER_PORT+nodeID, gps.getX(), gps.getY());
		nodesMap = new TreeMap<Integer, Node>();
		nodesMap.put(nodeID, selfNode);
		cache = new RbaCache();
		front = 0;
		frontVinfo = null;
		letCarIn = 0;
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
	
	
	public int inreaseSeqNum(String packetType) {
		if (this.snMap.get(packetType) == null)
			System.out.println(packetType);
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
			header.setExtraInfo(extraInfo);
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
	
	public void sendPacket(Packet packetToSend, int source, int sn, int prevHop) {
		SortedSet<Integer> neighbors = selfNode.getLinks();
		if (neighbors.isEmpty()) 
			return;
		for (int nbID : neighbors) {
			if (nodesMap.get(nbID) != null) {
				Node nb = nodesMap.get(nbID);
				if (nbID != prevHop) {
					String nbHostname = nb.getHostname();
					int nbPort = nb.getPortNumber();
					packetToSend.getHeader().setPrevHop(this.nodeID);
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
		Header header = packetReceived.getHeader();
		int source = header.getSource();
		int sn = header.getSeqNum();
		int prevHop = header.getPrevHop();
		String packetType = header.getPacketType();
		if (packetType.equals("normal"))  {
			if (cache.updatePacketSeqNum(source, packetType, sn, getNodeID())) {
				VehicleInfo vInfo = packetReceived.getVehicleInfo();
				selfNode = VehicleHandler.updateNeighborsFromPacket(selfNode, source, vInfo.getGPS());			
				sendPacket(packetReceived, source, sn, prevHop);
				if (front == source) {
					frontVinfo = vInfo;
				}
				if (sn % 100 == 0) {
					System.out.println(packetReceived.toString());
					System.out.println("Node " + nodeID + " is following " + front);
				}
				
			}
		} else {		// in the case that of not normal packets
			if ( (header.getDest() != this.nodeID) && (cache.updatePacketSeqNum(source, packetType, sn, getNodeID())) )
				sendPacket(packetReceived, source, sn, prevHop);
			else if (header.getDest() == this.nodeID) {
				System.out.println("Received " + packetType + " message from Node " + source);
				int info = header.getExtraInfo();
				if (packetType.equals("join"))
					processJoinRequest(source);
				else if (packetType.equals("leave"))
					processLeaveRequest(source);
				else if (packetType.equals("ackJoin"))
					processAckJoin(source, info);
				else if (packetType.equals("ackLeave"))
					processAckLeave(source, info);
				else if (packetType.equals("notify"))
					letCarIn = header.getExtraInfo();
				else if (packetType.equals("ok"))
					processOK(source, letCarIn);
				else if (packetType.equals("update")) 
					front = header.getExtraInfo();
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
		if (gps.getX() >= 4000 && gps.getX() <= 5000) {
			if (frontVinfo.getVelocity() > 20) {
				setVelocity(20);
				setAcceleration(0);
			}else {
				setVelocity(frontVinfo.getVelocity());
				setAcceleration(frontVinfo.getAcceleration());	
			}
		} else if (frontVinfo.getX() - gps.getX() >= 20) {
			setVelocity(frontVinfo.getVelocity() + 5);	// increase speed to catch up
			setAcceleration(0);
		} else if (frontVinfo.getX() - gps.getX() <= 10) {
			setVelocity(frontVinfo.getVelocity() - 5);	// decrease speed to slow down
			setAcceleration(0);
		} else {
			setVelocity(frontVinfo.getVelocity());
			setAcceleration(frontVinfo.getAcceleration());				
		}
	}
	

	public void sensorUpdate() {
		long currentTime = System.currentTimeMillis();
		double dt = (currentTime - this.timeStamp) / 1000.0; // in seconds
		this.timeStamp = currentTime;
//		System.out.println(dt);
		setGPS(VehicleHandler.computeGPS(gps, velocity, acceleration, dt));
		
		if (gps.getX() >= 4000 && gps.getX() <= 5000) {
			if (frontVinfo != null && frontVinfo.getVelocity() <= 20) {
				setVelocity(frontVinfo.getVelocity());
				setAcceleration(frontVinfo.getAcceleration());
			} else {
				setVelocity(20);
				setAcceleration(0);
			}
		} else if (frontVinfo != null) {
			
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
					if (gps.getY() != 0) {		// in the case of not in roadtrain
						sendSpecificPacket("ackJoin", 1, front);
						gps.setY(0); 	// merge to the right lane, & join the road train						
					}
				}				
			}
		} else {
			if ((gps.getX() > 5000) && (velocity < 21))
				setVelocity(30);
			else
				setVelocity(VehicleHandler.computeVelocity(velocity, acceleration, dt));
			setAcceleration();			
		}
		
	}
	
	public void sendSpecificPacket(String pType, int dest, int info) {
		if (nodesMap == null)
			return;
		Packet specialPacket = initPacket(pType, dest, info);
		System.out.println(specialPacket.toString());
		Node destNode = nodesMap.get(dest);
		System.out.println(destNode.getHostname() + ":::::::" + destNode.getPortNumber());
		// TODO: Send at most 3 times, but notice that this method is in ServerThread
		ClientThread tempClient = new ClientThread(destNode.getHostname(), destNode.getPortNumber(), specialPacket);
		tempClient.run();		
	}
	
//	public void startAll() {
//		bt = new BroadcastThread();
//		ct = new ConfigThread();
//		st = new ServerThread(SERVER_PORT+nodeID);
//
//		bt.start();
//		ct.start();
//		st.run();
//	}

	public class BroadcastThread extends Thread {
		
		@Override
		public void run() {
			while (true) {
				try {
					Packet p = initPacket();
					sendPacket(p, p.getHeader().getSource(), p.getHeader().getSeqNum(), p.getHeader().getPrevHop()); 
					Thread.sleep(10);
//					System.out.println("gogo");
					sensorUpdate();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		
	}
	
	
	public class BroadcastClient implements Runnable {
		public static final String BROADCAST_ADDRESS = "131.204.14.255";
		@Override
		public void run() {
			try {
				DatagramSocket socket = new DatagramSocket();
				socket.setBroadcast(true);
				Packet packetToSend = initPacket();
				byte[] buffer = PacketHandler.packetAssembler(packetToSend);
				InetAddress addr = InetAddress.getByName(BROADCAST_ADDRESS);
				DatagramPacket packet = new DatagramPacket(buffer, buffer.length, addr, SERVER_PORT);
				socket.send(packet);
				socket.close();
			} catch (SocketException | UnknownHostException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
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
					
//					System.out.println("config");
					
					Thread.sleep(500);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	
	public class ServerThread implements Runnable {
		public final int MAX_PACKET_SIZE = 4096;
		private int port;
		private boolean listening;
		
		public ServerThread(int port) {
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
//						System.out.println("@@" + packetReceived.toString());
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
