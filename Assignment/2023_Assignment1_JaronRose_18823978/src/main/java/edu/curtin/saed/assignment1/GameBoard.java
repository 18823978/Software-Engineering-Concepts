/*
* Jaron Rose 18823978 - Software Engineering Concepts 
* Assignment 1 GameBoard Class
* Creates a gameboard that houses all "Props" such as robots,
* walls and the citadel. This class will place the props on the board,
* and communicates with the JFXArena.java to draw the images on the GUI
*/

package edu.curtin.saed.assignment1;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;

public class GameBoard 
{
    private JFXArena arena;
    private TextArea logger;
    private Score score;
    private Label wallLabel;
    private List<Robot> robots;
    private List<Wall> walls;
    private List<Prop> allProps;
    private Object mutex;
    private int wallBuildCooldown;
    private Prop[][] board;
    private static int numWalls = 0;
    private static final int NUM_WALLS = 10;
    private static ExecutorService threadPool = Executors.newCachedThreadPool();
    private static BlockingQueue<Wall> queue = new ArrayBlockingQueue<>(NUM_WALLS);
    private static ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private static ScheduledExecutorService scoreUpdater = Executors.newSingleThreadScheduledExecutor();
    private static ScheduledExecutorService wallLabelUpdater = Executors.newSingleThreadScheduledExecutor();
    private static ScheduledExecutorService wallSchedule = Executors.newSingleThreadScheduledExecutor();
    
    /*
     * Gameboard Constructor
     */
    public GameBoard(JFXArena arena, TextArea log, Score scorer, Label wallLbl) 
    {
        this.arena = arena;
        logger = log;
        score = scorer;
        wallLabel = wallLbl;
        board = new Prop[9][9];
        robots = new ArrayList<>();
        walls = new ArrayList<>();
        allProps = new ArrayList<>();
        wallBuildCooldown = 2000;
        arena.game = this;
        mutex = new Object();   
    }

    /*
     * This method creates the citadel in the middle of the grid
     */
    public void createCitadel()
    {
        Citadel citadel = new Citadel(4, 4);
        board[(int)citadel.getX()][(int)citadel.getY()] = citadel;
        addProp(citadel);
    }

    /*
     * This method is used to put the walls in a queue
     * take them out the queue and put them on the board.
     * If the number of walls is = 10, the queue gets emptied to
     * ignore any more wall building commands until a wall slot
     * is available
     */
    public void makeWall(double x, double y) throws InterruptedException
    {
        if(numWalls < 10)
        {
            queue.put(new Wall(x, y, wallBuildCooldown));
        }
        else if(numWalls == 10)
        {
            queue.removeAll(queue);
        }            
        wallSchedule.scheduleWithFixedDelay(()->
        {  
            try 
            {
                if(gameOver())
                {
                    Thread.currentThread().interrupt();
                }    
                if(numWalls < 10)
                {                   
                    Wall newWall = queue.take();
                    if(checkWallPlacement(newWall.getX(), newWall.getY()))
                    {
                        Thread.sleep(wallBuildCooldown);
                        addWall(newWall);
                        addProp(newWall);               
                        synchronized(mutex)
                        {
                            board[(int)newWall.getX()][(int)newWall.getY()] = newWall; 
                        }
                        incrementNumWalls();                                    
                        logger.appendText("A wall has been created and placed!\n"); 
                    }                                                       
                }              
            } 
            catch (InterruptedException e) 
            {
                Thread.currentThread().interrupt();
            }
        }, 0, wallBuildCooldown, TimeUnit.MILLISECONDS);
    }
    
    /*
     * This checks whether a wall can be placed in a 
     * specific square. If the square is already 
     * occupied, a wall cannot be built
     */
    private boolean checkWallPlacement(double x, double y)
    {
        boolean empty = false;

        if(board[(int)x][(int)y] == null)
        {
            empty = true;
        }
        for(Robot robot : robots)
        {
            if(robot.getX() == x && robot.getY() == y)
            {
                empty = false;
            }
        } 
        return empty;
    }

    /*
     * This method attempts to spawn robots and begin their movement
     */
    public void createRobots()
    {
        // Schedule a task to create a new robot every 1500 milliseconds
        scheduler.scheduleAtFixedRate(() -> 
        { 
            if(checkSpawn() != -1)
            {
                // Create a new robot  
                try 
                {
                    if(gameOver())
                    {
                        Thread.currentThread().interrupt();
                    }
                    spawnRobot();
                } 
                catch (InterruptedException e) 
                {
                    Thread.currentThread().interrupt();
                } 
            }     
        }, 0, 1500, TimeUnit.MILLISECONDS);      
    }

