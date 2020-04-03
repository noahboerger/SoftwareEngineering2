package de.dhbw.mosbach.gui;

import de.dhbw.mosbach.matchfield.MatchField;
import de.dhbw.mosbach.matchfield.fields.Field;
import de.dhbw.mosbach.matchfield.fields.HintField;
import de.dhbw.mosbach.matchfield.utils.FieldIndex;
import de.dhbw.mosbach.solve.YajisanKazusanSolver;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.MenuBar;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SolverSceneController {

    private final int gameSize = 500;
    private final int frameSize = 5;

    @FXML
    private MenuBar menuBar;

    @FXML
    private Pane boardPane;

    private Map<Integer, Map<Integer, StackPane>> fieldsMap;

    private YajisanKazusanSolver solver;

    private List<FieldIndex> solution;

    private MatchField actShowingMatchField;

    private MatchField solvedMatchField;

    private int actStep;

    public void init(YajisanKazusanSolver solver) {
        this.solver = solver;
        this.actShowingMatchField = solver.getUnsolvedMatchField();
        actStep = 0;
        initView();
    }

    @FXML
    public void handleBackToMenu() throws IOException {
        doBackToMenu();
    }

    @FXML
    public void handleStep() {
        doStep();
    }

    @FXML
    public void handleStepBack() {
        doStepBack();
    }

    @FXML
    public void handleFullSolution() {
        doFullSolution();
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
                        "Ein Schritt zurück gehen: (F7)\n" +
                        "Oder das aktuelle Spielfeld schließen: (F8)\n\n" +
                        "Viel Spaß wünscht der Entwickler: Noah Börger";
        manualAlert.setContentText(userManual);
        manualAlert.showAndWait();
    }

    @FXML
    public void onKeyboardPress(KeyEvent event) {
        if (event.getCode() == KeyCode.F5) {
            doStep();
        } else if (event.getCode() == KeyCode.F6) {
            doFullSolution();
        } else if (event.getCode() == KeyCode.F7) {
            doStepBack();
        } else if (event.getCode() == KeyCode.F8) {
            try {
                doBackToMenu();
            } catch (IOException ie) {
                ie.printStackTrace();
                System.exit(1);
            }
        }
    }

    private void initView() {
        boardPane.setPrefSize(gameSize + 2 * frameSize, gameSize + 2 * frameSize);
        boardPane.setStyle("-fx-border-width: " + frameSize);

        fieldsMap = new HashMap<>();
        for (int x = 0; x < actShowingMatchField.getSize(); x++) {
            fieldsMap.put(x, new HashMap<>());
            for (int y = 0; y < actShowingMatchField.getSize(); y++) {
                StackPane cell = new StackPane();
                cell.setPrefSize(getFieldPaneSize(), getFieldPaneSize());
                cell.setLayoutX(frameSize + x * getFieldPaneSize());
                cell.setLayoutY(frameSize + y * getFieldPaneSize());
                cell.getStyleClass().add("field");

                boardPane.getChildren().add(cell);
                fieldsMap.get(x).put(y, cell);
            }
        }
        updateView();
    }

    private void updateView() {
        for (int x : fieldsMap.keySet()) {
            for (int y : fieldsMap.get(x).keySet()) {
                Field cellField = actShowingMatchField.getFieldAt(x, y);
                StackPane cell = fieldsMap.get(x).get(y);

                cell.getStyleClass().clear();
                cell.getStyleClass().add("FIELD");
                cell.getStyleClass().add(cellField.getFieldState().toString());

                if (cellField instanceof HintField) {
                    cell.getChildren().clear();

                    HintField hintField = (HintField) cellField;
                    Label amountLabel = new Label(hintField.getAmount() + " " + hintField.getArrowDirection().toCharacter());

                    int fontSize = gameSize / (actShowingMatchField.getSize() * 3);
                    amountLabel.setFont(new Font(fontSize));

                    if (hintField.getFieldState() == Field.State.BLACK) {
                        amountLabel.getStyleClass().add("whiteFont");
                    }
                    cell.getChildren().add(amountLabel);
                }
            }
        }
    }

    private void calculateSolutionIfNotDone() {
        if (solution == null || solvedMatchField == null) {
            solution = solver.getSolvingParsingOrder();
            solvedMatchField = solver.getSolvedMatchField();
        }
    }

    private void doStep() {
        try {
            calculateSolutionIfNotDone();
        } catch (IllegalStateException ise) {
            showWarningDialog("Das geladenen Spielfeld hat keine Lösung!");
            return;
        }
        if (actStep < actShowingMatchField.getSize() * actShowingMatchField.getSize()) {
            Field.State correctFieldState = solvedMatchField.getFieldAt(solution.get(actStep).getX(), solution.get(actStep).getY()).getFieldState();
            actShowingMatchField.getFieldAt(solution.get(actStep).getX(), solution.get(actStep).getY()).setFieldState(correctFieldState);
            actStep++;
        } else {
            showWarningDialog("Das Spielfeld ist bereits gelöst!");
        }
        updateView();
    }

    private void doStepBack() {
        if (actStep > 0) {
            actShowingMatchField.getFieldAt(solution.get(actStep - 1).getX(), solution.get(actStep - 1).getY()).setFieldState(Field.State.UNKNOWN);
            actStep--;
        } else {
            showWarningDialog("Es wurde noch kein Feld offengelegt!");
        }
        updateView();
    }

    private void doFullSolution() {
        try {
            calculateSolutionIfNotDone();
        } catch (IllegalStateException ise) {
            showWarningDialog("Das geladenen Spielfeld hat keine Lösung!");
            return;
        }
        if (actStep < actShowingMatchField.getSize() * actShowingMatchField.getSize()) {
            for (FieldIndex fieldIndex : solution) {
                Field.State correctFieldState = solvedMatchField.getFieldAt(fieldIndex.getX(), fieldIndex.getY()).getFieldState();
                actShowingMatchField.getFieldAt(fieldIndex.getX(), fieldIndex.getY()).setFieldState(correctFieldState);
            }
            actStep = actShowingMatchField.getSize() * actShowingMatchField.getSize();
        } else {
            showWarningDialog("Das Spielfeld ist bereits gelöst!");
        }
        updateView();
    }

    private void doBackToMenu() throws IOException {
        Stage activeStage = (Stage) menuBar.getScene().getWindow();
        Parent menu = FXMLLoader.load(getClass().getClassLoader().getResource("fxml/MenuScene.fxml"));
        Scene menuScene = new Scene(menu);
        menuScene.getStylesheets().add(getClass().getClassLoader().getResource("css/style.css").toExternalForm());
        activeStage.setScene(menuScene);
    }

    private void showWarningDialog(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Warnung!");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private int getFieldPaneSize() {
        return gameSize / actShowingMatchField.getSize();
    }
}
