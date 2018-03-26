package edu.auburn.comp6360.network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import edu.auburn.comp6360.utilities.PacketHandler;

public class BroadcastClient implements Runnable {
	
	private static final String BROADCAST_ADDRESS = "131.204.14.255";
	private int serverPort;
	private Packet packetToSend;
	
	public BroadcastClient(Packet packetToSend, int serverPort) {
		this.packetToSend = packetToSend;
	}
	
	@Override
	public void run() {
		try {
			DatagramSocket socket = new DatagramSocket();
			socket.setBroadcast(true);
			InetAddress addr = InetAddress.getByName(BROADCAST_ADDRESS);
			byte[] buffer = PacketHandler.packetAssembler(packetToSend);
			DatagramPacket packet = new DatagramPacket(buffer, buffer.length, addr, serverPort);
			socket.send(packet);
			socket.close();
		} catch (SocketException | UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

}
