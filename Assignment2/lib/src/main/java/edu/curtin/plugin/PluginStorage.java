package edu.curtin.plugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PluginStorage 
{
    private static PluginStorage instance;
    private List<PluginClass> plugins;

    public static PluginStorage getInstance()
    {
        if (instance == null) 
        {
            instance = new PluginStorage();
        }
        return instance;
    }

    private PluginStorage()
    {
        plugins = new ArrayList<>();
    }

    public void addPlugin(PluginClass plugin)
    {
        plugins.add(plugin);
    }

    public List<PluginClass> getPlugins()
    {
        return plugins;
    }

    public void displayList()
    {
        for(PluginClass plugin : plugins)
        {
            System.out.println("Plugin ID: " + plugin.getID());
            HashMap<String, String> map = plugin.getMap();
            for(Map.Entry<String,String> entry : map.entrySet())
            {
                System.out.println("Plugin Key: " + entry.getKey());
                System.out.println("Plugin Value: " + entry.getValue());
            }
        }
    }
}
