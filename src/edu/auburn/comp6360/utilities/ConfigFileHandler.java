package edu.auburn.comp6360.utilities;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import edu.auburn.comp6360.application.Node;



public class ConfigFileHandler {
	
	private String filename;
	
	private List<Node> allNodes;
	
	private static ReadWriteLock rwLock = new ReentrantReadWriteLock();
	
	public ConfigFileHandler(String filename) {
		this.filename = filename;
	}

	
	public static List<Node> readConfigFile(String filename) {
		List<Node> allNodes = new ArrayList<Node>();		
		List<String> lines = null;
		try {
			rwLock.readLock().lock();
			lines = Files.readAllLines(Paths.get(filename), StandardCharsets.UTF_8);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			rwLock.readLock().unlock();
		}
		
		Map<Integer, List<Integer>> linksMap = new HashMap<Integer, List<Integer>>();
		Map<Integer, Node> nodeMap = new HashMap<Integer, Node>();
		
		for (String line : lines) {
			//line = "Node 2 tux055, 10011 80 120 links 1 3 4";
			String[] lineArr = line.split(" ");
			int nodeID = Integer.parseInt(lineArr[1]);
			String host = lineArr[2].trim().substring(0, 6);
			int port = Integer.parseInt(lineArr[3]);
			double x = Double.parseDouble(lineArr[4]);
			double y = Double.parseDouble(lineArr[5]);
			List<Integer> links = new ArrayList<Integer>();
			for (int i = 7; i < line.length(); i++) {
				links.add(Integer.parseInt(lineArr[i]));
			}
			
			Node thisNode = new Node(nodeID, host, port, x, y);
			allNodes.add(thisNode);
			linksMap.put(nodeID, links);
			nodeMap.put(nodeID, thisNode);
		}
		for (Node node : allNodes) {
			for (Integer link : linksMap.get(node.getNodeID())) {
				node.addNeighbor(nodeMap.get(link));
			}
		}
		return allNodes;
	}
	
	public void writeConfigFile(String filename, Node updatedNode) {
		List<Node> allNodes = this.readConfigFile(filename);

		
		try {
			rwLock.writeLock().lock();
		} finally {
			rwLock.writeLock().unlock();
		}
	}
	
	public static void reset(String filename) {
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
	
	public static int newNodeID(String filename) {
		List<Node> allNodes = readConfigFile(filename);
		int numNodes = allNodes.size();
		return numNodes + 1;
	}
	
}
