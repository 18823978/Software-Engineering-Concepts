package edu.curtin.event;

import java.time.LocalDate;
import java.time.LocalTime;

public class Event 
{
    LocalDate date;
    LocalTime startTime;
    int duration;
    String title;
    boolean allDay;

    public Event(LocalDate date, LocalTime start, int dur, String inTitle)
    {
        this.date = date;
        this.startTime = start;
        this.duration = dur;
        this.title = inTitle;
        this.allDay = false;
    }

    public Event(LocalDate date, boolean allDay, String inTitle)
    {
        this.date = date;
        this.startTime = null;
        this.duration = 0;
        this.title = inTitle;
        this.allDay = allDay;
    }

    public boolean getAllDay()
    {
        return allDay;
    }

    public LocalDate getDate()
    {
        return date;
    }

    public LocalTime getTime()
    {
        return startTime;
    }

    public int getDuration()
    {
        return duration;
    }

    public String getTitle()
    {
        return title;
    }
}
