package edu.curtin.calendarapp;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.IllformedLocaleException;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import edu.curtin.calendarapi.Plugin;
import edu.curtin.calendarapi.Script;
import edu.curtin.calendarparser.CalendarParser;
import edu.curtin.calendarparser.ParseException;
import edu.curtin.terminalgrid.TerminalGrid;
import edu.curtin.event.Event;
import edu.curtin.event.EventStorage;
import edu.curtin.plugin.PluginClass;
import edu.curtin.plugin.PluginStorage;
import edu.curtin.script.ScriptClass;
import edu.curtin.script.ScriptStorage;

/*
 * Jaron Rose 18823978 Assignment 2
 * Software Engineering Concepts 2023
 */
public class CalendarApp 
{
    private static EventStorage eventStorage = EventStorage.getInstance();
    private static PluginStorage pluginStorage = PluginStorage.getInstance();
    private static ScriptStorage scriptStorage = ScriptStorage.getInstance();

    private static List<Event> events;
    private static List<PluginClass> plugins;
    private static List<ScriptClass> scripts;
    private static List<String> notificationList;

    private static LocalDate currentDate;
    private static CalendarAPI calendarAPI;
    private static boolean running = true;

    private ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);

    public static void main(String [] args)
    {
        if(args.length <= 0)
        {
            System.out.println("Please specify a file to read" + "\n" + "EXAMPLE: calendar.utf8.cal");
        }
        else
        {
            new CalendarApp().run(args);
        }
    }

    public void run(String[] args)
    {
        calendarAPI = new CalendarAPI(this);

        events = eventStorage.getEvents();
        plugins = pluginStorage.getPlugins();
        scripts = scriptStorage.getScripts();
        notificationList = new ArrayList<>();

        String fileName = args[0]; 
        readFile(fileName);
        
        for(PluginClass plugin : plugins)
        {
            loadPlugin(plugin.getID(), plugin.getMap());
        }

        for(ScriptClass script : scripts)
        {
            loadScript(script);
        }
        // Initialize the current date
        currentDate = LocalDate.now();

        // Create a TerminalGrid instance
        TerminalGrid terminalGrid = new TerminalGrid(System.out, 2000);

        // Display the initial calendar
        displayCalendar(terminalGrid);
        
        while(running)
        {
            displayMenu(terminalGrid);
        }
    }

    @SuppressWarnings({"resource", "PMD.CloseResource"})
    //Scanner will automatically close itself, and closing
    //Scanner too soon will not allow it to reopen when needed.
    public static String getString(String prompt)
    {
        String value = "";

        Locale locale = Locale.getDefault();
        String error1 = "";
        ResourceBundle bundle = null;
        try
        {
            bundle = ResourceBundle.getBundle("bundle", locale);
            error1 = bundle.getString("error1");
        }
        catch(MissingResourceException e)
        {
            System.out.println("Bundle file doesn't exist, setting default to en-AU");
            String setDefault = "en-AU";
            Locale defaultLocale = Locale.forLanguageTag(setDefault);
            bundle = ResourceBundle.getBundle("bundle", defaultLocale);
            error1 = bundle.getString("error1");
        }
        
        Scanner sc = new Scanner(System.in);

        try
        {
            System.out.println(prompt);
            //Receive string input
            value = sc.nextLine().trim();
            System.out.println();
        }
        catch(InputMismatchException e)
        {
            //If input is incorrect, go to next line
            sc.nextLine();
            System.out.println(error1);
        }
        return value;
    }

    private static void displayMenu(TerminalGrid terminalGrid)
    {
        Locale locale = Locale.getDefault();
        String prompt1 = "";

        ResourceBundle bundle = null;
        try
        {
            bundle = ResourceBundle.getBundle("bundle", locale);
            prompt1 = bundle.getString("prompt1");
        }
        catch(MissingResourceException e)
        {
            System.out.println("Bundle file doesn't exist, setting default to en-AU");
            String setDefault = "en-AU";
            Locale defaultLocale = Locale.forLanguageTag(setDefault);
            bundle = ResourceBundle.getBundle("bundle", defaultLocale);
            prompt1 = bundle.getString("prompt1");
        }

        // Prompt the user for input
        String choice = getString(prompt1);
        switch(choice)
        {
            case "quit":
                running = false;
                System.exit(0);
                break;
            case "languages":
                changeLocale();
                break;

            case "+d":
                currentDate = currentDate.plusDays(1);
                break;

            case "+w":
                currentDate = currentDate.plusWeeks(1);
                break;

            case "+m":
                currentDate = currentDate.plusMonths(1);
                break;

            case "+y":
                currentDate = currentDate.plusYears(1);
                break;

            case "-d":
                currentDate = currentDate.minusDays(1);
                break;

            case "-w":
                currentDate = currentDate.minusWeeks(1);
                break;

            case "-m":
                currentDate = currentDate.minusMonths(1);
                break;

            case "-y":
                currentDate = currentDate.minusYears(1);
                break;

            case "t":
                currentDate = LocalDate.now(); // Reset to today
                break;

            default:
                System.out.println("Invalid command. Please try again.");
        }
        displayCalendar(terminalGrid);
    }
    
    private static void changeLocale()
    {
        Locale locale = Locale.getDefault();
        String prompt2 = "";
        String newDefault = "";
        String invalid = "";

        ResourceBundle bundle = null;
        try
        {
            bundle = ResourceBundle.getBundle("bundle", locale);
            prompt2 = bundle.getString("prompt2");
            newDefault = bundle.getString("newDefault"); 
            invalid = bundle.getString("invalid");
        }
        catch(MissingResourceException e)
        {
            System.out.println("Bundle file doesn't exist, setting default to en-AU");
            String setDefault = "en-AU";
            Locale defaultLocale = Locale.forLanguageTag(setDefault);
            bundle = ResourceBundle.getBundle("bundle", defaultLocale);
            prompt2 = bundle.getString("prompt2");
            newDefault = bundle.getString("newDefault"); 
            invalid = bundle.getString("invalid");
        }

        String languageTag = getString(prompt2);
        Locale userLocale = null;
        try 
        {
            userLocale = Locale.forLanguageTag(languageTag);
            Locale.setDefault(userLocale);
            System.out.println(newDefault + " " + Locale.getDefault());
        } 
        catch (IllformedLocaleException e) 
        {
            System.out.println(invalid + " " + languageTag);
        }
    }

    private static void displayCalendar(TerminalGrid terminalGrid) 
    {
        List<LocalDate> days = new ArrayList<>();
        days.add(currentDate);
        List<LocalTime> hours = new ArrayList<>();
        LocalDate today = currentDate;

        Locale locale = Locale.getDefault();
        
        ResourceBundle bundle = null;
        String currDate = "";
        try
        {
            bundle = ResourceBundle.getBundle("bundle", locale);
            currDate = "";
        }
        catch(MissingResourceException e)
        {
            System.out.println("Bundle file doesn't exist, setting default to en-AU");
            String setDefault = "en-AU";
            Locale defaultLocale = Locale.forLanguageTag(setDefault);
            bundle = ResourceBundle.getBundle("bundle", defaultLocale);
            currDate = bundle.getString("currDate");
        }
        
        // Initialize a date format for displaying dates
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern(bundle.getString("datePattern"));
        LocalDate dayOffset;
        for(int i = 1; i < 7; i++)
        {
            dayOffset = currentDate.plusDays(i);
            days.add(dayOffset);
        }

        for(int i = 0; i < 24; i++)
        {
            LocalTime time = LocalTime.of(i, 0);
            hours.add(time);
        }

        String[][] calendarData = {
            { "", days.get(0).format(dateFormat), days.get(1).format(dateFormat), days.get(2).format(dateFormat), days.get(3).format(dateFormat), days.get(4).format(dateFormat), days.get(5).format(dateFormat), days.get(6).format(dateFormat)}, 
            {hours.get(0).toString(), "", "", "","", "", "", ""},
            {hours.get(1).toString(), "", "", "","", "", "", ""},
            {hours.get(2).toString(), "", "", "","", "", "", ""},
            {hours.get(3).toString(), "", "", "","", "", "", ""},
            {hours.get(4).toString(), "", "", "","", "", "", ""},
            {hours.get(5).toString(), "", "", "","", "", "", ""},
            {hours.get(6).toString(), "", "", "","", "", "", ""},
            {hours.get(7).toString(), "", "", "","", "", "", ""},
            {hours.get(8).toString(), "", "", "","", "", "", ""},
            {hours.get(9).toString(), "", "", "", "", "", "", ""},
            {hours.get(10).toString(), "", "", "", "", "", "", ""},
            {hours.get(11).toString(), "", "", "", "", "", "", ""},
            {hours.get(12).toString(), "", "", "","", "", "", ""},
            {hours.get(13).toString(), "", "", "","", "", "", ""},
            {hours.get(14).toString(), "", "", "","", "", "", ""},
            {hours.get(15).toString(), "", "", "","", "", "", ""},
            {hours.get(16).toString(), "", "", "","", "", "", ""},
            {hours.get(17).toString(), "", "", "","", "", "", ""},
            {hours.get(18).toString(), "", "", "","", "", "", ""},
            {hours.get(19).toString(), "", "", "","", "", "", ""},
            {hours.get(20).toString(), "", "", "","", "", "", ""},
            {hours.get(21).toString(), "", "", "", "", "", "", ""},
            {hours.get(22).toString(), "", "", "", "", "", "", ""},
            {hours.get(23).toString(), "", "", "", "", "", "", ""}
        };
        populateCalendar(calendarData, days, hours);
        terminalGrid.setTerminalWidth(3000);
        terminalGrid.print(calendarData);
        System.out.println(currDate + " " + today);
    }

    private static void populateCalendar(String[][] calendarData, List<LocalDate> days, List<LocalTime> hours) 
    {
        // Iterate over events and add them to the calendar
        for (Event event : events) 
        {
            if(!event.getAllDay())
            {
                String eventTitle = event.getTitle();

                LocalTime eventStartTime = event.getTime();
                LocalDate eventDate = event.getDate();
                int eventDuration = event.getDuration();;

                int hourTime = event.getTime().getHour();
                int dayCounter = 0;
                // Iterate through calendar time slots (rows and columns)
                for (int row = 1; row < calendarData.length; row++) 
                {
                    if(dayCounter == 7)
                    {
                        dayCounter = 0;
                    }
                    LocalDate calendarDay = days.get(dayCounter); // Date of the calendar day
                    dayCounter++;
                    // Check if the event's date matches the calendar day
                    if (eventDate.equals(calendarDay)) 
                    {
                        LocalTime calendarTimeSlotStart = hours.get(hourTime); // Start time of the calendar time slot
                        LocalTime calendarTimeSlotEnd = calendarTimeSlotStart.plusMinutes(60); // End time of the calendar time slot

                        // Check if the event's start time is contained within the calendar time slot'
                        if(eventStartTime.isAfter(calendarTimeSlotStart.minusMinutes(1)) && eventStartTime.isBefore(calendarTimeSlotEnd.plusMinutes(1)))
                        {
                            // Place the event in the calendar
                            calendarData[hourTime + 1][dayCounter] = eventTitle + " Time: " + eventStartTime + " Duration: " + eventDuration + " mins"; 
                            break; 
                        }
                    }
                }
            } 
            else 
            {
                String eventTitle = event.getTitle();
                boolean eventAllDay = event.getAllDay();
                LocalDate eventDate = event.getDate();
                String duration = "";
                if(eventAllDay)
                {
                    duration = "All Day";
                }
                // Iterate through calendar time slots (rows and columns)
                for (int column = 0; column < 7; column++) 
                {
                    LocalDate calendarDay = days.get(column); // Date of the calendar day
                    // Check if the event's date matches the calendar day
                    if (eventDate.equals(calendarDay)) 
                    {
                        calendarData[1][column + 1] = eventTitle + " Duration: " + duration; 
                    }
                }
            }
        }
    }

    private static void readFile(String fileName)
    {
        try  
        {
            Charset encoding = getEncodingFromFilename(fileName);
            CalendarParser.parse(fileName, encoding);
        }
        catch (ParseException e) 
        {
            e.printStackTrace();
        }
        catch (IOException e) 
        {
            e.printStackTrace();
        } 
    }

    private static Charset getEncodingFromFilename(String filename) 
    {
        Charset utfSet = null;
        if (filename.endsWith(".utf16.cal")) {
            utfSet = StandardCharsets.UTF_16;
        } else if (filename.endsWith(".utf32.cal")) {
            utfSet = Charset.forName("UTF-32");
        } else {
            utfSet = StandardCharsets.UTF_8;
        }
        return utfSet;
    }

    public static void loadPlugin(String pluginName, HashMap<String, String> plugKeyValues)
    {
        try
        {
            Class<?> pluginClass = Class.forName(pluginName);
            Plugin pluginObj = (Plugin) pluginClass.getConstructor().newInstance();
            pluginObj.start(calendarAPI, plugKeyValues);
            System.out.println("Plugin: " + pluginName + " loaded successfully!");
        }
        catch(ReflectiveOperationException re)
        {
            System.out.println("Reflective Operation Error: " + re.getMessage());
        }
        catch(RuntimeException ex)
        {
            System.out.println("Runtime Exception: " + ex.getMessage());
        }
    }

    public static void loadScript(ScriptClass script)
    {
        try
        {
            Class<?> pluginClass = Class.forName("edu.curtin.customscripts.ScriptPublicHolidays");
            Script pluginObj = (Script) pluginClass.getConstructor().newInstance();
            pluginObj.runScript(calendarAPI, script.getScript());
            System.out.println("Script loaded successfully!");
        }
        catch(ReflectiveOperationException re)
        {
            System.out.println("Reflective Operation Error: " + re.getMessage());
        }
        catch(RuntimeException ex)
        {
            System.out.println("Runtime Exception: " + ex.getMessage());
        }
    }

    public void createEvent(String title, LocalDate date, LocalTime startTime, int duration)
    {
        Event event = new Event(date, startTime, duration, title);
        events.add(event);
    }

    public void createEvent(String title, LocalDate date, boolean allDay)
    {
        Event event = new Event(date, allDay, title);
        events.add(event);
    }

    public void createEvent(String title, String date) 
    {
        LocalDate scriptDate = LocalDate.parse(date);
        Event event = new Event(scriptDate, true, title);
        events.add(event);
	}

    public void notifyEvent(String eventToNotify)
    {
        notificationList.add(eventToNotify);
        // Schedule the notification to run in the future
        executor.scheduleAtFixedRate(() -> {
            for (String eventNotification : notificationList) 
            {
                for (Event event : events) 
                {
                    String eventTitle = event.getTitle();
                    if (event.getDate().equals(currentDate) && eventTitle.contains(eventNotification)) 
                    {
                    // Check if the event title contains the specified text
                        if (!event.getAllDay()) 
                        {
                            LocalTime timeNow = LocalTime.now();
                            LocalTime eventStartTime = event.getTime(); // Start time of the calendar time slot
                            LocalTime eventEndTime = eventStartTime.plusMinutes(event.getDuration()); // End time of the calendar time slot
                            // Check if the event's start time is contained within the calendar time slot'
                            if(timeNow.isAfter(eventStartTime.minusMinutes(1)) && timeNow.isBefore(eventEndTime.plusMinutes(1)))
                            {
                                // Output the complete event details
                                System.out.println("EVENT NOW");
                                System.out.println("Event Details:");
                                System.out.println("Title: " + eventTitle);
                                System.out.println("Date: " + event.getDate());
                                System.out.println("Start Time: " + event.getTime());
                                System.out.println("Duration: " + event.getDuration() + " minutes");
                                notificationList.remove(eventToNotify);
                            }
                        } 
                        else 
                        {
                            // Output the complete event details
                            System.out.println("EVENT TODAY!");
                            System.out.println("Event Details:");
                            System.out.println("Title: " + eventTitle);
                            System.out.println("Date: " + event.getDate());
                            System.out.println("All Day");
                            notificationList.remove(eventToNotify);
                        }
                    }
                }
            }
        }, 0, 10, TimeUnit.SECONDS);
    }

	
}


