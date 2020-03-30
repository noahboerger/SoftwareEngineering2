package de.dhbw.mosbach.gui;

import de.dhbw.mosbach.file.JSONFileValidator;
import de.dhbw.mosbach.file.JSONMatchFieldParser;
import de.dhbw.mosbach.solve.YajisanKazusanSolver;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.ResourceBundle;

public class MenuSceneController {

    @FXML
    private TextField filePathField;

    @FXML
    public void handleChooseFile(ActionEvent event) {
        Stage activeStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("JSON mit Spielfeld auswählen...");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON-Dateien", "*.json", "*.JSON"));
        File selectedFile = fileChooser.showOpenDialog(activeStage);
        if (selectedFile != null) {
            filePathField.setText(selectedFile.getAbsolutePath());
        }
    }

    @FXML
    public void handleStartMainScene(ActionEvent event) throws IOException {
        final JSONFileValidator analyzer = new JSONFileValidator(filePathField.getText());
        if (analyzer.getValidationResult() != JSONFileValidator.ValidationResult.VALID_FILE) {
            Alert fileNotValidAlert = new Alert(Alert.AlertType.ERROR);
            fileNotValidAlert.setTitle("Unpassende Datei ausgewählt!");
            fileNotValidAlert.setHeaderText(null);
            fileNotValidAlert.setContentText(analyzer.getValidationResult().getErrorMessage());
            fileNotValidAlert.showAndWait();
            return;
        }
        final JSONMatchFieldParser parser = new JSONMatchFieldParser(filePathField.getText());
        JSONMatchFieldParser.ParsingValidationResult result = parser.getParsingValidationResult().orElseGet(null);
        if (result != JSONMatchFieldParser.ParsingValidationResult.PARSED_SUCCESSFUL) {
            Alert fileNotValidAlert = new Alert(Alert.AlertType.ERROR);
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
        Stage activeStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("SolverScene.fxml"));
        Parent solver = loader.load();

        SolverSceneController solverSceneController = loader.getController();
        solverSceneController.init(new YajisanKazusanSolver(parser.getMatchFieldOfParsedJSON().orElseGet(null)));

        activeStage.setScene(new Scene(solver));
        activeStage.setTitle("Second Window");
        activeStage.show();
    }

    //Menu-Bar
    @FXML
    public void handleHelp() {
        Alert manualAlert = new Alert(Alert.AlertType.INFORMATION);
        manualAlert.setTitle("Nutzungsinformationen");
        manualAlert.setHeaderText("Anleitung zur Bedienung des Yajisan-Kazusan-Lösers");
        String userManual =
                "Dieses Programm kann das Yajisan-Kazusan Rätsel lösen.\n" +
                        "Öffne hierzu einfach auf dem Startbildschirm eine JSON-Datei.\n" +
                        "Ziehe diese hierzu in das Fenster oder gebe den Pfad ein.\n" +
                        "Zum betrachten der Lösung drücke dann auf \"Spiel lösen\"\n\n" +
                        "Viel Spaß wünscht der Entwickler: Noah Börger";
        manualAlert.setContentText(userManual);
        manualAlert.showAndWait();
    }

    @FXML
    public void handleExitProgram() {
        System.exit(0);
    }

    //Drag and Drop
    @FXML
    public void handleDragDroppedEvent(DragEvent event) {
        Dragboard db = event.getDragboard();
        if (db.hasFiles()) {
            List<File> files = db.getFiles();
            if (files.size() == 1) {
                filePathField.setText(files.get(0).getAbsolutePath());
                event.setDropCompleted(true);
            }
        }
        event.consume();
    }

    public void handleDragOverEvent(DragEvent event) {
        Dragboard db = event.getDragboard();
        if (event.getDragboard().hasFiles()) {
            List<File> files = db.getFiles();
            if (files.size() == 1) {
                if (new JSONFileValidator(files.get(0).getAbsolutePath()).getValidationResult() == JSONFileValidator.ValidationResult.VALID_FILE) {
                    event.acceptTransferModes(TransferMode.ANY);
                }
            }
        }
        event.consume();
    }
}
