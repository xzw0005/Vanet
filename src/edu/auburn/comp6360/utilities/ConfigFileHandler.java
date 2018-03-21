package edu.auburn.comp6360.utilities;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import edu.auburn.comp6360.application.Node;



public class ConfigFileHandler {
	
	private String filename;
	private static ReadWriteLock rwLock = new ReentrantReadWriteLock();
	
	public ConfigFileHandler(String filename) {
		this.filename = filename;
	}

	
	public SortedMap<Integer, Node> readConfigFile() {
		SortedMap<Integer, Node> nodesMap = new TreeMap<Integer, Node>();
		List<String> lines = null;
		try {
			rwLock.readLock().lock();
			lines = Files.readAllLines(Paths.get(filename), StandardCharsets.UTF_8);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			rwLock.readLock().unlock();
		}
		
		for (String line : lines) {
			//line = "Node 2 tux055, 10011 80 120 links 1 3 4";
			String[] lineArr = line.split(" ");
			int nodeID = Integer.parseInt(lineArr[1]);
			String host = lineArr[2].trim().substring(0, 6);
			int port = Integer.parseInt(lineArr[3]);
			double x = Double.parseDouble(lineArr[4]);
			double y = Double.parseDouble(lineArr[5]);
			Node node = new Node(nodeID, host, port, x, y);
			if (lineArr.length > 7) {
				for (int i = 7; i < line.length(); i++) 
					node.addLink(Integer.parseInt(lineArr[i]));				
			}
			nodesMap.put(nodeID, node);
		}
		return nodesMap;
	}
	
	public SortedMap<Integer, Node> writeConfigFile(Node updatedNode) {
		SortedMap<Integer, Node> nodesMap = readConfigFile();
		nodesMap.put(updatedNode.getNodeID(), updatedNode);
		try {
			FileWriter fw = new FileWriter(filename);	
			rwLock.writeLock().lock();
			nodesMap.forEach((k, v) -> {
				try {
					fw.write(v.toString());
				} catch (IOException e) {
					e.printStackTrace();
				}
			}); 
			fw.close();
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
		SortedMap<Integer, Node> existingNodes = readConfigFile();
		int numNodes = existingNodes.size();
		return numNodes + 1;
	}
	
}
