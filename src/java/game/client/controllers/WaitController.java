package game.client.controllers;

import game.client.application.Main;
import game.server.utils.Singleton;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Circle;

import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ResourceBundle;
import java.util.Scanner;

public class WaitController implements Initializable {

    @FXML
    private HBox root;
    @FXML
    private Circle circle1;
    @FXML
    private Circle circle2;
    @FXML
    private Circle circle3;

    private Scanner scanner;
    private Socket socket;
    private PrintWriter printWriter;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        socket = Singleton.getInstance(null).socket;
        try {
            scanner = new Scanner(socket.getInputStream());
            printWriter = new PrintWriter(socket.getOutputStream(), true);
            ServerListener serverListener = new ServerListener();
            serverListener.start();
            serverListener.setOnSucceeded(t -> {
                if (t.getSource().getValue().equals("nextScreen")) {
                    try {
                        printWriter.println("Move to gameScreen");
                        URL sceneUrl = Paths.get("src/res/fxml/gameScreen.fxml").toUri().toURL();
                        Main.loadScene(FXMLLoader.load(sceneUrl), root);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class ServerListener extends Service<String> {

        @Override
        protected Task<String> createTask() {
            return new Task() {
                @Override
                protected String call() {
                    while (!socket.isInputShutdown()) {
                        if (scanner.hasNextLine()) {
                            String response = scanner.nextLine();
                            if (response.equals("next")) {
                                return "nextScreen";
                            }
                        }
                    }
                    return null;
                }
            };
        }
    }
}
