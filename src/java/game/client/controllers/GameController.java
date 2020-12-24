package game.client.controllers;

import game.server.utils.Singleton;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.text.TextAlignment;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ResourceBundle;
import java.util.Scanner;

public class GameController implements Initializable {
    @FXML
    private ImageView board;
    @FXML
    private ImageView imageViewOne;
    @FXML
    private ImageView imageViewTwo;
    @FXML
    private Label labelWarrior;
    @FXML
    private Label figureMe;
    @FXML
    private Label figureWarrior;
    @FXML
    private ComboBox<String> emotionBox;
    @FXML
    private StackPane stackPane1;
    @FXML
    private StackPane stackPane2;
    @FXML
    private StackPane stackPane3;
    @FXML
    private StackPane stackPane4;
    @FXML
    private StackPane stackPane5;
    @FXML
    private StackPane stackPane6;
    @FXML
    private StackPane stackPane7;
    @FXML
    private StackPane stackPane8;
    @FXML
    private StackPane stackPane9;

    private Scanner scanner;
    private Socket socket;
    private PrintWriter printWriter;
    private int warrior;
    private int me;
    private StackPane[] array = new StackPane[9];
    private boolean myMove = false;
    private int figure = Integer.MIN_VALUE;
    private boolean[] wasMove = new boolean[9];

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        socket = Singleton.getInstance(null).socket;
        try {
            scanner = new Scanner(socket.getInputStream());
            printWriter = new PrintWriter(socket.getOutputStream(), true);
            addStackPaneToArray();

            ServerDataOfUsersService serverListener = new ServerDataOfUsersService();
            serverListener.start();
            serverListener.setOnSucceeded(t -> {
                System.out.println((String) t.getSource().getValue());
                String response = (String) t.getSource().getValue();
                String[] data = response.split(",");
                labelWarrior.setText(data[3]);
                labelWarrior.setTextAlignment(TextAlignment.CENTER);
                if (data[2].equals("1")) {
                    setSidImage(imageViewOne, "default");
                    me = 1;
                } else {
                    setAbrImage(imageViewOne, "default");
                    me = 2;
                }
                if (data[4].equals("1")) {
                    setSidImage(imageViewTwo, "default");
                    warrior = 1;
                } else {
                    setAbrImage(imageViewTwo, "default");
                    warrior = 2;
                }
                if (data[5].equals("X")) {
                    figureMe.setText(" X");
                    figure = 1;
                    myMove = true;
                    figureWarrior.setText(" O");
                } else {
                    figureMe.setText(" O");
                    myMove = false;
                    figure = 2;
                    figureWarrior.setText(" X");
                }
            });

            GameDataService gameListener = new GameDataService();
            gameListener.start();
            gameListener.setOnSucceeded(t -> {
                System.out.println((String) t.getSource().getValue());
                String result = (String) t.getSource().getValue();
                String[] split = result.split(",");
                if (split[0].equals("Win")) {
                    myMove = false;
                    if (split[1].equals("nobody")) {
                        System.out.println("nobody");
                        showAlertWin("Nobody didn't won 0_o");
                    } else {
                        if (split[1].equals("" + figure)) {
                            showAlertWin("You won!!!");
                        } else {
                            System.out.println("You lose :(");
                        }
                    }
                    gameListener.cancel();
                } else {
                    if (split[0].equals("Emotion")) {
                        switch (warrior) {
                            case 1: {
                                setSidImage(imageViewTwo, split[1]);
                                break;
                            }
                            case 2: {
                                setAbrImage(imageViewTwo, split[1]);
                                break;
                            }
                        }
                    } else {
                        System.out.println("drawing");
                        int figure2 = Integer.parseInt(split[1]);
                        int where = Integer.parseInt(split[2]);
                        drawFigure(array[where], figure2, where);
                        myMove = true;
                    }
                    gameListener.restart();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void uploadEmotion() {
        switch (me) {
            case 1: {
                setSidImage(imageViewOne, emotionBox.getValue());
                break;
            }
            case 2: {
                setAbrImage(imageViewOne, emotionBox.getValue());
                break;
            }
        }
        printWriter.println("Emotion," + emotionBox.getValue());
        System.out.println("Emotion," + emotionBox.getValue());
    }

    private void showAlertWin(String what) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("GameOver");
        alert.setContentText(what);
        alert.show();
    }

    private void setSidImage(ImageView image, String which) {
        try {
            URL url = Paths.get("src/res/pictures/sid-" + which + ".png").toUri().toURL();
            File file = new File(url.toURI());
            Image imageOne = new Image(file.toURI().toString());
            image.setImage(imageOne);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    private void setAbrImage(ImageView image, String which) {
        try {
            URL url = Paths.get("src/res/pictures/abr-" + which + ".png").toUri().toURL();
            File file = new File(url.toURI());
            Image imageOne = new Image(file.toURI().toString());
            image.setImage(imageOne);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    private void drawFigure(StackPane stackPane, int figure, int which) {
        ImageView imageView = null;
        if (figure == 2) {
            File file = new File("src/res/pictures/circle.png");
            Image image = new Image(file.toURI().toString());
            imageView = new ImageView(image);
            imageView.setScaleX(1.5);
            imageView.setScaleY(1.5);
        }
        if (figure == 1) {
            File file = new File("src/res/pictures/cross.png");
            Image image = new Image(file.toURI().toString());
            imageView = new ImageView(image);
            imageView.setScaleX(1.5);
            imageView.setScaleY(1.5);
        }
        stackPane.getChildren().add(imageView);
        wasMove[which] = true;
        System.out.println("Drew");
    }

    private void addStackPaneToArray() {
        array[0] = stackPane1;
        array[1] = stackPane2;
        array[2] = stackPane3;
        array[3] = stackPane4;
        array[4] = stackPane5;
        array[5] = stackPane6;
        array[6] = stackPane7;
        array[7] = stackPane8;
        stackPane1.setOnMouseClicked(t -> {
            if (myMove &&!wasMove[0]) {
                drawFigure(stackPane1, figure, 0);
                printWriter.println("Motion," + figure + "," + 0);
                myMove = false;
            }
        });
        stackPane2.setOnMouseClicked(t -> {
            if (myMove &&!wasMove[1]) {
                drawFigure(stackPane2, figure, 1);
                printWriter.println("Motion," + figure + "," + 1);
                myMove = false;
            }
        });
        stackPane3.setOnMouseClicked(t -> {
            if (myMove &&!wasMove[2]) {
                drawFigure(stackPane3, figure, 2);
                printWriter.println("Motion," + figure + "," + 2);
                myMove = false;
            }
        });
        stackPane4.setOnMouseClicked(t -> {
            if (myMove &&!wasMove[3]) {
                drawFigure(stackPane4, figure, 3);
                printWriter.println("Motion," + figure + "," + 3);
                myMove = false;
            }
        });
        stackPane5.setOnMouseClicked(t -> {
            if (myMove &&!wasMove[4]) {
                drawFigure(stackPane5, figure, 4);
                printWriter.println("Motion," + figure + "," + 4);
                myMove = false;
            }
        });
        stackPane6.setOnMouseClicked(t -> {
            if (myMove &&!wasMove[5]) {
                drawFigure(stackPane6, figure, 5);
                printWriter.println("Motion," + figure + "," + 5);
                myMove = false;
            }
        });
        stackPane7.setOnMouseClicked(t -> {
            if (myMove &&!wasMove[6]) {
                drawFigure(stackPane7, figure, 6);
                printWriter.println("Motion," + figure + "," + 6);
                myMove = false;
            }
        });
        stackPane8.setOnMouseClicked(t -> {
            if (myMove &&!wasMove[7]) {
                drawFigure(stackPane8, figure, 7);
                printWriter.println("Motion," + figure + "," + 7);
                myMove = false;
            }
        });
        stackPane9.setOnMouseClicked(t -> {
            if (myMove &&!wasMove[8]) {
                drawFigure(stackPane9, figure, 8);
                printWriter.println("Motion," + figure + "," + 8);
                myMove = false;
            }
        });

    }

    private class ServerDataOfUsersService extends Service<String> {
        @Override
        protected Task<String> createTask() {
            return new Task() {
                @Override
                protected String call() {
                    while (!socket.isInputShutdown()) {
                        if (scanner.hasNextLine()) {
                            String response = scanner.nextLine();
                            if (response.startsWith("DataUser")) {
                                return response;
                            }
                        }
                    }
                    return null;
                }
            };
        }
    }

    private class GameDataService extends Service<String> {

        @Override
        protected Task<String> createTask() {
            return new Task() {
                @Override
                protected String call() {
                    while (!socket.isInputShutdown()) {
                        if (scanner.hasNextLine()) {
                            String response = scanner.nextLine();
                            if (response.startsWith("Emotion,") || response.startsWith("Motion,") || response.startsWith("Win,")) {
                                System.out.println(response);
                                return response;
                            }
                        }
                    }
                    return null;
                }
            };
        }
    }
}
