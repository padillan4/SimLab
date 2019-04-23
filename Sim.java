package simLab;

import java.util.Scanner;

import simLab.Event.Status;

import java.util.ArrayList;

// File to execute
public class Sim {
	static ArrayList<Double> timeArrival;

    /* Declare non-simlib global variables. */

    static int   minTellers, maxTellers, numTellers, shortestLength, shortestQueue;
    static double lengthDoorsOpen;

    static final int EVENT_ARRIVAL     = 1;  // Event type for arrival of a customer
    static final int EVENT_DEPARTURE   = 2;  // Event type for departure of a customer
    static final int EVENT_CLOSE_DOORS = 3;  // Event type for closing doors at 5 P.M.
    
    static final int SAMPST_DELAYS     = 1;  // sampst variable for delays in queue(s)

    static final int STREAM_INTERARRIVAL = 1;
    static final int STREAM_SERVICE = 2;

    static double meanInterArrival;
    static double meanService;
    static int nDelaysRequired;

    static int nEvents;
    static int serverStatus;
    static ArrayList numInQ;
    static double timeLastEvent;
    static int nCustsDelayed;
    static double totalOfDelays; 
    static double minDelay;
    static double maxDelay;
    static double delays;

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
			
			/** Default Input
			 * Mean interarrival time      1.000 minutes
			Mean service time           0.500 minutes
			Number of customers          1000
			 */
			
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
			nEvents = 2;
			//Gather input data
			/**System.out.println("You have selected multiteller banking simulation.");
			System.out.print("Enter minimum number of tellers(int): ");
			minTellers = sc.nextInt();
			System.out.print("Enter maximum number of tellers(int): ");
			maxTellers = sc.nextInt();
			System.out.print("Enter mean arrival time(double): ");
			meanInterArrival = sc.nextDouble();
			System.out.print("Enter mean service time(double): ");
			meanService = sc.nextDouble();
			System.out.print("Enter total time bank is open in hours(double): ");
			lengthDoorsOpen = sc.nextDouble();
			System.out.println();**/
			sc.close();
			
