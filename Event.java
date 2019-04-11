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
    
    static private int 					numInQ;
    
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
    	if(qNum == 25){
    		if(order == Order.FIRST){
    			eventList.addFirst(ev);
    		}
    		else if(order == Order.LAST){
    			eventList.addLast(ev);
    		}
    	}
    	if(queueLists.get(qNum) != null){
    		if(order == Order.FIRST){
    			queueLists.get(qNum).addFirst(ev);
    		}
    		else if(order == Order.LAST){
    			queueLists.get(qNum).addLast(ev);
    		}
    	}
    	else{
    		System.out.println("invalid qNum to insert.");
    		System.exit(4);
    	}
    }

    static Object RemoveFromQueue(Order order, int qNum)
    {
    	if(qNum == 25){
    		if(order == Order.FIRST && eventList != null){
    			return eventList.removeFirst();
    		}
    		else if(order == Order.LAST && eventList != null){
    			return eventList.removeLast();
    		}
    	}
    	else if(order == Order.FIRST && queueLists.get(qNum) != null){
    		return queueLists.get(qNum).removeFirst();
    	}
    	else if(order == Order.LAST && queueLists.get(qNum) != null){
    		return queueLists.get(qNum).removeLast();
    	}
    	return null;
    }
    
    static int EventCancel(int eventType){
    	ListIterator<Event> it = eventList.listIterator();
    	boolean found = false;
    	
    	if(eventType == 1 && eventList.isEmpty() != true){
    		while(it.hasNext() && found != true){
    			if(it.next().GetEventType() == eventType){
    				eventList.remove(it.nextIndex()-1);
    				found = true;
    				return 1;
    			}
    			else if(it.nextIndex() > eventList.size()){
    				return 0;
    			}
    		}
    		if(found == true){
    			return 1;
    		}
    		else{
    			return 0;
    		}
    	}
    	else if(eventType == 2 && eventList.isEmpty() != true){
    		while(it.hasNext() && found != true){
    			if(it.next().GetEventType() == eventType){
    				eventList.remove(it.nextIndex()-1);
    				found = true;
    				return 1;
    			}
    			else if(it.nextIndex() > eventList.size()){
    				return 0;
    			}
    		}
    		if(found == true){
    			return 1;
    		}
    		else{
    			return 0;
    		}
    	}
    	else{
    		return 0;
    	}
    }


    //Initialization function
    static public void Initialize(int numTellers)
    {        
    	//Initialize lists for simulation
    	eventList = new LinkedList<Event>();
    	queueLists = new ArrayList<LinkedList>();
    	
    	//Initialize simulation clock
    	simTime = 0.0;
    	numInQ = 0;
    	
    	//Initialize queue size
    	for(int i = 0; i<numTellers; i++){
    		queueLists.add(new LinkedList());
    	}
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
    	if(qNum == 25){
    		return eventList.size();
    	}
    	else{
    		return queueLists.get(qNum).size();
    	}	
    }

    static public void Timing()
    {
        Event ev      = (Event)RemoveFromQueue(Order.FIRST, 25);
        simTime       = ev.GetEventTime();
        nextEventType = ev.GetEventType();
        tellerNumber  = ev.GetTeller();
        
    }
    
    static void UpdateTimAvgStats(int numTellers){
    	for(int i = 0; i < numTellers; i++){
    		System.out.println("tellers: " + numTellers);
    		System.out.println("q:" + Event.GetQueueSize(i));
    		numInQ += Event.GetQueueSize(i);
    	}
    }
    
    public static int GetNumInQ(){
    	return numInQ;
    }
}
