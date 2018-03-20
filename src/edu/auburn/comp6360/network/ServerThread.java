package edu.auburn.comp6360.network;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

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
				byte[] data = new byte[MAX_PACKET_SIZE];
				DatagramPacket recvPacket = new DatagramPacket(data, MAX_PACKET_SIZE);
				try {
					socket.receive(recvPacket);
				} catch (Exception e) {
					e.printStackTrace();
					stopListening();
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
