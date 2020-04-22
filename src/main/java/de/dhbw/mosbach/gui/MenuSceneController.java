package de.dhbw.mosbach.gui;

import de.dhbw.mosbach.file.parser.JSONMatchFieldParser;
import de.dhbw.mosbach.file.parser.MatchFieldParser;
import de.dhbw.mosbach.file.validator.JSONFileValidator;
import de.dhbw.mosbach.solve.YajisanKazusanSolver;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.input.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

public class MenuSceneController {

    @FXML
    private TextField filePathField;

    @FXML
    public void handleChooseFile(final ActionEvent event) {
        final Stage activeStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        final FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("JSON mit Spielfeld auswählen...");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON-Dateien", "*.json", "*.JSON"));
        final File selectedFile = fileChooser.showOpenDialog(activeStage);
        if (selectedFile != null) {
            filePathField.setText(selectedFile.getAbsolutePath());
        }
    }

    @FXML
    public void handleStartMainScene(final ActionEvent event) {
        startSolverScene();
    }

    //Menu-Bar
    @FXML
    public void handleHelp() {
        final Alert manualAlert = new Alert(Alert.AlertType.INFORMATION);
        manualAlert.setTitle("Nutzungsinformationen");
        manualAlert.setHeaderText("Anleitung zur Bedienung des Yajisan-Kazusan-Lösers");
        final String userManual =
                "Dieses Programm kann das Yajisan-Kazusan Rätsel lösen.\n" +
                        "Öffne hierzu einfach auf dem Startbildschirm eine JSON-Datei.\n" +
                        "Ziehe diese hierzu in das Fenster oder gebe den Pfad ein.\n" +
                        "Zum betrachten der Lösung drücke dann auf \"Spiel lösen\"\n\n" +
                        "Viel Spaß beim Lösen der Rätsel!";
        manualAlert.setContentText(userManual);
        manualAlert.showAndWait();
    }

    @FXML
    public void handleExitProgram() {
        System.exit(0);
    }

    //Drag and Drop
    @FXML
    public void handleDragDroppedEvent(final DragEvent event) {
        final Dragboard db = event.getDragboard();
        if (db.hasFiles()) {
            final List<File> files = db.getFiles();
            final int acceptedFileSize = 1;
            if (files.size() == acceptedFileSize) {
                filePathField.setText(files.get(0).getAbsolutePath());
                event.setDropCompleted(true);
            }
        }
        event.consume();
    }

    @FXML
    public void handleDragOverEvent(final DragEvent event) {
        final Dragboard db = event.getDragboard();
        if (event.getDragboard().hasFiles()) {
            final List<File> files = db.getFiles();
            final int acceptedFileSize = 1;
            if (files.size() == acceptedFileSize && new JSONFileValidator(files.get(0).getAbsolutePath()).getValidationResult() == JSONFileValidator.ValidationResult.VALID_FILE) {
                event.acceptTransferModes(TransferMode.ANY);
            }
        }
        event.consume();
    }

    @FXML
    public void onKeyboardPress(final KeyEvent event) {
        if (event.getCode() == KeyCode.F5 || event.getCode() == KeyCode.F6) {
            startSolverScene();
        } else if (event.getCode() == KeyCode.F8) {
            System.exit(0);
        }
    }

    private void startSolverScene() {
        final JSONFileValidator analyzer = new JSONFileValidator(filePathField.getText());
        if (analyzer.getValidationResult() != JSONFileValidator.ValidationResult.VALID_FILE) {
            final Alert fileNotValidAlert = new Alert(Alert.AlertType.ERROR);
            fileNotValidAlert.setTitle("Unpassende Datei ausgewählt!");
            fileNotValidAlert.setHeaderText(null);
            fileNotValidAlert.setContentText(analyzer.getValidationResult().getErrorMessage());
            fileNotValidAlert.showAndWait();
            return;
        }
        final MatchFieldParser parser = new JSONMatchFieldParser(filePathField.getText());
        final MatchFieldParser.ParsingValidationResult result = parser.getParsingValidationResult();
        if (result != MatchFieldParser.ParsingValidationResult.PARSED_SUCCESSFUL) {
            final Alert fileNotValidAlert = new Alert(Alert.AlertType.ERROR);
            fileNotValidAlert.setTitle("Datei ist korrupt!");
            fileNotValidAlert.setHeaderText(null);
            if (result == null) {
                fileNotValidAlert.setContentText("Ein unerwarteter Fehler ist aufgetreten!");
            } else {
                fileNotValidAlert.setContentText(result.getErrorMessage());
            }
            fileNotValidAlert.showAndWait();
            return;
        }
        final Stage activeStage = (Stage) filePathField.getScene().getWindow();
        final FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("fxml/SolverScene.fxml"));
        Parent solver = null;
        try {
            solver = loader.load();
        } catch (IOException e) {
            System.exit(0);
        }

        final SolverSceneController solverSceneController = loader.getController();
        solverSceneController.init(new YajisanKazusanSolver(parser.getMatchFieldOfParsedFile().orElseThrow(() -> new IllegalStateException("MatchField can not be null here!"))));

        final Scene solverScene = new Scene(solver);
        solverScene.setOnKeyPressed(solverSceneController::onKeyboardPress);
        solverScene.getStylesheets().add(Objects.requireNonNull(getClass().getClassLoader().getResource("css/style.css")).toExternalForm());

        activeStage.setScene(solverScene);
    }
}
