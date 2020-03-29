package de.dhbw.mosbach.gui;

import de.dhbw.mosbach.file.JSONFileValidator;
import de.dhbw.mosbach.file.JSONMatchFieldParser;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

public class MenuSceneController {

    @FXML
    private TextField filePathField;

    @FXML
    public void chooseFile(ActionEvent event) {
        Stage mainMenuStage = (Stage)((Node) event.getSource()).getScene().getWindow();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("JSON mit Spielfeld auswählen...");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON-Dateien", "*.json", "*.JSON"));
        File selectedFile = fileChooser.showOpenDialog(mainMenuStage);
        if (selectedFile != null) {
            filePathField.setText(selectedFile.getAbsolutePath());
        }
    }

    @FXML
    public void startMainScene(ActionEvent event) {
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
        JSONMatchFieldParser.ParsingValidationResult result= parser.getParsingValidationResult().orElseGet(null);
        if(result != JSONMatchFieldParser.ParsingValidationResult.PARSED_SUCCESSFUL) {
            Alert fileNotValidAlert = new Alert(Alert.AlertType.ERROR);
            fileNotValidAlert.setTitle("Datei ist korrupt!");
            fileNotValidAlert.setHeaderText(null);
            if(result == null) {
                fileNotValidAlert.setContentText("Ein unerwarteter Fehler ist aufgetreten!");
            }else {
                fileNotValidAlert.setContentText(result.getErrorMessage());
            }
            fileNotValidAlert.showAndWait();
            return; //TODO
        }
    }

    //Menu-Bar
    @FXML
    public void help(ActionEvent event) {
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
    public void exitProgram() {
        System.exit(0);
    }
}
