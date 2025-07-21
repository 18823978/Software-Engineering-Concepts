package edu.curtin.event;

import java.util.ArrayList;
import java.util.List;

public final class EventStorage 
{
    private static EventStorage instance;
    private List<Event> events;

    public static EventStorage getInstance()
    {
        if (instance == null) 
        {
            instance = new EventStorage();
        }
        return instance;
    }

    private EventStorage()
    {
        events = new ArrayList<>();
    }

    public void addEvent(Event event)
    {
        events.add(event);
    }

    public List<Event> getEvents()
    {
        return events;
    }

    public void displayList()
    {
        for(Event event : events)
        {
            System.out.println("Event Name: " + event.title);
            System.out.println("Event Date: " + event.date);
            System.out.println("Event Time: " + event.startTime);
            System.out.println("Event Duration: " + event.duration);
            System.out.println("ALL DAY: " + event.allDay);
        }
    }
}
