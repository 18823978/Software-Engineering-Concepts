package edu.curtin.saed.assignment1;

/*
* Jaron Rose 18823978 - Software Engineering Concepts
* Assignment 1: Prop Class
* Super class to Robot, Wall, and Citadel
* Holds the basic information for the classes
*/

public class Prop 
{
    public int id;
    public int delay;
    public double positionX;
    public double positionY;

    /*
     * Prop Constructors: There are 3 constructors as
     * each class will use different constructors 
     * based on their needs
     */
    public Prop(double x, double y, int inID, int del)
    {
        positionX = x;
        positionY = y;
        id = inID;
        delay = del;
    }

    public Prop(double x, double y, int del)
    {
        positionX = x;
        positionY = y;
        delay = del;
    }

    public Prop(double x, double y)
    {
        positionX = x;
        positionY = y;
    }

    /*
     * Retrieves the X Position of the specified object
     */
    public double getX()
    {
        return positionX;
    }

    /*
     * Retrieves the Y Position of the specified object
     */
    public double getY()
    {
        return positionY;
    }

    /*
     * Retrieves the delay of the specified object
     */
    public int getDelay()
    {
        return delay;
    }

    /*
     * Retrieves the ID of the specified object
     */
    public int getID()
    {
        return id;
    }

    /*
     * Allows setting of the X Position of the specified
     * object
     */
    public void setX(double x)
    {
        positionX = x;
    }

    /*
     * Allows setting of the Y Position of the specified
     * object
     */
    public void setY(double y)
    {
        positionY = y;
    }
}
