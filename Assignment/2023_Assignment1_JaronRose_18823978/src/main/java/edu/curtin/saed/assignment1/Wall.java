package edu.curtin.saed.assignment1;

/*
 * Jaron Rose 18823978 - Software Engineering Concepts
 * Assignment 1: Wall Class
 * Basic wall construct to hold wall information.
 * Extends super class Prop
 */

public class Wall extends Prop
{
    private int wallHealth;
  
    /*
     * Wall Constructor
     */
    public Wall(double x, double y, int del)
    {
        super(x, y, del);
        wallHealth = 2;
    }

    /*
     * return the health of the wall. Walls 
     * have 2 health as they can be impacted
     * 2 times by different robots
     */
    public int getHealth()
    {
        return wallHealth;
    }

    /*
     * When a robot impacts a wall, the wall will
     * lose 1 health
     */
    public void reduceHealth()
    {
        wallHealth--;
    }

}
