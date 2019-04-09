package SimLab;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.ListIterator; 

public class Event
{
    public enum Order {FIRST, LAST, INCREASING, DECREASING};

    static private final int             MAX_QUEUES = 24;          
    static private double                simTime;
    static private int                   nextEventType;
    static private int                   tellerNumber;
    static private LinkedList<Event>     eventList;
    static private ArrayList<LinkedList> queueLists;

    private double                      eventSimTime;
    private int                         eventType;
    private int                         teller;
    
    final int SAMPST_DELAYS     = 1;  // sampst variable for delays in queue(s)

    final int STREAM_INTERARRIVAL = 1;
    final int STREAM_SERVICE = 2;

    // Constructor for objects of class Event
    public Event()
    {
        this.eventSimTime = 0.0;
        this.eventType    = 1;
        this.teller       = 0;
    }

    // Constructor for objects of class Event
    public Event(double eventSimTime, int eventType)
    {
        this.eventSimTime = eventSimTime;
        this.eventType    = eventType;
        this.teller       = 0;
    }
    
     // Constructor for objects of class Event
    public Event(double eventSimTime, int eventType, int teller)
    {
        this.eventSimTime = eventSimTime;
        this.eventType    = eventType;
        this.teller       = teller;
    }

    public double GetEventTime()
    {
        return eventSimTime;
    }
    
    public int GetEventType()
    {
        return eventType;
    }
    
    public int GetTeller()
    {
        return teller;
    }

    static public void EventSchedule(Event ev)
    {
    	eventList.add(ev);
    }

    static public void InsertInQueue(Event ev, Order order, int qNum)
    {
    	if(queueLists.get(qNum) != null){
    		if(order == Order.FIRST){
    			queueLists.get(qNum).addFirst(ev);
    		}
    		else if(order == Order.LAST){
    			queueLists.get(qNum).add(ev);
    		}
    	}
    	else{
    		System.out.println("invalid qNum to insert.");
    	}
    }

    static Object RemoveFromQueue(Order order, int qNum)
    {
    	if(order == Order.FIRST && queueLists.get(qNum) != null){
    		return queueLists.get(qNum).removeFirst();
    	}
    	else if(order == Order.LAST && queueLists.get(qNum) != null){
    		return queueLists.get(qNum).removeLast();
    	}
    	else{
    		return null;
    	}
    }
    
    static int EventCancel(int eventType){
    	ListIterator it = eventList.listIterator();
    	
    	if(eventType == 1 && eventList.isEmpty() != true){
    		while(it.hasNext()){
    			if(it.next().equals(eventType)){
    				
    			}
    		}
    		return 1;
    	}
    	else if(eventType == 2){
    		
    		return 1;
    	}
    	else{
    		return 0;
    	}
    }


    //Initialization function
    static public void Initialize()
    {        
    	//Initialize lists for simulation
    	eventList = new LinkedList<Event>();
    	queueLists = new ArrayList<LinkedList>();
    }

    static public double GetSimTime()
    {
        return simTime;
    }
    
    static public int GetNextEventType()
    {
        return nextEventType;
    }
    
    static public int GetTellerNumber()
    {
        return tellerNumber;
    }

    static public int GetQueueSize(int qNum)
    {
    	return queueLists.get(qNum).size();
    }

    static public void Timing()
    {
        Event ev      = (Event)RemoveFromQueue(Order.FIRST, 25);
        simTime       = ev.GetEventTime();
        nextEventType = ev.GetEventType();
        tellerNumber  = ev.GetTeller();
        
    }
}