    /*
     * This method determines the coordinates to create the robot
     * on the map using checkSpawn
     */
    private void spawnRobot() throws InterruptedException
    {
        Random rand = new Random();
        // Random choice of 4 spawn locations
        int spawnLocation = checkSpawn();
        if(spawnLocation != -1) 
        {
            // Default values
            int x = -1;
            int y = -1;
            
            switch (spawnLocation) 
            {
                case 1:
                    x = 0;
                    y = 0;
                break;

                case 2:
                    x = 0;
                    y = 8;
                break;

                case 3:
                    x = 8;
                    y = 0;
                break;

                case 4:
                    x = 8;
                    y = 8;
                break;

                default:
                break;
            }           
            // Create Robot and add to board
            Robot newRobot = new Robot(x, y, rand.nextInt(500, 2000));
            logger.appendText("Robot has been created!\n");
            newRobot.setBoard(this);
            newRobot.incrementID();
            addRobot(newRobot);
            addProp(newRobot);
            board[x][y] = newRobot;
            // Start movement
            robotMovement(newRobot); 

            arena.boardProps = allProps;                                   
        }             
    }

    /*
     * This will check whether one of the starting positions are already
     * occupied by a robot before spawning another in
     */
    private int checkSpawn()
    {
        int empty = -1;
        Random rand = new Random();
        // Random choice between 1 - 4
        int num = rand.nextInt(1,5);

        switch(num)
        {
            case 1:
                 // If board space is null 
                if(board[0][0] == null)
                {
                    empty = 1;
                }
            break;

            case 2:
                // If board space is null 
                if(board[0][8] == null)
                {
                    empty = 2;
                }
            break;

            case 3:
                // If board space is null 
                if(board[8][0] == null)
                {
                    empty = 3;
                }
            break;

            case 4:
                // If board space is null 
                if(board[8][8] == null)
                {
                    empty = 4;
                }       
            break;

            default:
            break;
        }
        return empty;
    }

    /*
     * This method determines the movement of all robots
     */
    public void robotMovement(Robot robot) throws InterruptedException 
    {      
        // Submit movement tasks for each robot to the thread pool
        threadPool.execute(() -> 
        {
            try 
            {                   
                while(!gameOver())
                {
                    // Delay movement
                    Thread.sleep(robot.getDelay()); 
                    // Check if robot occupies wall square
                    checkInteraction(); 
                    // Move the robot
                    robot.moveRobot();                      
                    synchronized(mutex)
                    {
                        // Set the game boards position and make the previous slot now open for other robots to move
                        board[(int)robot.getPreviousX()][(int)robot.getPreviousY()] = null; 
                        board[(int)robot.getX()][(int)robot.getY()] = robot;                                                              
                    }   
                    updateBoard();                                                                                                                                                                                                       
                }   
                System.out.println("Game Over!");
                endThread();      
            } 
            catch (InterruptedException e) 
            {
                Thread.currentThread().interrupt();
            }          
        }); 
    }

    /*
     * This will check whether a robot has entered the
     * square occupied by a wall, in which case the robot
     * will damage the wall, but die
     */
    private void checkInteraction()
    {
        // Temp list to remove props to avoid concurrentmod exception
        List<Robot> robotsToRemove = new ArrayList<>();
        List<Wall> wallsToRemove = new ArrayList<>();
            
        // Search through all walls and robots
        for (Wall wall : walls) 
        {
            for(Robot robot : robots)
            {
                // If robot occupies wall coordinates
                if(wall.getX() == robot.getX() && wall.getY() == robot.getY()) 
                {            
                    // Damage wall and kill robot
                    robot.reduceHealth();
                    wall.reduceHealth();
                    logger.appendText("Wall has been collided with by a robot!\nWall Health: " + wall.getHealth() + "\n");  
                    if(wall.getHealth() == 0)
                    {
                        // Remove wall from the board
                        board[(int)wall.getX()][(int)wall.getY()] = null;
                        wallsToRemove.add(wall);
                        decrementNumWalls();                                  
                        // logger.appendText("Wall has been destroyed by a robot!\n");
                    }
                    if(robot.getHealth() == 0)
                    {
                        // Remove robot from the board
                        board[(int)robot.getX()][(int)robot.getY()] = null;
                        robotsToRemove.add(robot);
                        // JavaFX Thread needs to use run later                 
                        Platform.runLater(() ->
                        {
                            score.robotKillScore();
                        });                       
                    }
                }
            }         
        }           
        synchronized(mutex)
        {
            // Remove walls and robots using temp lists
            robots.removeAll(robotsToRemove);
            allProps.removeAll(robotsToRemove);
            walls.removeAll(wallsToRemove);
            allProps.removeAll(wallsToRemove); 
        }  
    }

