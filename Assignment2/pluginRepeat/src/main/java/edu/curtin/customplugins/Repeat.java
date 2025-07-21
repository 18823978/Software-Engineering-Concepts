package edu.curtin.customplugins;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;

import edu.curtin.calendarapi.*;

public class Repeat implements Plugin
{
    private API api;
    private HashMap<String, String> pluginKeyValues;

    @Override
    public void start(API api, HashMap<String, String> pluginKeyValues)
    {
        this.api = api;
        this.pluginKeyValues = pluginKeyValues;
        createEvent();
    }

    public void createEvent()
    {
        System.out.println("Creating Plugin Event:");
        // for(Map.Entry<String,String> entry : pluginKeyValues.entrySet())
        // {
        //     System.out.println(entry.getKey() + "\tValue: " + entry.getValue());
        // }

        String checkAllDay = pluginKeyValues.get("time");
        checkAllDay = checkAllDay.replace("\"", ""); // Remove double quotes
        if(!checkAllDay.equals("all-day"))
        {
            String name = pluginKeyValues.get("name");

            String dateStr = pluginKeyValues.get("date");
            dateStr = dateStr.replace("\"", ""); // Remove double quotes
            LocalDate date = LocalDate.parse(dateStr);

            String timeStr = pluginKeyValues.get("time");
            timeStr = timeStr.replace("\"", ""); // Remove double quotes
            LocalTime time = LocalTime.parse(timeStr);

            String durationStr = pluginKeyValues.get("duration");
            durationStr = durationStr.replace("\"", ""); // Remove double quotes
            int duration = Integer.parseInt(durationStr);

            String repeatStr = pluginKeyValues.get("repeat");
            repeatStr = repeatStr.replace("\"", ""); // Remove double quotes
            int repeat = Integer.parseInt(repeatStr);
            LocalDate temp = date;
            LocalDate endDate = temp.plusYears(1); // 1 Year from start date

            //Create initial event
            api.createEvent(name, date, time, duration);
            // No repeating days
            if(repeat != 0)
            {
                // create events every repeat day until end date
                while (date.isBefore(endDate)) 
                {
                    date = date.plusDays(repeat);
                    api.createEvent(name, date, time, duration);
                }
            }
        }
        else
        {
            String name = pluginKeyValues.get("name");

            String dateStr = pluginKeyValues.get("date");
            dateStr = dateStr.replace("\"", ""); // Remove double quotes
            LocalDate date = LocalDate.parse(dateStr);

            boolean allDay = true;

            String repeatStr = pluginKeyValues.get("repeat");
            repeatStr = repeatStr.replace("\"", ""); // Remove double quotes
            int repeat = Integer.parseInt(repeatStr);
            LocalDate temp = date;
            LocalDate endDate = temp.plusYears(1); // 1 Year from start date

            //Create initial event
            api.createEvent(name, date, allDay);
            // No repeating days
            if(repeat != 0)
            {
                // create events every repeat day until end date
                while (date.isBefore(endDate)) 
                {
                    date = date.plusDays(repeat);
                    api.createEvent(name, date, allDay);
                }
            }
        }
    }
}
