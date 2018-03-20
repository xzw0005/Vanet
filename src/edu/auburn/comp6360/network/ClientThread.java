package edu.auburn.comp6360.network;

import java.net.InetAddress;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.*;

public class ClientThread implements Runnable {

	private String hostName;
	private int portNum;
	private Packet packetToSent;
	
	public ClientThread(String hostName, int portNum, Packet packetToSent) {
		this.hostName = hostName;
		this.portNum = portNum;
		this.packetToSent = packetToSent;
	}
	
	@Override
	public void run() {
		try {
			DatagramSocket socket = new DatagramSocket();
			InetAddress host = InetAddress.getByName(hostName); 
			byte[] data = Packet.packetAssembler(packetToSent);
			DatagramPacket packet = new DatagramPacket(data, data.length, host, portNum);
			socket.send(packet);
			socket.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
