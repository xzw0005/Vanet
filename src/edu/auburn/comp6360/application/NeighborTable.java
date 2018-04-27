package edu.auburn.comp6360.application;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.ConcurrentSkipListSet;

import java.util.Map.Entry;
import java.util.Set;

public class NeighborTable {
	
	private int neighborhoodSequenceNumber;
	private ConcurrentSkipListMap<Integer, String> oneHopNeighbors;
	private ConcurrentSkipListMap<Integer, ConcurrentSkipListSet<Integer>> twoHopNeighbors;
	private ConcurrentSkipListMap<Integer, Integer> twoHopNeighborsThroughMPR;
	private ConcurrentSkipListSet<Integer> mprSet;
	private ConcurrentSkipListSet<Integer> mprSelectorTable;
	private ConcurrentSkipListSet<Integer> neighborSet;

	public NeighborTable() {
		this.neighborhoodSequenceNumber = 0;
		this.oneHopNeighbors = new ConcurrentSkipListMap<Integer, String>();
		this.twoHopNeighbors = new ConcurrentSkipListMap<Integer, ConcurrentSkipListSet<Integer>>();
		this.twoHopNeighborsThroughMPR = new ConcurrentSkipListMap<Integer, Integer>();
		this.mprSelectorTable = new ConcurrentSkipListSet<Integer>();
		this.neighborSet = new ConcurrentSkipListSet<Integer>();
	}
	
	public ConcurrentSkipListSet<Integer> getNeighborSet() {
		return neighborSet;
	}
	
	public ConcurrentSkipListSet<Integer> getMprSelectorTable() {
		return mprSelectorTable;
	}

	public ConcurrentSkipListMap<Integer, String> getOneHopNeighbors() {
		return oneHopNeighbors;
	}

	public ConcurrentSkipListMap<Integer, ConcurrentSkipListSet<Integer>> getTwoHopNeighbors() {
		return twoHopNeighbors;
	}
	
	public String getLinkStatus(int nid) {
		return oneHopNeighbors.get(nid);
	}
	
	public ConcurrentSkipListSet<Integer> getAccessThrough(int twoHopNb) {
		return twoHopNeighbors.get(twoHopNb);
	}
	
	public void setLinkStatus(int nid, String status) {
		oneHopNeighbors.put(nid, status);
		updateNeighborSet();
	}
	
	/*
	 * Remove source from the one hop neighbors
	 * Also, the two hop neighbors need to be updated
	 * as well as MPRs
	 */
	public boolean unlink(int source) {
		if (!oneHopNeighbors.containsKey(source))
			return false;
		oneHopNeighbors.remove(source);
		updateNeighborSet();
		for (int twoHopNb : twoHopNeighbors.keySet()) {
			if (twoHopNeighbors.get(twoHopNb).contains(source)) {
				twoHopNeighbors.get(twoHopNb).remove(Integer.valueOf(source));
			}
		}
		cleanTwoHopNeighbors();
		updateMPRs();
		++neighborhoodSequenceNumber;
		return true;
	}
	
	public void setAccessThrough(int twoHopNb, ConcurrentSkipListSet<Integer> accessThroughSet) {
		twoHopNeighbors.put(twoHopNb, accessThroughSet);
	}
	
	public int getNeighborhoodSequenceNumber() {
		return neighborhoodSequenceNumber;
	}

	public ConcurrentSkipListSet<Integer> getMPRs() {
		return this.mprSet;
	}
	
	
	public void updateNeighborSet() {
		ConcurrentSkipListSet<Integer> biLinks = new ConcurrentSkipListSet<Integer>();
		for (int nid : this.oneHopNeighbors.keySet()) {
			String linkStatus = oneHopNeighbors.get(nid);
			if (linkStatus.equalsIgnoreCase("BI") || linkStatus.equalsIgnoreCase("MPR"))
				biLinks.add(nid);
		}
		this.neighborSet =  biLinks;
	}
	
	public boolean isOneHopNeighbor(int source) {
		return oneHopNeighbors.containsKey(source);
	}
	
