//Shubham Arya 1001650536
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.TreeMap;
import java.util.TreeSet;

public class Network {
	public static final int Infinity = 16;
	public final String space = " ";
	public TreeMap<Integer,Node> nodes;
	public TreeSet<Integer> nodeNumbers;
	public int time;

	//Sets the variables for the class Network
	public Network(String filename)  {
		time = 0;
		nodes = new TreeMap<Integer, Node>();
		try {
			nodeNumbers = this.getIDsFromFile(filename);
			for(int node : nodeNumbers) {
				nodes.put(node, new Node(node, nodeNumbers));
			}

			BufferedReader input = new BufferedReader(new FileReader(filename));
			String line = null;
			while((line = input.readLine()) != null) { 
				String[] data = line.split(space);
				nodes.get(Integer.parseInt(data[0])).addDist(Integer.parseInt(data[1]), Integer.parseInt(data[2]), nodeNumbers);
				nodes.get(Integer.parseInt(data[1])).addDist(Integer.parseInt(data[0]), Integer.parseInt(data[2]), nodeNumbers);
			}
			input.close();
		} catch(Exception e) {
			System.out.println("Error in file");
		}
	}

	//Run the code until the nodes reach a steady state.  For each node, it sends message to the next neighboring node. it makes
	//sure that it doesn't send message to self because that is not required. It sends all the other nodes it's own tables and 
	//they are marked as received in the receive message. Then all the nodes that received the message has to run through the 
	// Bellman Ford algorithm until all the nodes are steady.
	public void steadied() {
		while(!isSteady()) {
			for(int nodeID : nodes.keySet()) {
				Node currentt = nodes.get(nodeID);
				if(currentt.change) {
					for(int next : currentt.dvt.keySet()) {
						if(next != nodeID) {
							nodes.get(next).receiveMessage(nodeID, currentt.dvt.get(nodeID));
						}
					}
					currentt.change = false;
				}
			}
			for(int node : nodes.keySet()) {
				if(nodes.get(node).rcvd) {
					nodes.get(node).bfAlgo();
				}
			}
			time++;
		}
	}
	
	//isSteady returns a boolean and tells whether a function is steady or not. If a node has changes then it is not steady
	//if all nodes do not have changes, then it returns true, i.e, steady.
	public boolean isSteady() {
		for(int node : nodes.keySet()) {
			if(nodes.get(node).change) {
				return false;
			}
		}
		return true;
	}
	
	//Convert the node to a string represntation.
	public String toString() {
		String result = "\nCycle: "+time+"\n\n";
		for(int nodeID : nodes.keySet()) {
			result += nodes.get(nodeID).toString()+"\n";
		}
		result += "Cycle "+time+" completed \n";
		return result;
	}

	//This opens the input file and reads the data. This then takes the data read, converts them into
	//integer datatype and put then into a tree as a node.
	TreeSet<Integer> getIDsFromFile(String filename) throws Exception {
		TreeSet<Integer> nodes = new TreeSet<Integer>();
		BufferedReader input = new BufferedReader(new FileReader(filename));	
		String line = null;

		while((line = input.readLine()) != null) { 
			String[] data = line.split(space);
			addNodesFromFileToTree(data[0],nodes);
			addNodesFromFileToTree(data[1],nodes);
		}
		input.close();
		return nodes;
	}

	//This add a node to the three from the file only if the node has an integer value of the string
	void addNodesFromFileToTree(String n,TreeSet<Integer> nodes){
		if(!nodes.contains(Integer.valueOf(n))) {
			nodes.add(Integer.valueOf(n));
		}
	}
}
