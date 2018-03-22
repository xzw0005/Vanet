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
			String[] lineArray = line.split(" ");
			int nodeID = Integer.parseInt(lineArray[1]);
			String host = lineArray[2].trim();
			host = host.substring(0, host.indexOf(",") - 1);
			int port = Integer.parseInt(lineArray[3]);
			double x = Double.parseDouble(lineArray[4]);
			double y = Double.parseDouble(lineArray[5]);
			Node node = new Node(nodeID, host, port, x, y);
			if (lineArray.length > 7) {
				for (int i = 7; i < line.length(); i++) 
					node.addLink(Integer.parseInt(lineArray[i]));			
			}
			nodesMap.put(nodeID, node);
		}
		return nodesMap;
	}
	
	public SortedMap<Integer, Node> writeConfigFile(Node updatedNode) {
		SortedMap<Integer, Node> nodesMap = readConfigFile();
		nodesMap.put(updatedNode.getNodeID(), updatedNode);
		try {
			PrintWriter pw = new PrintWriter(filename);
			rwLock.writeLock().lock();
			nodesMap.forEach((k, v) -> { pw.println(v.toString());});
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
		SortedMap<Integer, Node> existingNodes = readConfigFile();
		int numNodes = existingNodes.size();
		return numNodes + 1;
	}
	
}
