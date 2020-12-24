package game.client.controllers;

import game.client.application.Main;
import game.server.utils.Singleton;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.*;
import java.nio.file.Paths;
import java.util.ResourceBundle;

public class StartController implements Initializable {

    //fox - 1 dog - 2
    private int chooseCharacter = Integer.MAX_VALUE;
    private Integer port = null;
    private boolean portIsHidden = true;

    @FXML
    private ImageView imageViewOne;
    @FXML
    private ImageView imageViewTwo;
    @FXML
    private GridPane root;
    @FXML
    public TextField portField;
    @FXML
    public TextField username;

    @FXML
    public void playerButtonAction() {
        if (portIsHidden) {
            portField.setVisible(true);
            portIsHidden = false;
        } else {
            String portText = portField.getText();
            if (portText != null && !portText.equals("")) {
                try {
                    port = Integer.parseInt(portText);
                    if (alertUsername() && alertCharacter()) {
                        String response = username.getText() + "," + chooseCharacter;
                        System.out.println(response);
                        Socket socket = new Socket("localhost", port);
                        Singleton.getInstance(socket);
                        new PrintWriter(socket.getOutputStream(), true).println(response);
                        URL url = Paths.get("src/res/fxml/waitScreen.fxml").toUri().toURL();
                        Main.loadScene(FXMLLoader.load(url), root);
                    }
                } catch (NumberFormatException | UnknownHostException | ConnectException e) {
                    alertPort();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                alertPort();
            }
        }

    }

    @FXML
    public void dogHasChosen() throws URISyntaxException, MalformedURLException {
        if (chooseCharacter == 1) {
            URL url = Paths.get("src/res/pictures/sid-eye-start.png").toUri().toURL();
            File file = new File(url.toURI());
            Image imageOne = new Image(file.toURI().toString());
            imageViewOne.setImage(imageOne);
            File fileTwo = new File("src/res/pictures/abr-eye-start-selected.png");
            Image imageTwo = new Image(fileTwo.toURI().toString());
            imageViewTwo.setImage(imageTwo);
            chooseCharacter = 2;
        } else {
            if (chooseCharacter == 2) {
                chooseCharacter = Integer.MAX_VALUE;
                File fileTwo = new File("src/res/pictures/abr-eye-start.png");
                Image imageTwo = new Image(fileTwo.toURI().toString());
                imageViewTwo.setImage(imageTwo);
            } else {
                File fileTwo = new File("src/res/pictures/abr-eye-start-selected.png");
                Image imageTwo = new Image(fileTwo.toURI().toString());
                imageViewTwo.setImage(imageTwo);
                chooseCharacter = 2;
            }
        }
    }

    @FXML
    public void foxHasChosen() {
        if (chooseCharacter == 2) {
            File fileTwo = new File("src/res/pictures/abr-eye-start.png");
            Image imageTwo = new Image(fileTwo.toURI().toString());
            imageViewTwo.setImage(imageTwo);
            File file = new File("src/res/pictures/sid-eye-start-selected.png");
            Image imageOne = new Image(file.toURI().toString());
            imageViewOne.setImage(imageOne);
            chooseCharacter = 1;
        } else {
            if (chooseCharacter == 1) {
                chooseCharacter = Integer.MAX_VALUE;
                File file = new File("src/res/pictures/sid-eye-start.png");
                Image imageOne = new Image(file.toURI().toString());
                imageViewOne.setImage(imageOne);
            } else {
                File file = new File("src/res/pictures/sid-eye-start-selected.png");
                Image imageOne = new Image(file.toURI().toString());
                imageViewOne.setImage(imageOne);
                chooseCharacter = 1;
            }
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        URL picUrl = null;
        try {
            picUrl = Paths.get("src/res/pictures/sid-eye.png").toUri().toURL();
            File file = new File(picUrl.toURI());
            Image imageOne = new Image(file.toURI().toString());
            imageViewOne.setImage(imageOne);
            File fileTwo = new File("src/res/pictures/abr-eye.png");
            Image imageTwo = new Image(fileTwo.toURI().toString());
            imageViewTwo.setImage(imageTwo);
            portField.setVisible(false);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    //HELPERS
    private boolean alertUsername() {
        if (username.getText() == null || username.getText().equals("") || username.getText().contains(",")) {
            username.setText(null);
            username.setPromptText("Please enter correct username");
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error!");
            alert.setContentText("Please enter correct username. Check that you didn't use ','");
            alert.showAndWait();
            return false;
        }
        return true;
    }

    private boolean alertCharacter() {
        if (chooseCharacter == Integer.MAX_VALUE) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error!");
            alert.setContentText("Please choose character");
            alert.showAndWait();
            return false;
        }
        return true;
    }

    private void alertPort() {
        portField.setText(null);
        portField.setPromptText("Please enter correct port");
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error!");
        alert.setContentText("Please enter correct host / Check entered port");
        alert.showAndWait();
    }
}
