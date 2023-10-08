package com.game.connect4game;

import com.game.connect4game.Controller;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {

    private Controller controller;
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("game.fxml"));
        GridPane rootNode = loader.load();
        controller = loader.getController();



        controller.createPlayground();


        Pane menupane = (Pane) rootNode.getChildren().get(0);

        MenuBar menubar = createMenu();
        menubar.prefWidthProperty().bind(stage.widthProperty());
        menupane.getChildren().add(menubar);

        Scene scene = new Scene(rootNode);
        stage.setScene(scene);
        stage.setTitle("Connect Four");
        stage.setResizable(false);
        stage.show();
    }


    public MenuBar createMenu() {
        Menu fileMenu = new Menu("File");

        MenuItem newGameMenuItem = new MenuItem("New Game");
        MenuItem resetGameMenuItem = new MenuItem("Reset Game");
        SeparatorMenuItem seperator = new SeparatorMenuItem();
        MenuItem exitGameMenuItem = new MenuItem("Exit Game");

        newGameMenuItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                controller.resetGame();
            }
        });

        resetGameMenuItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {

                controller.resetGame();
            }
        });

        exitGameMenuItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                Platform.exit();
                System.exit(0);
            }
        });

        MenuBar menubar = new MenuBar();
        fileMenu.getItems().addAll(newGameMenuItem, resetGameMenuItem, seperator, exitGameMenuItem);

//		Help menu
        Menu helpMenu = new Menu("Help");
        MenuItem aboutGame = new MenuItem("About Game");
        SeparatorMenuItem seperatoritem = new SeparatorMenuItem();
        MenuItem aboutMe = new MenuItem("About Me");

        aboutMe.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                aboutMyself();
            }
        });

        aboutGame.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                aboutConnectFour();
            }
        });

        helpMenu.getItems().addAll(aboutGame, seperatoritem, aboutMe);

        menubar.getMenus().addAll(fileMenu, helpMenu);

        return menubar;

    }

    public void resetGame() {

    }

    public void aboutMyself(){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About The Developer");
        alert.setHeaderText("John Doe");
        alert.setContentText("Connect 4 is a two player game where the objective is to get four of your "+
                "colored checkers in a row vertically, horizontally, or diagonally in the " +
                "grid used as the \"game board.\"  Each player takes turns putting one of");
        alert.show();
    }

    public void aboutConnectFour() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About Game");
        alert.setHeaderText("How to Play?");
        alert.setContentText("Connect 4 is a two player game where the objective is to get four of your "+
                "colored checkers in a row vertically, horizontally, or diagonally in the " +
                "grid used as the \"game board.\"  Each player takes turns putting one of"  +
                " their colored checkers in the 6 x 7 grid.  Similarly to tic-tac-toe, players"+
                " want to get four of their colored checkers in a row, while also preventing their"+
                " opponent from getting four of their colored checkers in a row.  The first player to"+
                " put four of their checkers in a row vertically, horizontally, or diagonally wins.  ");
        alert.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}