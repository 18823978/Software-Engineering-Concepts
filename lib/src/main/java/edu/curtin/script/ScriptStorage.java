package edu.curtin.script;

import java.util.ArrayList;
import java.util.List;

public class ScriptStorage 
{
    private static ScriptStorage instance;
    private List<ScriptClass> scripts;

    public static ScriptStorage getInstance()
    {
        if (instance == null) 
        {
            instance = new ScriptStorage();
        }
        return instance;
    }

    private ScriptStorage()
    {
        scripts = new ArrayList<>();
    }

    public void addScript(ScriptClass script)
    {
        scripts.add(script);
    }

    public List<ScriptClass> getScripts()
    {
        return scripts;
    }

    public void displayList()
    {
        for(ScriptClass script : scripts)
        {
            System.out.println("Script Content: " + script.script);
        }
    }
}
