package edu.curtin.customplugins;

import java.util.HashMap;

import edu.curtin.calendarapi.*;

public class Notify implements Plugin
{
    private API api;
    private HashMap<String, String> pluginKeyValues;

    @Override
    public void start(API api, HashMap<String, String> pluginKeyValues)
    {
        this.api = api;
        this.pluginKeyValues = pluginKeyValues;
        notifyEvent();
    }
   
    public void notifyEvent()
    {
        String eventToNotify = pluginKeyValues.get("text");
        api.notifyEvent(eventToNotify);
    }
}