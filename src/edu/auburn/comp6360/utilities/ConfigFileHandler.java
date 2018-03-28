package edu.auburn.comp6360.utilities;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
//import java.util.Scanner;
//import java.util.SortedMap;
//import java.util.TreeMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import edu.auburn.comp6360.application.Node;
import edu.auburn.comp6360.utilities.VehicleHandler;

public class ConfigFileHandler {
	
	private String filename;
	private static ReadWriteLock rwLock = new ReentrantReadWriteLock();
	
	public ConfigFileHandler(String filename) {
		this.filename = filename;
	}

	
	public ConcurrentSkipListMap<Integer, Node> readConfigFile() {
//		SortedMap<Integer, Node> nodesMap = new TreeMap<Integer, Node>();
		ConcurrentSkipListMap<Integer, Node> nodesMap = new ConcurrentSkipListMap<Integer, Node>();
		List<String> lines = null;
		try {
			rwLock.readLock().lock();
			lines = Files.readAllLines(Paths.get(filename), StandardCharsets.UTF_8);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			rwLock.readLock().unlock();
		}
		
//		Scanner sc;
//		for (String line : lines) {
//			//line = "Node 2 tux055, 10011 80 120 links 1 3 4";
//			//line = "Node 1 pavilion, 10121 34.6 0.0 links"
//			if (line.split(" ").length > 1) {
//				sc = new Scanner(line);
//				sc.next();	// eat "Node"
//				int nodeID = sc.nextInt();
//				String host = sc.next();
//				host = host.substring(0, host.indexOf(","));
//				int port = sc.nextInt();
//				double x = sc.nextDouble();
//				double y = sc.nextDouble();
//				Node node = new Node(nodeID, host, port, x, y);
//				sc.next();	// eat "links"
//				while (sc.hasNextInt()) {
//					node.addLink(sc.nextInt());
//				}
//				nodesMap.put(nodeID, node);				
//			}
//		}
		
		for (String line : lines) {			
			String[] lineArray = line.split(" ");
			if (lineArray.length >= 7) {
				int nodeID = Integer.parseInt(lineArray[1]);
				String host = lineArray[2].trim();
				host = host.substring(0, host.indexOf(","));
				int port = Integer.parseInt(lineArray[3]);
				double x = Double.parseDouble(lineArray[4]);
				double y = Double.parseDouble(lineArray[5]);
				Node node = new Node(nodeID, host, port, x, y);
				if (lineArray.length > 7) {
					for (int i = 7; i < lineArray.length; i++) 
						node.addLink(Integer.parseInt(lineArray[i]));				
				}
				nodesMap.put(nodeID, node);
			}						
		}
			
		return nodesMap;
	}
	
	public ConcurrentSkipListMap<Integer, Node> writeConfigFile(Node updatedNode) {
//		SortedMap<Integer, Node> nodesMap = readConfigFile();
		ConcurrentSkipListMap<Integer, Node> nodesMap = readConfigFile();
		updatedNode = VehicleHandler.updateNeighborsFromFile(updatedNode, nodesMap);
//		for (SortedMap.Entry<Integer, Node> entry: nodesMap.entrySet()) {	
//			int i = entry.getKey();
//			if ((i != updatedNode.getNodeID()) && (!updatedNode.getLinks().contains(i))) {
//				if (VehicleHandler.inTransmissionRange(entry.getValue(), updatedNode))
//					updatedNode.addLink(i);
//			}
//		}
//		System.out.println("@@@@@@@@@@@" + updatedNode);
		
		nodesMap.put(updatedNode.getNodeID(), updatedNode);
		try {
			PrintWriter pw = new PrintWriter(filename);
			rwLock.writeLock().lock();
			nodesMap.forEach((k, v) -> { pw.println(v.toString());
//				System.out.println(v.toString());
			});
			pw.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			rwLock.writeLock().unlock();
		}
		return nodesMap;
	}
	
	public void reset() {
//		File configFile = new File(filename);
//		if (configFile.exists()) {
//			configFile.delete();
//		}
		try {
			FileWriter fw = new FileWriter(filename);
			fw.close();			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public int newNodeID(String filename) {
//		SortedMap<Integer, Node> existingNodes = readConfigFile();
		ConcurrentSkipListMap<Integer, Node> existingNodes = readConfigFile();
		int numNodes = existingNodes.size();
		return numNodes + 1;
	}
	
}