			minTellers = 4;
	        maxTellers = 7;
	        meanInterArrival = 1.0;
	        meanService = 4.5;
	        lengthDoorsOpen = 8.0;    // in hours
			// Run the simulation while more delays are still needed 
	        for (numTellers = minTellers; numTellers <= maxTellers; numTellers++) 
	        {
	        	numInQ = new ArrayList<Double>();
	            Event.Initialize();

	            // Schedule the first arrival. 
	            double time = SimLib_Random.Expon(meanInterArrival, STREAM_INTERARRIVAL);
	            Event ev = new Event(time, EVENT_ARRIVAL);
	            Event.EventSchedule(ev);
	            
	            // Schedule the bank closing, in minutes
	            ev = new Event(60.0 * lengthDoorsOpen, EVENT_CLOSE_DOORS);
	            Event.EventSchedule(ev);
	            
	            while (Event.GetQueueSize(25) != 0)
	            {
	                Event.Timing();                // Determine the next event.
	                Event.UpdateTimAvgStats();		// Update time-average statistical accumulators.

	                switch (Event.GetNextEventType())   // Invoke the appropriate event function.
	                {
	                    case EVENT_ARRIVAL:
	                       Arrive();
	                    break;
	                    case EVENT_DEPARTURE:
	                       Depart(Event.GetTellerNumber());
	                    break;
	                    case EVENT_CLOSE_DOORS:
	                       Event.EventCancel(EVENT_ARRIVAL); //Cancel the only arrival event in the event queue
	                    break;
	                }
	            }
	            Report();
	        }
		}
		else {
			System.out.println("Invalid Simulation Selection.");
			sc.close();
		}
	}
	
	 /*****************************************************************************************************************************
     * 
     * Arrive
     * 
     *  queues (nTellers + 1) to (nTellerss + nTellers)  are the actual tellers (empty queue teller is idle, one in queue teller is busy
     *  queues (1) to (nTellers) are the actual lines at the tellers
     *  
     */
	static void Arrive()
    {   
        // Schedule the next arrival. 
        Event ev = new Event(Event.GetSimTime() + SimLib_Random.Expon(meanInterArrival, STREAM_INTERARRIVAL), EVENT_ARRIVAL);
        Event.EventSchedule(ev);

        for (int teller = 0; teller < numTellers; teller++) 
        {
            if (Event.GetStatus(teller) == 0)  // Is the teller idle? if so, this customer has 0 delay
            {
            	Event.Sampst(0.0, SAMPST_DELAYS);
                
                // Make this teller busy (attributes are irrelevant). 
                Event.SetStatus(teller, Event.Status.BUSY);

                // Schedule a service completion. 

                ev = new Event(Event.GetSimTime() + SimLib_Random.Expon(meanService, STREAM_SERVICE), EVENT_DEPARTURE, teller);
                Event.EventSchedule(ev);
                return;
            }
        }

        // All tellers are busy, so find the shortest queue (leftmost shortest in case of ties).

        int shortestLength = Event.GetQueueSize(0);
        int shortestQueue = 0;

        for (int teller = 1; teller < numTellers; teller++)
        {
            if (Event.GetQueueSize(teller) < shortestLength) {
                shortestLength = Event.GetQueueSize(teller);
                shortestQueue  = teller;
            }
        }  

        // Place the customer at the end of the leftmost shortest queue. 
        ev = new Event(Event.GetSimTime(), 0); // fake event for waiting in line
        Event.InsertInQueue(ev, Event.Order.LAST, shortestQueue);
    }

    /*****************************************************************************************************************************
     * 
     * Depart
     * 
     *  queues (nTellers + 1) to (nTellerss + nTellers)  are the actual tellers (empty queue teller is idle, one in queue teller is busy
     *  queues (1) to (nTellers) are the actual lines at the tellers
     */
    static void Depart(int teller)
    {   
        if (Event.GetQueueSize(teller) == 0)  // Is there a line at this teller??
        {
            // There was no line at this teller, and since we are processing departure this teller goes idle
            Event.SetStatus(teller, Event.Status.IDLE);
        }
        else
        {
            // There is a line at this teller, so move the first one is line, and then schedule when this customer will leave the teller
            Event evRemoved = ((Event)Event.RemoveFromQueue(Event.Order.FIRST, teller));
            
            double eTime = evRemoved.GetEventTime();
            double delay = Event.GetSimTime() - evRemoved.GetEventTime();  // total for customer going from front of line to teller
            
            Event.Sampst(delay, SAMPST_DELAYS);
            
            // Create the depart event for the customer now being served by teller number (teller)
            Event ev = new Event(Event.GetSimTime() + SimLib_Random.Expon(meanService, STREAM_SERVICE), EVENT_DEPARTURE, teller);
            Event.EventSchedule(ev);
        }

        // Let a customer from the end of another queue jockey to the end of this queue, if possible. 
        Jockey(teller);
    }

    /*****************************************************************************************************************************
     * 
     * Jockey
     * 
     * Jockey a customer to the end of queue "teller" from the end of another queue, if possible.
     *  
     *  queues (nTellers + 1) to (nTellerss + nTellers)  are the actual tellers (empty queue teller is idle, one in queue teller is busy
     *  queues (1) to (nTellers) are the actual lines at the tellers
     */
    static void Jockey(int teller)
    {
        int jumper, minDistance, ni, nj, otherTeller, distance;

        //Find the number, jumper, of the queue whose last customer will jockey to
        // queue or teller "teller", if there is such a customer. 

        jumper       = 0;
        minDistance = 1000;
        ni           = Event.GetQueueSize(teller) + Event.GetStatus(teller);

        // Scan all the lines at the tellers
        for (otherTeller = 0; otherTeller < numTellers; otherTeller++)
        {
            int lineSize     = Event.GetQueueSize(otherTeller);
            int tellerStatus = Event.GetStatus(otherTeller);
            nj = lineSize + tellerStatus;
            distance = Math.abs(teller - otherTeller);
            
            // Check whether the customer at the end of queue other_teller qualifies
            // for being the jockeying choice so far. 
            if (otherTeller != teller && nj > ni + 1 && distance < minDistance) 
            {
                // The customer at the end of queue other_teller is our choice so
                // far for the jockeying customer, so remember his queue number and
                // its distance from the destination queue. 

                jumper       = otherTeller;
                minDistance = distance;
            }   
        } 

        // Check to see whether a jockeying customer was found
        if (jumper > 0) 
        {
            Event evRemoved = ((Event)Event.RemoveFromQueue(Event.Order.LAST, jumper));
            double eTime = evRemoved.GetEventTime();
            
            if (Event.GetStatus(teller) > 0)
            {
                // The teller of this new queue is busy, so place the jumper customer at the end of this queue.
                Event.InsertInQueue(evRemoved, Event.Order.LAST, teller);
            }
            else 
            {
                // The teller of his new queue is idle, so tally the jockeying customer's delay, make 
                // the teller busy, and start service.
                
                double delay = Event.GetSimTime() - evRemoved.GetEventTime();  // total delay from back of line to switch line
                Event.Sampst(delay, SAMPST_DELAYS);
            
                Event.SetStatus(teller, Event.Status.BUSY);
                Event ev = new Event(Event.GetSimTime() + SimLib_Random.Expon(meanService, STREAM_SERVICE), EVENT_DEPARTURE, teller);
                Event.EventSchedule(ev);
            }
        }
    }

    /*****************************************************************************************************************************
      * Report
    */
    static void Report()
    {
        double avgNumInQ = 0.0;
        double avgDelays = 0.0;
        
        for(int i = 0; i< numInQ.size(); i++) {
        	avgNumInQ += (double)numInQ.get(i);
        }
       avgDelays = delays/Event.GetSimTime();
        
        System.out.format("%n%nWith%2d tellers, average number in queue = %10.3f", numTellers, avgNumInQ);
        System.out.format("%n%nDelays in queue, in minutes:%n%n");
        System.out.println(" sampst                         Number\r\n" + 
        		"variable                          of\r\n" + 
        		" number       Average           values          Maximum          Minimum\r\n" + 
        		"________________________________________________________________________\n");
        System.out.format("    %d          %.3f             %d             %.3f            %.3f  %n", SAMPST_DELAYS, avgDelays, nCustsDelayed, maxDelay, minDelay);
        System.out.println("________________________________________________________________________");
    }
}
