/*
* Jaron Rose 18823978 - Software Engineering Concepts
* Assignment 1: Score Class
* This class is used to track the timer to update the 
* score on the GUI when a robot is killed, and surviving
* every extra second
*/

package edu.curtin.saed.assignment1;

import javafx.scene.control.Label;

public class Score 
{
    private static int score = 0;
    private int killScore = 0;
    private long startTime;
    private Label scoreLabel;

    public Score(Label inScore) 
    {
        //Start timer
        startTime = System.currentTimeMillis();   
        scoreLabel = inScore;
    }

    public void updateScore() 
    {
        long currentTime = System.currentTimeMillis();
        long elapsedTime = currentTime - startTime;
        
        // Calculate the number of seconds elapsed
        int elapsedSeconds = (int) (elapsedTime / 1000);
        
        // Update the score by adding 10 points for each elapsed second
        score = elapsedSeconds * 10 + killScore;

        scoreLabel.setText("Score: " + score);
    }

    public void robotKillScore()
    {
        killScore += 100;
    }

    public int getScore()
    {
        return score;
    }
}
