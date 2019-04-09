package simLab;

public class Single_Server {
	
	public enum serverState {IDLE, BUSY};
	private serverState server_state;
	
	private double meanInterArrival, meanService,timeLastEvent,simTime, total_of_delays, areaNumInQ, areaServerStatus
	, timeNextEvent[], nEvents, time_arrival[], maxD, maxT, timeInSystem[], timeOutSystem[], averageTimeSystem;
	private int nDelaysRequired, numInQ, nCustsDelayed, nextEventType, timeAverageCount, maxQ, addIn, addOut;
	
	// constructor for class Single_Server
	public Single_Server() {
		this.meanInterArrival = 0.0;
		this.meanService = 0.0;
		this.nDelaysRequired = 0;
		nEvents = 2;
	}
	// constructor for class Single_Server
	public Single_Server(double meanInterArrival, double meanService, int nDelaysRequired) {
		this.meanInterArrival = meanInterArrival;
		this.meanService = meanService;
		this.nDelaysRequired = nDelaysRequired;
		nEvents = 2;
	}
	
	//Getter for nCustsDelayed
	public int GetnCustsDelayed() {
		return nCustsDelayed;
	}
	
	//Getter for nextEventType
	public int GetNextEventType() {
		return nextEventType;
	}
	
	//Getter for nDelaysRequired
	private int GetnDelaysRequired() {
		return nDelaysRequired;
	}
	
	//Initialize simulation instance
	public void Init() {
		//initialize simulation clock
		simTime = 0.0;
		
		// initialize state variables
		server_state = serverState.IDLE;
		numInQ = 0;
		timeLastEvent = 0.0;
		
		// Initialize the statistical counters. 
		nCustsDelayed = 0;
		total_of_delays = 0.0;
		areaNumInQ = 0.0;
		areaServerStatus = 0.0;
		
		// Initialize event list.  Since no customers are present, the departure(service completion) event is eliminated from consideration.
		time_arrival = new double[100];
		timeNextEvent = new double[3];
		timeNextEvent[1] = simTime + SimLib_Random.Expon(meanInterArrival, 1);
		timeNextEvent[2] = 1.0e+30;
		timeInSystem = new double[GetnDelaysRequired()];
		timeOutSystem = new double[GetnDelaysRequired()];
		
		// Initialize time-average statistical counters.
		timeAverageCount = 0;
		
		// Initialize Average time in system.
		averageTimeSystem = 0.0;
		addIn = 0;
		addOut = 0;
		
		// Initialize Maximum queue counter
		maxQ = 0;

		// Initialize Maximum delay
		maxD = 0.0;

		// Initialize Maximum time in system
		maxT = 0.0;
	}
	
	// Timing function
	public void Timing() {
		double min_time_next_event = 1.0e+29;
		
		nextEventType = 0;

		// Determine the event type of the next event to occur. 
		for (int i = 1; i <= nEvents; i++)
		{
			if (timeNextEvent[i] < min_time_next_event)
			{
				min_time_next_event = timeNextEvent[i];
				nextEventType = i;
			}
		}
		
		// Check to see whether the event list is empty
		if (nextEventType == 0)
		{
			// The event list is empty, so stop the simulation. 
			System.out.println("Event list empty at time " + simTime);
			System.exit(1);
		}
		
		// The event list is not empty, so advance the simulation clock. 
		simTime = min_time_next_event;
	}
	
	//Arrive Function
	public void Arrive() {
		double delay;
		Addin();
		timeNextEvent[1] = simTime + SimLib_Random.Expon(meanInterArrival, 1);      // Schedule next arrival.

		if (server_state == serverState.BUSY)                                  // Check to see whether server is busy.
		{
			numInQ++;;                                             // Server is busy, so increment number of customers in queue.
			if (numInQ > 100)                                 // Check to see whether an overflow condition exists.
			{
				/* The queue has overflowed, so stop the simulation. */
				System.out.println("\nOverflow of the array time_arrival at time " + simTime);
				System.exit(2);
			}

			/* There is still room in the queue, so store the time of arrival of the
			   arriving customer at the (new) end of time_arrival. */

			time_arrival[numInQ] = simTime;
		}

		else
		{
			// Server is idle, so arriving customer has a delay of zero.  (The
			// following two statements are for program clarity and do not affect
			// the results of the simulation.)

			delay = 0.0;
			total_of_delays += delay;

			// Increment the number of customers delayed, and make server busy
			nCustsDelayed++;
			server_state = serverState.BUSY;

			// Schedule a departure (service completion)
			timeNextEvent[2] = simTime + SimLib_Random.Expon(meanService, 1);
		}
	}
	
