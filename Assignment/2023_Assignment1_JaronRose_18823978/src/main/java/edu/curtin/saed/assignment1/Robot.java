/*
* Jaron Rose 18823978 - Software Engineering Concepts
* Assignment 1: Robot Class
* Provides methods to move robot and get robot
* information. Extends super class Prop
*/

package edu.curtin.saed.assignment1;

import java.util.Random;

public class Robot extends Prop
{
    private static int robotID = 0;
    private int robotHealth;
    private GameBoard board;
    private double previousX;
    private double previousY;

    /*
     * Robot Constructor
     */
    public Robot(double x, double y, int del)
    {
        super(x, y, robotID, del);
        robotHealth = 1;
    }

    /*
     * Increments the robot id
     */
    public void incrementID()
    {
        robotID++;
    }

    /*
     * Returns the health of the robot,
     * if the robot impacts a wall, it will die
     */
    public int getHealth()
    {
        return robotHealth;
    }

    /*
     * Reduce the health of the robot
     */
    public void reduceHealth()
    {
        robotHealth--;
    }

    /*
     * Sets the gameboard which the robot will be played on
     */
    public void setBoard(GameBoard inBoard)
    {
        board = inBoard;
    }

    /*
     * Retrieve previous X location of the robot
     */
    public double getPreviousX()
    {
        return previousX;
    }

    /*
     * Retrieve previous Y location of the robot
     */
    public double getPreviousY()
    {
        return previousY;
    }

    /*
     * Method for the basic movement of the robot,
     * The robot will randomly choose a direction from 
     * Up, Down, Left, or Right. The robot will check if its a valid
     * movement before moving into the new square
     */
    public void moveRobot() 
    {
        // This will choose randomly the direction the robot will go
        Random rand = new Random();
        int num = rand.nextInt(1,5);

        // Calculate the direction to move towards the citadel (up, down, left, or right) 
        double targetX  = super.getX();
        double targetY  = super.getY();

        // Citadel position already known
        double citadelX = 4.0;
        double citadelY = 4.0; 

        if(num == 1 && positionX < citadelX) 
        {
            // Move right
            targetX++;
        } 
        if(num == 2 && positionX > citadelX) 
        {
            // Move left
            targetX--;
        }
        if(num == 3 && positionY < citadelY) 
        {
            // Move down
            targetY++;
        } 
        if(num == 4 && positionY > citadelY) 
        {
            // Move up
            targetY--;
        }

        // Check if the next position is valid (not occupied by another robot or outside the grid)
        if (isValidMove(targetX, targetY)) 
        {
            previousX = super.getX();
            previousY = super.getY();
            // Move the robot to the new position
            super.setX(targetX);
            super.setY(targetY);         
        } 
    }

    /*
     * Checks whether the robot can move into a tile.
     * The robot cannot move outside the grid, or the robot
     * cannot move into a tile already occupied by another robot
     */
    private boolean isValidMove(double x, double y) 
    {
        boolean valid = true;
        // Check if the new coordinates are within the bounds of the grid
        if(x < 0 || x >= 9 || y < 0 || y >= 9) 
        {
            valid = false; // The move is outside the grid boundaries
        }
        // Check if the new grid square is already occupied by another robot
        for (Robot otherRobot : board.getRobots()) 
        {
            if(!otherRobot.equals(this) && otherRobot.getX() == x && otherRobot.getY() == y) 
            {
                valid = false; // Another robot is occupying the square
            }
        }        
        // The move is valid
        return valid;
    } 
}
