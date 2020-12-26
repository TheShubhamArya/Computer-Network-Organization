//Shubham Arya 1001650536
import java.util.Collections;
import java.io.IOException;
import java.util.TreeMap;
import java.util.TreeSet;

public class Node {
	public static final int Infinity = 16;
	public static final String divider = "\n-----------------\n";
	public int nodeNum;
	public boolean change = true;
	public TreeMap<Integer, TreeMap<Integer, Integer>> dvt; //distance vector table
	public boolean rcvd = false;
	
	//Constructor for Node that adds the nodes to the distance vector table
	public Node(int node, TreeSet<Integer> nodeNumbers) {
		this.nodeNum = node;
		dvt = new TreeMap<Integer, TreeMap<Integer, Integer>>();
		dvt.put(node, new TreeMap<Integer, Integer>());
		for(int n : nodeNumbers) {
			if(n == this.nodeNum) {
				dvt.get(this.nodeNum).put(n, 0);
			} else {
				dvt.get(this.nodeNum).put(n, Infinity);
			}
		}
	}
	
	//Runs the Bellman-Ford algorithm on the routing table. Compares the new path and the old path
	//and if the newer path is lesser cost, then adds it to the distance vector table.
	//https://www.geeksforgeeks.org/bellman-ford-algorithm-dp-23/
	public void bfAlgo() {
		for(int dest : dvt.get(this.nodeNum).keySet()) {
			TreeMap<Integer, Integer> nodeDistances = new TreeMap<Integer, Integer>();
			for(int n : dvt.keySet()) {
				nodeDistances.put(dvt.get(this.nodeNum).get(n) + dvt.get(n).get(dest), n);
			}
			int newLeastCost = Collections.min(nodeDistances.keySet());
			int oldLeastCost = dvt.get(this.nodeNum).get(dest);
			if(oldLeastCost>newLeastCost) {
				change = true;
				dvt.get(this.nodeNum).put(dest, newLeastCost);
			} else if (newLeastCost<oldLeastCost) {
				change = true;
				dvt.get(this.nodeNum).put(dest, oldLeastCost);
			}else {
				//if it is equal then no change and it stays as it is
			}
		}
	}

	//Adds new row in the distance vector table. It then adds all the 2nd columns 
	//and then sets the distance for the adjacent node's node in the distance vector table
	public void addDist(int neighbour, int distance, TreeSet<Integer> ids) {
		dvt.put(neighbour, new TreeMap<Integer, Integer>());
		for(int node : ids) {
			if (node != neighbour){
				dvt.get(neighbour).put(node, Infinity); //for destinations y in Dx(y) = c(x,y). If y is not a neighbor then c(x,y) = ∞
			} else {
				dvt.get(neighbour).put(node, 0);
			}
		}
		dvt.get(this.nodeNum).put(neighbour, distance);
	}

	//This receives the row from the next node and replaces that row in the routing table. 
	//It sets the rcvd flag to true to indicate that the row was received
	public void receiveMessage(int senderID, TreeMap<Integer, Integer> tableEntry) {
		rcvd = true;
		dvt.put(senderID, (TreeMap<Integer, Integer>) tableEntry.clone()); 
	}
	
	//Convert routing table into string representation
	public String toString() {
		String output = "Node: "+this.nodeNum+"\n\n=>";
		//prints the destination node like 1,2,3...
		for(int dest : dvt.get(this.nodeNum).keySet()) 
		{
			output = output + String.format("%2d|", dest);
		}
		output = output + divider;
		//The two for loops print the link cost between the two nodes.
		for(int start : dvt.keySet()) {
			output = output + start + "|";
			for(int dest : dvt.get(start).keySet()) {
				output = output + String.format("%2d|", dvt.get(start).get(dest));
			}
			output = output + divider;
		}
		return output;
	}
}
