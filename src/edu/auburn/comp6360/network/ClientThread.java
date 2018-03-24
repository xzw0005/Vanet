package edu.auburn.comp6360.network;

import java.net.InetAddress;
//import java.net.SocketException;
//import java.net.UnknownHostException;
//import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

import edu.auburn.comp6360.utilities.PacketHandler;

public class ClientThread implements Runnable {

	private String serverHostname;
	private int serverPort;
	private Packet packetToSent;
	
	public ClientThread(String serverHostname, int serverPort, Packet packetToSent) {
		this.serverHostname = serverHostname;
		this.serverPort = serverPort;
		this.packetToSent = packetToSent;
	}
	
	@Override
	public void run() {
		try {
			DatagramSocket socket = new DatagramSocket();
			InetAddress serverAddr = InetAddress.getByName(serverHostname);
			byte[] data = PacketHandler.packetAssembler(packetToSent);
			DatagramPacket packet = new DatagramPacket(data, data.length, serverAddr, serverPort);
			socket.send(packet);
			socket.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}