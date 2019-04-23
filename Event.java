package simLab;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.ListIterator; 

public class Event
{
	public enum Order {FIRST, LAST, INCREASING, DECREASING};
	public enum Status {BUSY, IDLE};

    static private final int             MAX_QUEUES = 24;          
    static private double                simTime;
    static private int                   nextEventType;
    static private int                   tellerNumber;
    static private LinkedList<Event>     eventList;
    static private ArrayList<LinkedList> queueLists;
    static private ArrayList<Status> 	 statusList;

    private double                      eventSimTime;
    private int                         eventType;
    private int                         teller;

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
    
    static public int GetStatus(int teller) {
    	if(statusList.get(teller) != null) {
    		if(statusList.get(teller) == Status.IDLE) {
    			return 0;
    		}
    		else {
    			return 1;
    		}
    	}
    	return 0;
    }
    
    static public void SetStatus(int teller, Status status){
    	if(statusList.get(teller) != null) {
    		statusList.set(teller, status);
    	}
    }

    static public void EventSchedule(Event ev)
    {
    	Event.InsertInQueue(ev, Order.INCREASING, 25);
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
    		else if(order == Order.INCREASING) {
    			ListIterator<Event> it = eventList.listIterator();
    	    	boolean schedualed = false;
    	    	int ind = 0;
    	    	
    	    	while(it.hasNext() && schedualed != true) {
    	    		if(ev.GetEventTime() <= it.next().GetEventTime()) {
    	    			ind = it.nextIndex() - 1;
    	    			schedualed = true;
    	    		}
    	    		else if(it.nextIndex() == eventList.size() && schedualed == false) {
    	    			eventList.add(ev);
    	    			return;
    	    		}
    	    	}
    	 
    	    	eventList.add(ind, ev);
    		}
    		else if(order == Order.DECREASING) {
    			ListIterator<Event> it = eventList.listIterator();
    	    	boolean schedualed = false;
    	    	int ind = 0;
    	    	
    	    	while(it.hasNext() && schedualed != true) {
    	    		if(ev.GetEventTime() > it.next().GetEventTime()) {
    	    			ind = it.nextIndex() - 1;
    	    			schedualed = true;
    	    		}
    	    		else if(it.nextIndex() == eventList.size() && schedualed == false) {
    	    			eventList.addFirst(ev);
    	    			return;
    	    		}
    	    	}
    	    	
    	    	eventList.add(ind, ev);
    		}
    	}
    	else if(queueLists.get(qNum) != null){
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
    	
    	if(eventList.isEmpty() != true){
    		while(it.hasNext() && found != true){
    			if(it.next().GetEventType() == eventType){
    				eventList.remove(it.nextIndex() - 1);
    				found = true;
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
    static public void Initialize()
    {        
    	//Initialize lists for simulation
    	eventList = new LinkedList<Event>();
    	queueLists = new ArrayList<LinkedList>();
    	statusList = new ArrayList<Status>();
    	
    	//Initialize simulation clock
    	simTime = 0.0;
    	
    	//Initialize queue size
    	for(int i = 0; i<Sim.numTellers; i++){
    		queueLists.add(new LinkedList());
    		statusList.add(Status.IDLE);
    	}
    	
    	Sim.minDelay = 0;
    	Sim.maxDelay = 0;
    	Sim.totalOfDelays = 0;
    	Sim.nCustsDelayed = 0;
    	Sim.delays = 0;
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
    
    static void UpdateTimAvgStats(){
    	int temp = 0;
    	for(int i = 0; i < Sim.numTellers; i++){
    		temp += Event.GetQueueSize(i);
    	}
    	Sim.numInQ.add(temp/Event.GetSimTime());
    }
    
    static void Sampst(double delay, int stream) {
    	Sim.nCustsDelayed += 1;
    	Sim.delays += delay;
    	if(Sim.minDelay > delay) {
    		Sim.minDelay = delay;
    	}
    	if(Sim.maxDelay < delay) {
    		Sim.maxDelay = delay;
    	}
    }
    
}
