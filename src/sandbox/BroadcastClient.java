package sandbox;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class BroadcastClient {
	
	private static DatagramSocket socket = null;
	
	public static void main(String[] args) {
		try {
			broadcast("Hello", InetAddress.getByName("192.168.122.255"));
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}
	
	public static void broadcast(String msg, InetAddress addr) {
		try {
			socket = new DatagramSocket();
			socket.setBroadcast(true);
			
			byte[] buffer = msg.getBytes();
			DatagramPacket packet = new DatagramPacket(buffer, buffer.length, addr, 4445);
			socket.send(packet);
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

}
