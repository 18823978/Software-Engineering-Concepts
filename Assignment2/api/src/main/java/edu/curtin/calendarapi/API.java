package edu.curtin.calendarapi;

import java.time.LocalTime;
import java.time.LocalDate;

public interface API 
{
    void createEvent(String title, LocalDate date, LocalTime startTime, int duration);
    void createEvent(String title, String date);
    void createEvent(String title, LocalDate date, boolean allDay);
    void notifyEvent(String eventString);
}
