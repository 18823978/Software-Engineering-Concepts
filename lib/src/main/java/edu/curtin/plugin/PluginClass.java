package edu.curtin.plugin;

import java.util.HashMap;

public class PluginClass 
{
    private String id;
    private HashMap<String, String> plugKeyValues;

    public PluginClass(String id, HashMap<String, String> keyValues)
    {
        this.id = id;
        this.plugKeyValues = keyValues;
    }

    public String getID()
    {
        return id;
    }

    public HashMap<String, String> getMap()
    {
        return plugKeyValues;
    }
}
