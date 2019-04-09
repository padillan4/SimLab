package simLab;

import java.util.Scanner;

// File to execute
public class Sim {

	public static void main(String[] args) {
		
		Scanner sc = new Scanner(System.in);
		String simSelected;
		
		// select single server queuing simulation or banking simulation
		System.out.print("Select Simulation(type single or banking): ");
		simSelected = sc.next();
		if(simSelected.toLowerCase().equals("single")) {
			//Selected Single-Server simulation
			double meanInterArrival, meanService;
			int nDelaysRequired, simCount;
			
			//Gather input data
			System.out.println("You have selected single-server simulation.\n");
			System.out.print("Enter mean arrival time(double): ");
			meanInterArrival = sc.nextDouble();
			System.out.print("Enter mean service time(double): ");
			meanService = sc.nextDouble();
			System.out.print("Enter number of customers(int): ");
			nDelaysRequired = sc.nextInt();
			System.out.print("Enter the amount of times to run the simulation(int): ");
			simCount = sc.nextInt();
			System.out.println();
			sc.close();
			
			//Run single server simulation(s)
			for(int i = 0; i < simCount; i++) {
				System.out.println("Simulation run number: " + (i+1));
				
				//Initialize simulation instance
				Single_Server simInstance = new Single_Server(meanInterArrival, meanService, nDelaysRequired);
				simInstance.Init();
				
				// Run the simulation while more delays are still needed 
				while (simInstance.GetnCustsDelayed() < nDelaysRequired)
				{
					simInstance.Timing();                // Determine the next event.
					simInstance.UpdateTimAvgStats();     // Update time-average statistical accumulators.

					switch (simInstance.GetNextEventType())   // Invoke the appropriate event function.
					{
					case 1:
						simInstance.Arrive();
						break;
					case 2:
						simInstance.Depart();
						break;
					}
				}
				
				simInstance.Report();
			}
		}
		else if(simSelected.toLowerCase().equals("banking")) {
			//Selected Banking simulation
			sc.close();
		}
		else {
			System.out.println("Invalid Simulation Selection.");
			sc.close();
		}
		
	}

}
