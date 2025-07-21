package edu.curtin.saed.assignment1;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.stage.Stage;


/*
 * Jaron Rose 18823978
 * Provided main class to use for assignment:
 * Minor changes / additions were made
 */

public class App extends Application 
{
    
    public static void main(String[] args) 
    {
        launch();        
    }
    
    @Override
    public void start(Stage stage) throws InterruptedException
    {     
        stage.setTitle("SEC Assignment 2023 Jaron Rose 18823978");

        Label label = new Label("Score: ");
        Label wallLabel = new Label("Wall Commands: ");
        Score score = new Score(label);
        TextArea logger = new TextArea();
        JFXArena arena = new JFXArena();
        GameBoard game = new GameBoard(arena, logger, score, wallLabel);
        logger.setEditable(false);

        arena.setCitadellPosition();
        game.createRobots();
        game.scoreUpdate();
        game.updateWallLabel();

        stage.setOnCloseRequest(event -> 
        {
            // Display a confirmation dialog
            Alert alert = new Alert(AlertType.CONFIRMATION);
            alert.setTitle("Confirm Exit");
            alert.setHeaderText("Confirm Exit");
            alert.setContentText("Are you sure you want to exit?");

            ButtonType result = alert.showAndWait().orElse(ButtonType.CANCEL);

            if (result.equals(ButtonType.OK)) 
            {
                // Terminate the application
                Platform.exit();                          
            } 
            else 
            {
                // Cancel the close request
                event.consume();
            }
        });

        ToolBar toolbar = new ToolBar();           
        toolbar.getItems().addAll(label);
        toolbar.getItems().addAll(wallLabel);
                          
        SplitPane splitPane = new SplitPane();
        splitPane.getItems().addAll(arena, logger);
        arena.setMinWidth(300.0);
        
        BorderPane contentPane = new BorderPane();
        contentPane.setTop(toolbar);
        contentPane.setCenter(splitPane);
        
        Scene scene = new Scene(contentPane, 800, 800);

        arena.addListener((x, y) ->
        {
            try 
            {
                if(game.gameRunning())
                {
                    System.out.println("Arena click at (" + x + "," + y + ")");
                    arena.makeWall(x, y);
                }
                else
                {
                    scene.removeEventFilter(MouseEvent.ANY, MouseEvent::consume);
                    scene.removeEventFilter(KeyEvent.ANY, KeyEvent::consume);
                }
            } 
            catch (InterruptedException e) 
            {
                Thread.currentThread().interrupt();
            }         
        });

        stage.setScene(scene);
        stage.show();              
    }
}