	//Depart Function
	public void Depart() {
		double delay;
		Addout();
		/* Check to see whether the queue is empty. */

		if (numInQ == 0)
		{
			// The queue is empty so make the server idle and eliminate the departure (service completion) event from consideration
			server_state = serverState.IDLE;
			timeNextEvent[2] = 1.0e+30;
		}

		else
		{
			// The queue is nonempty, so decrement the number of customers in queue. 
			numInQ--;

			// Compute the delay of the customer who is beginning service and update the total delay accumulator
			delay = simTime - time_arrival[1];
			total_of_delays += delay;

			// Increment the number of customers delayed, and schedule departure
			nCustsDelayed++;
			timeNextEvent[2] = simTime + SimLib_Random.Expon(meanService, 1);

			// Move each customer in queue (if any) up one place
			for (int i = 1; i <= numInQ; ++i)
			{
				time_arrival[i] = time_arrival[i + 1];
			}
		}
	}
	
	//Update area accumulators for time-average statistics
	void UpdateTimAvgStats()
	{
		double time_since_last_event;

		// Compute time since last event, and update last-event-time marker
		time_since_last_event = simTime - timeLastEvent;
		timeLastEvent = simTime;

		// Update area under number-in-queue function
		areaNumInQ += numInQ * time_since_last_event;

		// Update area under server-busy indicator function
		if(server_state == serverState.IDLE) {
			areaServerStatus += 0 * time_since_last_event;
		}
		else {
			areaServerStatus += 1 * time_since_last_event;
		}
		

		//Update time-average number in system
		if (server_state == serverState.BUSY) {
			timeAverageCount += numInQ + 1;
			if (maxQ < numInQ) {
				maxQ = numInQ;
			}
		}
	}
	
	// Output Results
	public void Report() {
		// Compute and write estimates of desired measures of performance
		double timeAverageCustomers = 0.0;
		double timeAverageSystem = 0.0;
		int customersAboveOne = 0;
		double proportionAboveOne = 0.0;
		Addout();
		
		timeAverageCustomers = timeAverageCount / simTime;
		
		for (int i = 0; i < nDelaysRequired; i++) {
			timeAverageSystem += (timeOutSystem[i] - timeInSystem[i]);
			if ((timeOutSystem[i] - timeInSystem[i]) > maxD) {
				maxD = (timeOutSystem[i] - timeInSystem[i]);
			}
			if ((timeOutSystem[i] - timeInSystem[i]) > 1) {
				customersAboveOne++;
			}
		}
		timeAverageSystem = (timeAverageSystem / nDelaysRequired);
		maxT = maxD + timeAverageSystem;
		proportionAboveOne = (customersAboveOne / (double)nDelaysRequired) * 100;
		
		System.out.format("%n%nAverage delay in queue %11.3f minutes%n", (total_of_delays / nCustsDelayed));
		System.out.format("Average number in queue %10.3f%n", (areaNumInQ / simTime));
		System.out.format("Server utilization %15.3f%n", (areaServerStatus / simTime));
		System.out.format("Time simulation ended %12.3f minutes %n", simTime);
		System.out.format("Time-Average number of customers%10.3f%n", timeAverageCustomers);
		System.out.format("Average total time in the system%10.3f minutes%n", timeAverageSystem);
		System.out.format("Maximum queue size%10d%n", maxQ);
		System.out.format("Maximum delay in queue%10.3f minutes%n", maxD);
		System.out.format("Maximum time in system%10.3f minutes%n", maxT);
		System.out.format("Proportion of customers in queue for longer than a minute%10.1f percent%n%n", proportionAboveOne);
	}
	
	// Utility functions for time in system
	void Addin() {
		timeInSystem[addIn] = simTime;
		addIn++;
	}

	void Addout() {
		timeOutSystem[addOut] = simTime;
		addOut++;
	}
}