    /*
     * This creates a new thread to run a task to 
     * continuously update the score label every second
     */
    public void scoreUpdate() throws InterruptedException
    {
        if(gameOver())
        {
            Thread.currentThread().interrupt();
        }
        else
        {         
            scoreUpdater.scheduleAtFixedRate(() -> 
            { 
                // JavaFX Thread needs to run later 
                // otherwise exception occurs
                Platform.runLater(() ->
                {
                    score.updateScore();
                }); 
            }, 0, 1000, TimeUnit.MILLISECONDS);   
        }          
    }

    /*
    * This method updates the label that shows how many
    * wall commands are in the queue
    */
    public void updateWallLabel() throws InterruptedException
    {
        if(gameOver())
        {
            Thread.currentThread().interrupt();
        }
        else
        {
            wallLabelUpdater.scheduleAtFixedRate(() -> 
            { 
                // JavaFX Thread needs to run later 
                // otherwise exception occurs
                Platform.runLater(() ->
                {
                    wallLabel.setText("Wall Commands in Queue: " + queue.size());
                }); 
            }, 0, 1000, TimeUnit.MILLISECONDS);    
        }        
    }

    /*
     * This check whether a robot occupies the citadel square
     * to determine if the game is over
     */
    public boolean gameOver() throws InterruptedException
    {
        boolean gameOver = false; 
        synchronized(mutex)
        {
            for(Robot robot : robots)
            {
                // If a robot occupies the citadel square
                if(robot.getX() == 4 && robot.getY() == 4)
                {
                    gameOver = true;
                }
            }  
        }  
        if(gameOver) 
        {
            // Display a game over popup
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Game Over");
                alert.setHeaderText("Game Over");
                alert.setContentText("The game is over!\nFinal Score: " + score.getScore()
                + "\nExit Game 'X' at the Top Right\"");
                alert.showAndWait();
            }); 
        }         
        return gameOver;
    }

    /*
     * This determines if game is still running
     */
    public boolean gameRunning() throws InterruptedException
    {
        boolean gameRunning = true;
        if(gameOver())
        {
            gameRunning = false;
        }
        return gameRunning;
    }
    
    /*
     * This will gracefully end the threads once the game is over
     */
    public void endThread() throws InterruptedException
    {
        synchronized(mutex)
        {
            scoreUpdater.shutdown();
            scoreUpdater.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
            System.out.println("Score Updater OFF: " + scoreUpdater.isShutdown());

            wallLabelUpdater.shutdown();
            wallLabelUpdater.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
            System.out.println("wallLabelUpdater OFF: " + wallLabelUpdater.isShutdown());

            wallSchedule.shutdown();
            wallSchedule.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
            System.out.println("wallSchedule OFF: " + wallSchedule.isShutdown());

            scheduler.shutdown();
            scheduler.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
            System.out.println("scheduler OFF: " + scheduler.isShutdown());

            threadPool.shutdownNow();  
            System.out.println("threadPool OFF: " + threadPool.isShutdown());
        }      
    }

    /*
     * Using arena object, will request layout to update board graphics
     */
    public void updateBoard()
    {
        synchronized(mutex)
        {
            arena.requestLayout();
        }      
    }

    /*
     * This add robots to the robots list
     */
    private void addRobot(Robot robot)
    {
        robots.add(robot);     
    }

    /*
     * This add walls to the walls list
     */
    public void addWall(Wall wall)
    {
        walls.add(wall);        
    }

    /*
     * This add Props to the props list
     */
    public void addProp(Prop prop)
    {
        synchronized(mutex)
        {
            allProps.add(prop);
        }
    }

    /*
     * This incremements variable numWalls
     */
    public void incrementNumWalls()
    {
        numWalls++;     
    }

    /*
     * This decrements variable numWalls
     */
    public void decrementNumWalls()
    {
        synchronized(mutex)
        {
            numWalls--;
        }  
    }
    /*
     * Returns list of robots
     */
    public List<Robot> getRobots()
    {
        return robots;
    }

    /*
     * Returns list of walls
     */
    public List<Wall> getWalls()
    {
        return walls;
    }
}