	public boolean isTwoHopNeighbor(int source) {
		return twoHopNeighbors.containsKey(source);
	}
	
	public void removeTwoHopNeighbor(int source) {
		twoHopNeighbors.remove(source);
	}
	
	/*
	 * Update two hop neighbors upon arriving of HELLO message
	 * source: the node ID who sends the HELLO message
	 * neighborsOfSource: source's one hop neighbors, contains the link status
	 * 
	 */
	public boolean updateTwoHopNeighbors(int selfId, int source, ConcurrentSkipListMap<Integer, String> neighborsOfSource, boolean updated) {
//		boolean updated = false;
		
		// Traverse for each two hop neighbors
		// if source if previously recorded as an access through for a two hop neighbors twoHopNb
		// but if now the link between the source and twoHopNb is no longer bidirectional,
		// we won't record the source as twoHopNb's access-through
		for (int twoHopNb : twoHopNeighbors.keySet()) {
			if (twoHopNeighbors.get(twoHopNb).contains(source)) { 
				if ((!neighborsOfSource.containsKey(twoHopNb)) || (neighborsOfSource.get(twoHopNb).equalsIgnoreCase("UNI"))) {
					twoHopNeighbors.get(twoHopNb).remove(Integer.valueOf(source));
					updated = true;
				}
			}
		}
		
		// Traverse for each of the source's one hop neighbor i, which is candidate for this vehicle's two hop neighbor
		// if i is already a two hop neighbor of this vehicle, update its access through set by adding source if necessary
		// otherwise, add it into two hop neighbors. 
		for (int nid : neighborsOfSource.keySet()) {
			if (nid == selfId) {
				if (neighborsOfSource.get(selfId).equals("MPR")) {
					if (!mprSelectorTable.contains(source)) {
						System.out.println(source + " is not in MPR selector table.");
						mprSelectorTable.add(source);
						this.printMprSelectorTable(selfId);
						this.printOneHopNeighbors(selfId);
						this.printTwoHopNeighbors(selfId);
					}
				}
				else {
					if (mprSelectorTable.contains(source)) {
						System.out.println(source + " is in MPR selector table.");
						mprSelectorTable.remove(source);						
						this.printMprSelectorTable(selfId);
						this.printOneHopNeighbors(selfId);
						this.printTwoHopNeighbors(selfId);					}
				}
			}
			else if (neighborsOfSource.get(nid).equalsIgnoreCase("BI") || neighborsOfSource.get(nid).equalsIgnoreCase("MPR")) {
				ConcurrentSkipListSet<Integer> accessThroughSet = new ConcurrentSkipListSet<Integer>();
				if (twoHopNeighbors.containsKey(nid))
					accessThroughSet = twoHopNeighbors.get(nid);
				boolean added = accessThroughSet.add(source);
				if (added)
					updated = true;
				if (!this.isOneHopNeighbor(nid) && !this.isTwoHopNeighbor(nid)) {
					System.out.println("Node " + nid + " is not in 1-hop neighbor table or 2-hop neighor table");
					this.printOneHopNeighbors(selfId);
					this.printTwoHopNeighbors(selfId);
					twoHopNeighbors.put(nid, accessThroughSet);
				} 
			}
		}
		cleanTwoHopNeighbors();
		if (updated) {
			updateMPRs();
			++neighborhoodSequenceNumber;
		}
		return updated;
	}

