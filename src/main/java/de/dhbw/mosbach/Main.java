package de.dhbw.mosbach;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent menu = FXMLLoader.load(getClass().getClassLoader().getResource("MenuScene.fxml"));
        primaryStage.setTitle("Yajisan-Kazusan");
        primaryStage.setScene(new Scene(menu));
        primaryStage.show();
    }
}
