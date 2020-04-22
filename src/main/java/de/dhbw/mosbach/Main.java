package de.dhbw.mosbach;

import de.dhbw.mosbach.gui.MenuSceneController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class Main extends Application {

    public static void main(final String[] args) {
        launch(args);
    }

    @Override
    public void start(final Stage primaryStage) throws Exception {
        primaryStage.setTitle("Yajisan-Kazusan");
        primaryStage.getIcons().add(new Image(Objects.requireNonNull(getClass().getClassLoader().getResource("images/icon.png")).toExternalForm()));

        final FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("fxml/MenuScene.fxml"));
        Parent menu = null;
        try {
            menu = loader.load();
        } catch (IOException e) {
            System.exit(0);
        }

        final Scene menuScene = new Scene(menu);
        final MenuSceneController menuSceneController=loader.getController();
        menu.setOnKeyPressed(menuSceneController::onKeyboardPress);
        menuScene.getStylesheets().add(Objects.requireNonNull(getClass().getClassLoader().getResource("css/style.css")).toExternalForm());
        primaryStage.setScene(menuScene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }
}
