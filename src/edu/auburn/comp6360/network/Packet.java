package edu.auburn.comp6360.network;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import edu.auburn.comp6360.application.Vehicle;

public class Packet implements Serializable {
	
	private PacketHeader header;
	private Vehicle vehicle;
	
	private int packetSize;
	
	public Packet() {
		
	}
	
	public void setHead(PacketHeader header) {
		this.header = header;
	}
	
	public void setVehicle(Vehicle v) {
		this.vehicle = v;
	}
	
	public byte[] packetAssembler(Packet packet) throws Exception {
		ByteArrayOutputStream bos = null;
		ObjectOutputStream oos = null;
		byte[] packetData = null;
		try {
			bos = new ByteArrayOutputStream();
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
	
	public Packet packetDessembler(byte[] packetData) throws Exception {
		ByteArrayInputStream bis = null;
		ObjectInputStream ois = null;
		Object packetObject = null;
		try {
			bis = new ByteArrayInputStream(packetData);
			ois = new ObjectInputStream(bis);
			packetObject = ois.readObject();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return (Packet)packetObject;
	}
	
	public PacketHeader getHeader() {
		return this.header;
	}
	
	public Vehicle getVehicle() {
		return this.vehicle;
	}
	
	public int getPacketSize() {
		return 4096;
	}
	
}
