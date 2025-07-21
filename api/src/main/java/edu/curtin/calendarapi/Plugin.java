package edu.curtin.calendarapi;

import java.util.HashMap;

public interface Plugin 
{
    void start(API api, HashMap<String, String> pluginKeyValues);
}
