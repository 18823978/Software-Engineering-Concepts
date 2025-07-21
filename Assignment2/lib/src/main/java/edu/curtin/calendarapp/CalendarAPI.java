package edu.curtin.calendarapp;

import java.time.LocalDate;
import java.time.LocalTime;

import edu.curtin.calendarapi.API;

public class CalendarAPI implements API
{
    private CalendarApp mainInstance;
    
    public CalendarAPI(CalendarApp main)
    {
        mainInstance = main;
    }

    @Override
    public void createEvent(String title, LocalDate date, LocalTime startTime, int duration) 
    {
        mainInstance.createEvent(title, date, startTime, duration);
    }

    @Override
    public void createEvent(String title, LocalDate date, boolean allDay) 
    {
        mainInstance.createEvent(title, date, allDay);
    }

    @Override
    public void notifyEvent(String eventString) 
    {
        mainInstance.notifyEvent(eventString);
    }

    @Override
    public void createEvent(String title, String date) 
    {
        mainInstance.createEvent(title, date);
    }
}
