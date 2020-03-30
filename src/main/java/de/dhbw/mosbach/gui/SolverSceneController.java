package de.dhbw.mosbach.gui;

import de.dhbw.mosbach.matchfield.MatchField;
import de.dhbw.mosbach.matchfield.fields.Field;
import de.dhbw.mosbach.matchfield.fields.FieldState;
import de.dhbw.mosbach.matchfield.fields.HintField;
import de.dhbw.mosbach.solve.YajisanKazusanSolver;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.MenuBar;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SolverSceneController {

    private final int gameSize = 500;
    private final int frameSize = 5;

    @FXML
    private MenuBar menuBar;

    @FXML
    private Pane boardPane;

    private MatchField matchField;

    private Map<Integer, Map<Integer, StackPane>> fieldsMap;

    private YajisanKazusanSolver solver;

    private MatchField actShowingMatchField;

    public void init(YajisanKazusanSolver solver) {
        this.solver = solver;
        this.actShowingMatchField = solver.getUnsolvedMatchField();
        initView();
    }

    @FXML
    public void handleBackToMenu(ActionEvent event) throws IOException {
        Stage activeStage = (Stage) ((Node) menuBar).getScene().getWindow();
        Parent menu = FXMLLoader.load(getClass().getClassLoader().getResource("MenuScene.fxml"));
        Scene menuScene = new Scene(menu);
        menuScene.getStylesheets().add(getClass().getClassLoader().getResource("css/style.css").toExternalForm());
        activeStage.setScene(menuScene);
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
        int fieldSize = gameSize / actShowingMatchField.getSize();
        boardPane.setPrefSize(gameSize + 2 * frameSize, gameSize + 2 * frameSize);
        boardPane.setStyle("-fx-border-width: "+ frameSize);

        fieldsMap = new HashMap<>();
         for(int x = 0; x < actShowingMatchField.getSize(); x++) {
             fieldsMap.put(x, new HashMap<>());
             for (int y = 0; y < actShowingMatchField.getSize(); y++) {
                 StackPane cell = new StackPane();
                 cell.setPrefSize(fieldSize, fieldSize);
                 cell.setLayoutX(frameSize + x*fieldSize);
                 cell.setLayoutY(frameSize + y*fieldSize);
                 cell.getStyleClass().add("field");

                 boardPane.getChildren().add(cell);
                 fieldsMap.get(x).put(y, cell);
             }
         }
         updateView();
    }

    private void updateView() {
        actShowingMatchField.getFieldAt(0,0).setFieldState(FieldState.WHITE);
        actShowingMatchField.getFieldAt(1,0).setFieldState(FieldState.BLACK);
        for (int x : fieldsMap.keySet()) {
            for(int y : fieldsMap.get(x).keySet()) {
                Field cellField = actShowingMatchField.getFieldAt(x,y);
                StackPane cell = fieldsMap.get(x).get(y);

                cell.getStyleClass().clear();
                cell.getStyleClass().add("FIELD");
                cell.getStyleClass().add(cellField.getFieldState().toString());

                if(cellField instanceof HintField) {
                    cell.getChildren().clear();

                    HintField hintField = (HintField) cellField;
                    Label amountLabel = new Label(Integer.toString(hintField.getAmount()));
                    amountLabel.setFont(new Font(gameSize / (actShowingMatchField.getSize() * 3)));
                    cell.getChildren().add(amountLabel);
                }
            }
        }
    }

    private void step() {

    }

    private void fullSolution() {

    }
}
