package edu.curtin.customscripts;

import edu.curtin.calendarapi.*;
import org.python.util.PythonInterpreter;

@SuppressWarnings({"resource", "PMD.CloseResource"})
public class ScriptPublicHolidays implements Script
{
    @Override
    public void runScript(API api, String pythonScript)
    {
        System.out.println("RUNNING SCRIPT!");
        PythonInterpreter interpreter = new PythonInterpreter();
        interpreter.set("api", api);
        String newScript = pythonScript.substring(1, pythonScript.length() - 1);
        newScript = newScript.replaceAll("\"\"", "\"");
        System.out.println(newScript);
        
        interpreter.exec(newScript);
    }
}
