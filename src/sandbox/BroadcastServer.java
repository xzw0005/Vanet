package sandbox;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
//import java.net.SocketException;

public class BroadcastServer {

	private static DatagramSocket socket = null;
	
	public static void main(String[] args) {
		byte[] buf = new byte[256];
		DatagramPacket packet = new DatagramPacket(buf, buf.length);
		try {
			socket = new DatagramSocket(4445);
			while (true) {
					socket.receive(packet);
					System.out.println("Received from " + packet.getAddress() + ":" + packet.getPort());
					buf = packet.getData();
					System.out.println("Content: " + new String(buf));
					buf = "ACK".getBytes();
					DatagramPacket response = new DatagramPacket(buf, buf.length);
					socket.send(response);
					socket.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
			
		
		
	}
	
	
	
}