	/*
	 * Remove those whose access through sets are empty as two hop neighbors
	 */
	public void cleanTwoHopNeighbors() {	
		Set<Entry<Integer, ConcurrentSkipListSet<Integer>>> set = twoHopNeighbors.entrySet();
		Iterator<Entry<Integer, ConcurrentSkipListSet<Integer>>> iter = set.iterator();
		while (iter.hasNext()) {
			Entry<Integer, ConcurrentSkipListSet<Integer>> entry = iter.next();
			if (entry.getValue().isEmpty())
				iter.remove();
		}
//		// Another Approach		
//		ArrayList<Integer> toRemove = new ArrayList<Integer>();
//		for (int twoHopNb : twoHopNeighbors.keySet()) {
//			if (twoHopNeighbors.get(twoHopNb).isEmpty()) 
//				toRemove.add(twoHopNb);
//		}
//		for (int i : toRemove)
//			twoHopNeighbors.remove(i);
	}
	
	
	/* 
	 * Use heuristic to update MPR set
	 * First sort the two hop neighbors by the number of one hop neighbors (called access-through) they connected with (must be bidirectional)
	 * Then the one with the fewest links with the access-throughs chooses its MPR first, then the one with fewest access-throughs
	 * And so on
	 */
	public void updateMPRs() {
		twoHopNeighborsThroughMPR = new ConcurrentSkipListMap<Integer, Integer>();
		mprSet = new ConcurrentSkipListSet<Integer>();
		// Record the size of each node's accessThroughSet
		Map<Integer, Integer> accessThroughSizes = new HashMap<Integer, Integer>();
		for (int twoHopNb : twoHopNeighbors.keySet()) {
			int size = twoHopNeighbors.get(twoHopNb).size();
			accessThroughSizes.put(twoHopNb, size);
		}
		// Sort by value
		Set<Entry<Integer, Integer>> set = accessThroughSizes.entrySet();
		ArrayList<Entry<Integer, Integer>> list = new ArrayList<Entry<Integer, Integer>>(set);
		Collections.sort(list, new Comparator<Map.Entry<Integer, Integer>>() {
			public int compare(Map.Entry<Integer, Integer> o1, Map.Entry<Integer, Integer> o2) {
				return (o1.getValue()).compareTo(o2.getValue());
			}
		} );
		// Heuristics for MPR selection
		for (Entry<Integer, Integer> entry : list) {
			int twoHopNb = entry.getKey();
			ConcurrentSkipListSet<Integer> accessThroughSet = twoHopNeighbors.get(twoHopNb);
			boolean addMPR = true;
			for (int mpr : mprSet) {
				if (accessThroughSet.contains(mpr)) {
					this.twoHopNeighborsThroughMPR.put(twoHopNb, mpr);
					addMPR = false;
				}
			}
			if (addMPR) {
				int mpr = accessThroughSet.first();
				mprSet.add(mpr);
				this.twoHopNeighborsThroughMPR.put(twoHopNb, mpr);
			}
		}
		// Update the one hop neighbors by labeling MPRs
		for (int nb : this.oneHopNeighbors.keySet()) {
			if (mprSet.contains(Integer.valueOf(nb))) {
				if (this.oneHopNeighbors.get(nb).equals("BI"))
					this.setLinkStatus(nb, "MPR");
			} else {
				if (this.oneHopNeighbors.get(nb).equals("MPR"))
					this.setLinkStatus(nb, "BI");
			}
		}
//		for (int mpr : mprSet) {
//			this.setLinkStatus(mpr, "MPR");
//		}
	}
	
	public void printMprSelectorTable(int selfId) {
		System.out.print(selfId + "'s MPR Selector Table:  ");
		for (int i : getMprSelectorTable()) {
			System.out.print(i + ";\t");
		}
		System.out.println();
	}
	
	public void printOneHopNeighbors(int selfId) {
		System.out.println(selfId + "'s 1-hop neighbors:  ");
		System.out.println("--------------------");
		System.out.println("| Node ID | Status |");
		System.out.println("--------------------");
		for (int i : this.oneHopNeighbors.keySet()) {
			System.out.println("|    " + i + "    |   " +  this.oneHopNeighbors.get(i) + "   |");
		}
		System.out.println("--------------------");
	}
	
	public void printTwoHopNeighbors(int selfId) {
		System.out.println(selfId + "'s  2-hop neighbors:  ");
		System.out.println("--------------------");
		System.out.println("| Node ID | AccThr |");
		System.out.println("--------------------");
		for (int i : this.twoHopNeighborsThroughMPR.keySet()) {
			System.out.println("|    " + i + "    |   " +  this.twoHopNeighborsThroughMPR.get(i) + "   |");
		}
		System.out.println("--------------------");
	}
	
	
}
