package edu.auburn.comp6360.utilities;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import edu.auburn.comp6360.network.Packet;

public class PacketHandler {

	
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
			oos.close();
			bos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return packetData;
	}
	
	public static Packet packetDessembler(byte[] packetData) {
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
	
}
