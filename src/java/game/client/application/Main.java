package game.client.application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;
import java.nio.file.Paths;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        URL url = Paths.get("src/res/fxml/startScreen.fxml").toUri().toURL();
        Parent root = FXMLLoader.load(url);
        primaryStage.setTitle("TicTacToe");
        primaryStage.setScene(new Scene(root, 1280, 720));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

    public static void loadScene(Parent next, Parent now) {
        Scene scene = new Scene(next, 1280, 720);
        Stage stage = (Stage) now.getScene().getWindow();
        stage.setScene(scene);
    }
}
