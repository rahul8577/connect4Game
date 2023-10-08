package com.game.connect4game;

import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.util.Duration;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Controller implements Initializable {

    private static final int ROW = 6;
    private static final int COLUMN = 7;
    private static final int CIRCLE_DIAMETER = 80;
    private static final String discColor1 = "#24303E";
    private static final String discColor2 = "#4CAA88";

    @FXML
    public TextField playerOneField;
    @FXML
    public  TextField playerTwoField;
    @FXML
    public Button startButton;

    public static String PLAYER_ONE = "Player One"; //this can be replace by player name
    public static String PLAYER_TWO = "Player Two";

    private boolean isPlayerOne = true;

    private boolean isAllowedToInsert=true;

    @FXML
    public GridPane rootGridPane;

    public Pane insertDiscPane;

    @FXML
    public Label playerNameLabel;

    public Disc[][] insertedDiscArray = new Disc[ROW][COLUMN];

    public void createPlayground() {



        Shape rectangle = new Rectangle((COLUMN + 1) * CIRCLE_DIAMETER, (ROW + 1) * CIRCLE_DIAMETER);

        for (int row = 0; row < ROW; row++) {
            for (int col = 0; col < COLUMN; col++) {
                Circle circle = new Circle();
                circle.setRadius(CIRCLE_DIAMETER / 2);
                circle.setCenterX(CIRCLE_DIAMETER / 2);
                circle.setCenterY(CIRCLE_DIAMETER / 2);

                circle.setTranslateX(col * (CIRCLE_DIAMETER + 5) + CIRCLE_DIAMETER / 4);
                circle.setTranslateY(row * (CIRCLE_DIAMETER + 5) + CIRCLE_DIAMETER / 4);

                rectangle = Shape.subtract(rectangle, circle);
                rectangle.setFill(Color.WHITE);

            }
        }
        rootGridPane.add(rectangle, 0, 1);

        startButton.setOnAction(event->{
            PLAYER_ONE=playerOneField.getText();
            PLAYER_TWO=playerTwoField.getText();
        });

        List<Rectangle> colRectangleList = createClickableColumns();

        for (Rectangle colRectangle : colRectangleList) {
            rootGridPane.add(colRectangle, 0, 1);
        }

    }



    private List<Rectangle> createClickableColumns() {

        List<Rectangle> rectangleList = new ArrayList<>();

        for (int col = 0; col < COLUMN; col++) {

            Rectangle rectangle = new Rectangle(CIRCLE_DIAMETER, (ROW + 1) * CIRCLE_DIAMETER);
            rectangle.setFill(Color.TRANSPARENT);
            rectangle.setTranslateX(col * (CIRCLE_DIAMETER + 5) + CIRCLE_DIAMETER / 4);

            rectangleList.add(rectangle);

            rectangle.setOnMouseEntered(event -> {
                rectangle.setFill(Color.valueOf("#eeeeee26"));
            });

            rectangle.setOnMouseExited(event -> {
                rectangle.setFill(Color.TRANSPARENT);
            });

            final int column = col;
            rectangle.setOnMouseClicked(event -> {
                if(isAllowedToInsert){
                    isAllowedToInsert=false;
                    insertDisc(new Disc(isPlayerOne), column);
                }
            });

        }
        return rectangleList;
    }

    public void insertDisc(Disc disc, int column) {

        int row = ROW - 1;

        while (row > 0) {
            if (insertedDiscArray[row][column] == null) {
                break;
            }
            row--;
        }

        if (row < 0) {
            return;
        }
        insertedDiscArray[row][column] = disc;
        insertDiscPane.getChildren().add(disc);
        disc.setTranslateX(column * (CIRCLE_DIAMETER + 5) + CIRCLE_DIAMETER / 4);
        TranslateTransition transition = new TranslateTransition(Duration.seconds(0.5), disc);

        transition.setToY(row * (CIRCLE_DIAMETER + 5) + CIRCLE_DIAMETER / 4);

        transition.setOnFinished(event -> {
            isAllowedToInsert=true;

            isPlayerOne = !isPlayerOne;
            playerNameLabel.setText(isPlayerOne ? PLAYER_ONE : PLAYER_TWO);

        });

        if (gameIsEnded(row, column)) {
            gameOver();
        }

        transition.play();
    }

    public void gameOver(){
        String winner=isPlayerOne?PLAYER_ONE:PLAYER_TWO;
        Alert alert=new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Connect Four");
        alert.setHeaderText("Winner is "+winner );
        alert.setContentText("Want Play Again?");

        ButtonType btnYes=new ButtonType("Yes");
        ButtonType btnNo=new ButtonType("No, Exit");

        alert.getButtonTypes().setAll(btnYes,btnNo);
        Optional<ButtonType> btnClicked=alert.showAndWait();

//		Platform.runLater(()->{
        if(btnClicked.isPresent()&&btnClicked.get()==btnYes) {
            resetGame();
        }else{
            Platform.exit();
            System.exit(0);
        }

//		});
    }

    public void resetGame(){
        insertDiscPane.getChildren().clear();

        for(int row=0; row < insertedDiscArray.length; row++){
            for(int collumn=0; collumn<insertedDiscArray[row].length; collumn++){
                insertedDiscArray[row][collumn]=null;
            }
        }
        isPlayerOne=true;

        playerNameLabel.setText(PLAYER_ONE);
        createPlayground();

    }

    private boolean checkCombinations(List<Point2D> points) {

        int chain = 0;

        for (Point2D point : points) {

            int rowIndexForArray = (int) point.getX();
            int columnIndexForArray = (int) point.getY();

            Disc disc = getDiscIfPresent(rowIndexForArray, columnIndexForArray);

            if (disc != null && disc.isPlayerOneMove == isPlayerOne) {  // if the last inserted Disc belongs to the current player

                chain++;
                if (chain == 4) {
                    return true;
                }
            } else {
                chain = 0;
            }
        }

        return false;
    }

    private Disc getDiscIfPresent(int row, int column) {    // To prevent ArrayIndexOutOfBoundException

        if (row >= ROW || row < 0 || column >= COLUMN || column < 0)  // If row or column index is invalid
            return null;

        return insertedDiscArray[row][column];
    }

    public boolean gameIsEnded(int row, int column) {
        List<Point2D> verticalPoints = IntStream.rangeClosed(row - 3, row + 3)  // If, row = 3, column = 3, then row = 0,1,2,3,4,5,6
                .mapToObj(r -> new Point2D(r, column))  // 0,3  1,3  2,3  3,3  4,3  5,3  6,3 [ Just an example for better understanding ]
                .collect(Collectors.toList());

        List<Point2D> horizontalPoints = IntStream.rangeClosed(column - 3, column + 3)
                .mapToObj(col -> new Point2D(row, col))
                .collect(Collectors.toList());

        Point2D startPoint1 = new Point2D(row - 3, column + 3);
        List<Point2D> diagonal1Points = IntStream.rangeClosed(0, 6)
                .mapToObj(i -> startPoint1.add(i, -i))
                .collect(Collectors.toList());

        Point2D startPoint2 = new Point2D(row - 3, column - 3);
        List<Point2D> diagonal2Points = IntStream.rangeClosed(0, 6)
                .mapToObj(i -> startPoint2.add(i, i))
                .collect(Collectors.toList());

        boolean isEnded = checkCombinations(verticalPoints) || checkCombinations(horizontalPoints)
                || checkCombinations(diagonal1Points) || checkCombinations(diagonal2Points);

        return isEnded;
    }

    private static class Disc extends Circle {

        private final boolean isPlayerOneMove;

        Disc(boolean isPlayerOneMove) {
            this.isPlayerOneMove = isPlayerOneMove;
            setRadius(CIRCLE_DIAMETER / 2);
            setFill(isPlayerOneMove ? Color.valueOf(discColor1) : Color.valueOf(discColor2));
            setCenterX(CIRCLE_DIAMETER / 2);
            setCenterY(CIRCLE_DIAMETER / 2);
        }


    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
}