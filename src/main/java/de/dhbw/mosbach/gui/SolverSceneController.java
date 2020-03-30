package de.dhbw.mosbach.gui;

import de.dhbw.mosbach.matchfield.MatchField;
import de.dhbw.mosbach.solve.YajisanKazusanSolver;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.MenuBar;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class SolverSceneController {

    @FXML
    private MenuBar menuBar;

    @FXML
    private Pane boardPane;

    private MatchField matchField;

    private Map<Integer, Map<Integer, StackPane>> fieldsMap;

    private YajisanKazusanSolver solver;

    private MatchField actShowingField;

    public void init(YajisanKazusanSolver solver) {
        this.solver = solver;
        this.actShowingField = solver.getUnsolvedMatchField();
        initView();
    }

    @FXML
    public void handleBackToMenu(ActionEvent event) throws IOException {
        Stage activeStage = (Stage) ((Node) menuBar).getScene().getWindow();
        Parent menu = FXMLLoader.load(getClass().getClassLoader().getResource("MenuScene.fxml"));
        activeStage.setScene(new Scene(menu));
    }

    @FXML
    public void handleStep(ActionEvent event) {
        System.out.println("TODO: NOT HANDLED YET!");
    }

    @FXML
    public void handleFullSolution(ActionEvent event) {
        System.out.println("TODO: NOT HANDLED YET!");
    }

    @FXML
    public void handleHelp() {
        Alert manualAlert = new Alert(Alert.AlertType.INFORMATION);
        manualAlert.setTitle("Nutzungsinformationen");
        manualAlert.setHeaderText("Anleitung zur Bedienung des Yajisan-Kazusan-Lösers");
        String userManual =
                "Dieses Programm kann das Yajisan-Kazusan Rätsel lösen.\n" +
                        "Du kannst einen Schritt weitergehen: (F5)\n" +
                        "Das ganze Spiel direkt lösen: (F6)\n" +
                        "Oder das aktuelle Spielfeld schließen: (F8)\n\n" +
                        "Viel Spaß wünscht der Entwickler: Noah Börger";
        manualAlert.setContentText(userManual);
        manualAlert.showAndWait();
    }

    private void initView() {
        fieldsMap = new HashMap<>();
         for(int x = 0; x < actShowingField.getSize(); x++) {
             fieldsMap.put(x, new HashMap<>());
             for (int y = 0; y < actShowingField.getSize(); y++) {
                 StackPane cell = new StackPane();
                 cell.setPrefSize(10, 10);
                 cell.setLayoutX(x*10);
                 cell.setLayoutY(y*10);
                 cell.getStyleClass().add("root");

                 boardPane.getChildren().add(cell);
                 fieldsMap.get(x).put(y, cell);
             }
         }
         updateView();
    }

    private void updateView() {

    }

    private void step() {

    }

    private void fullSolution() {

    }
}
