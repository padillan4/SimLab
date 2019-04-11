package SimLab;

import java.util.Scanner;

import java.util.ArrayList;

public class Bank
{
    static ArrayList<Double> timeArrival;

    /* Declare non-simlib global variables. */

    static int   minTellers, maxTellers, numTellers, shortestLength, shortestQueue;
    static double lengthDoorsOpen;

    static final int IDLE = 0;
    static final int BUSY = 1;

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
    static double timeLastEvent;
    static int nCustsDelayed;
    static double totalOfDelays; 
    static double areaNumInQ;
    static double areaServerStatus;
    static double[] timeNextEvent = new double[3];
    static double total_of_delays;
    

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
        //        Event.DisplayQueue();

        for (int teller = 1; teller <= numTellers; ++teller) 
        {
            if (Event.GetQueueSize(numTellers + teller) == 0)  // Is the teller idle? if so, this customer has 0 delay
            {
                
                ev = new Event(); // fake event, server busy
                // Make this teller busy (attributes are irrelevant). 
                Event.InsertInQueue(ev, Event.Order.FIRST, numTellers + teller);

                // Schedule a service completion. 

                ev = new Event(Event.GetSimTime() + SimLib_Random.Expon(meanService, STREAM_SERVICE), EVENT_DEPARTURE, teller);
                Event.EventSchedule(ev);

                return;
            }
        }

        // All tellers are busy, so find the shortest queue (leftmost shortest in case of ties).

        int shortestLength = Event.GetQueueSize(1);
        int shortestQueue = 1;

        for (int teller = 2; teller <= numTellers; ++teller)
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
            // There was no line at this teller, and since we are processing departure this teller goes idle via remove from queue
            Event.RemoveFromQueue(Event.Order.FIRST, numTellers + teller);
        }
        else
        {
            // There is a line at this teller, so move the first one is line, and then schedule when this customer will leave the teller
            Event evRemoved = ((Event)Event.RemoveFromQueue(Event.Order.FIRST, teller));
            
            double eTime = evRemoved.GetEventTime();
            double delay = Event.GetSimTime() - evRemoved.GetEventTime();  // total for customer going from front of line to teller
            
            
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
        ni           = Event.GetQueueSize(teller) + Event.GetQueueSize(numTellers + teller);

        // Scan all the lines at the tellers
        for (otherTeller = 1; otherTeller <= numTellers; ++otherTeller)
        {
            int lineSize     = Event.GetQueueSize(otherTeller);
            int tellerStatus = Event.GetQueueSize(numTellers + otherTeller);
//System.out.print("(" + tellerStatus + ") - " + lineSize + " "); // Display teller state, and line size
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
//System.out.println();

        // Check to see whether a jockeying customer was found
        if (jumper > 0) 
        {
            Event evRemoved = ((Event)Event.RemoveFromQueue(Event.Order.LAST, jumper));
            double eTime = evRemoved.GetEventTime();
            
            if (Event.GetQueueSize(numTellers + teller) > 0)
            {
                // The teller of this new queue is busy, so place the jumper customer at the end of this queue.
                Event.InsertInQueue(evRemoved, Event.Order.LAST, teller);
            }
            else 
            {
                // The teller of his new queue is idle, so tally the jockeying customer's delay, make 
                // the teller busy, and start service.
                
                double delay = Event.GetSimTime() - evRemoved.GetEventTime();  // total delay from back of line to switch line
                
                Event ev = new Event(Event.GetSimTime(), 0); // fake event
                Event.InsertInQueue(ev, Event.Order.FIRST, numTellers + teller);
                ev = new Event(Event.GetSimTime() + SimLib_Random.Expon(meanService, STREAM_SERVICE), EVENT_DEPARTURE, teller);
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
        
        avgNumInQ = Event.GetNumInQ() / Event.GetSimTime();
        System.out.println(avgNumInQ);
        
        System.out.format("%n%nWith%2d tellers, average number in queue = %10.3f", numTellers, avgNumInQ);
        
        System.out.format("%n%nDelays in queue, in minutes:%n");
    }
    
    static void setTellers(int Tellers){
    	numTellers = Tellers;
    }
}
