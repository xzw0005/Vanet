package edu.auburn.comp6360.utilities;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;

import edu.auburn.comp6360.network.Packet;

public class PacketHandler {

	public static final String BROADCAST_ADDRESS = "131.204.14.255";
	
	public static byte[] packetAssembler(Packet packet) {
		ByteArrayOutputStream bos = null;
		ObjectOutputStream oos = null;
		byte[] packetData = null;
		bos = new ByteArrayOutputStream();
		try {
			oos = new ObjectOutputStream(bos);
			oos.writeObject((Object)packet);
			oos.flush();
			packetData = bos.toByteArray();
//			System.out.println("PACKET ASSEMBLER: size = " + packetData.length);
			oos.close();
			bos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return packetData;
	}
	
	public static Packet packetDessembler(byte[] packetData) {
//		System.out.println("PACKET DESSEMBLER: size = " + packetData.length);
		
		ByteArrayInputStream bis = null;
		ObjectInputStream ois = null;
		Object packetObject = null;

		bis = new ByteArrayInputStream(packetData);
		try {
			ois = new ObjectInputStream(bis);
			packetObject = ois.readObject();
		} catch (ClassNotFoundException | IOException e) {
			e.printStackTrace();
		}
		return (Packet)packetObject;
	}
	
	public static Packet forwardCopy(Packet packetToForward, int newPrevHop) {
		// Serialization of object
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutputStream oos = null;
		ObjectInputStream ois = null;
		Packet copied = null;
		try {
			oos = new ObjectOutputStream(bos);
			oos.writeObject(packetToForward);
			//Deserialization of object
			byte[] packetBytes = bos.toByteArray();
			ByteArrayInputStream bis = new ByteArrayInputStream(packetBytes);
			ois = new ObjectInputStream(bis);
			copied = (Packet) ois.readObject();
			copied.setPrevHop(newPrevHop);
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
		return copied;
	}
	
	
	public static void broadcastPacket(Packet packet, DatagramSocket socket, int port) {
		byte[] buffer = PacketHandler.packetAssembler(packet);
		try {
			InetAddress addr = InetAddress.getByName(BROADCAST_ADDRESS);
			DatagramPacket p = new DatagramPacket(buffer, buffer.length, addr, port);
			socket.send(p);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}	
	
}
