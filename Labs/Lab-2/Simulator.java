//Shubham Arya 1001650536
import java.io.IOException;
import java.util.Scanner;
import java.io.*; 

public class Simulator {

	public static final int Infinity = 16;
	static Scanner read = new Scanner(System.in);
	public static void main(String[] args){
		int lapse = 0;
		Network network = null;
		boolean stabilized = false;
		boolean flag = true;
		
		try {
			//reads the filename that the user enters and constructs a Network
			System.out.print("Enter filename- ");
			network = new Network(read.nextLine());
			
			//loops until user breaks it with exit character
			while (true) {
				System.out.println(network);
				if(network.isSteady()) {
					System.out.print("The stable state reached at cycle "+network.time+"\n");
					flag = false;
					if(stabilized) {
						stabilized = false;
						System.out.println(lapse+" cycles after stabilization was started");
					}
				}
				printNextSteps(flag);
				String input = read.nextLine();
				System.out.println("****************************************************");

				/*based on the user input, it either goes to the next cycle, or stabilizes, or adjust cost
				of link or exits the program.*/
				switch (input){
					case "1": if(!network.isSteady()){
								network.steadied();
							}
						    break;
					case "2": if(!network.isSteady()){
								int start = network.time; //time before stabilize
								network.steadied(); 
								lapse = network.time - start; 
								stabilized = true;
							}
							break;
					case "3": System.out.print("Enter node1 node2 linkCost: ");
							  String data[] = read.nextLine().split(network.space);
							if(!network.isSteady()){
								network.steadied();
							}
							flag = true;
							break;
					default: //Close the scanner, break the while loop and ends the program
							read.close();
							break;
				}
			}

		} catch (Exception e) {
			System.out.println(e.getLocalizedMessage());
		}
	}

	//This function is to print the next steps for the user
	static void printNextSteps(boolean flag) {
		System.out.println("****************************************************");
		if (flag) {
			System.out.println("1 to run each step in algorithm");
			System.out.println("2 to display number of cycles to reach stable state");
		} else {
			System.out.println("*1 will QUIT the program*");
			System.out.println("*2 will QUIT the program*");
		}
		System.out.println("3 to adjust cost of link ");
		System.out.println("Press any other key to exit");
		System.out.print("Enter here: ");
	}
}
