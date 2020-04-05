package de.dhbw.mosbach;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.util.Objects;

public class Main extends Application {

    public static void main(final String[] args) {
        launch(args);
    }

    @Override
    public void start(final Stage primaryStage) throws Exception {
        primaryStage.setTitle("Yajisan-Kazusan");
        primaryStage.getIcons().add(new Image(Objects.requireNonNull(getClass().getClassLoader().getResource("images/icon.png")).toExternalForm()));

        final Parent menu = FXMLLoader.load(Objects.requireNonNull(getClass().getClassLoader().getResource("fxml/MenuScene.fxml")));
        final Scene menuScene = new Scene(menu);
        menuScene.getStylesheets().add(Objects.requireNonNull(getClass().getClassLoader().getResource("css/style.css")).toExternalForm());
        primaryStage.setScene(menuScene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }
}
