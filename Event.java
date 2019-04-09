package simLab;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.ListIterator; 

public class Event
{
    public enum Order {FIRST, LAST, INCREASING, DECREASING};

    static private final int             MAX_QUEUES = 25;          
    
    static private int                   nextEventType;
    static private int                   tellerNumber;
    static private LinkedList<Event>     eventList;
    static private ArrayList<LinkedList> queueLists;

    private double                      eventSimTime;
    private int                         eventType;
    private int                         teller;

    /**
     * Constructor for objects of class Event
     */
    public Event()
    {
        this.eventSimTime = 0.0;
        this.eventType    = 1;
        this.teller       = 0;
    }

    /**
     * Constructor for objects of class Event
     */
    public Event(double eventSimTime, int eventType)
    {
        this.eventSimTime = eventSimTime;
        this.eventType    = eventType;
        this.teller       = 0;
    }
    
        /**
     * Constructor for objects of class Event
     */
    public Event(double eventSimTime, int eventType, int teller)
    {
        this.eventSimTime = eventSimTime;
        this.eventType    = eventType;
        this.teller       = teller;
    }

    /**
     */
    public double GetEventTime()
    {
        return eventSimTime;
    }
    
    /**
     */
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
    	
    }

    static public void InsertInQueue(Event ev, Order order, int qNum)
    {
    	if(qNum >= 1 || qNum <= 24) {
    		queueLists.add(queueLists.size(), eventList);
    		
    	}
    	
    }

    static Object RemoveFromQueue(Order order, int qNum)
    {
    	
    	return 0;
    }



    static public void Initialize()
    {        
    	
    }
    
    static public int GetNextEventType()
    {
        return nextEventType;
    }
    
    static public int GetTellerNumber()
    {
        return tellerNumber;
    }

    /**
     * Gets the amount of Events before the specified qNum
     * 
     * @param qNum    the specified qNum
     */
    static public int GetQueueSize(int qNum)
    {
    	int waitLength = 0;
    	for(int i = 0; i < qNum; i++) {
    		waitLength = i;
    	}
    	return waitLength;
    }

    /**
     * Searches for the first Event object with specified eventType
     * then removes it from eventList.
     * 
     * @param eventType    the event type to be removed
     */
    static public void EventCancel(int eventType) 
    {
    	int i = 0; 
    	while(i < eventList.size()) {
    		if(eventList.get(i).GetEventType() == eventType) {
    			eventList.remove(i);
    			i = eventList.size(); //Kills the loop
    		} else {
    			i++;
    		}
    	}
    }
    
    static public void Timing()
    {
        Event ev      = (Event)RemoveFromQueue(Order.FIRST, 25);
        nextEventType = ev.GetEventType();
        tellerNumber  = ev.GetTeller();
        
    }

    /**
     * I don't know what this methods suppose to do
     * @param d
     * @param sampstDelays
     */
	public static void Sampst(double d, int sampstDelays) {
		// TODO Auto-generated method stub
		
	}

	/**
     * I don't know what this methods suppose to do
     */
	public static double Filest(int j) {
		// TODO Auto-generated method stub
		return 0;
	}

	/**
     * I don't know what this methods suppose to do
     */
	public static void OutSampst(int sampstDelays, int sampstDelays2) {
		// TODO Auto-generated method stub
		
	}
}