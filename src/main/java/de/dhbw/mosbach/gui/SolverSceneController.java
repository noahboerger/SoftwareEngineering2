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
import java.util.Objects;

public class SolverSceneController {

    private final static int GAME_SIZE = 500;

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

    void init(final YajisanKazusanSolver solver) {
        this.solver = solver;
        this.actShowingMatchField = solver.getUnsolvedMatchField();
        actStep = 0;
        initView();
    }

    @FXML
    public void handleBackToMenu() {
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
        final Alert manualAlert = new Alert(Alert.AlertType.INFORMATION);
        manualAlert.setTitle("Nutzungsinformationen");
        manualAlert.setHeaderText("Anleitung zur Bedienung des Yajisan-Kazusan-Lösers");
        final String userManual =
                "Dieses Programm kann das Yajisan-Kazusan Rätsel lösen.\n" +
                        "Du kannst einen Schritt weitergehen: (F5)\n" +
                        "Das ganze Spiel direkt lösen: (F6)\n" +
                        "Einen Schritt zurück gehen: (F7)\n" +
                        "Oder das aktuelle Spielfeld schließen: (F8)\n\n" +
                        "Viel Spaß beim Lösen der Rätsel!";
        manualAlert.setContentText(userManual);
        manualAlert.showAndWait();
    }

    @FXML
    void onKeyboardPress(final KeyEvent event) {
        if (event.getCode() == KeyCode.F5) {
            doStep();
        } else if (event.getCode() == KeyCode.F6) {
            doFullSolution();
        } else if (event.getCode() == KeyCode.F7) {
            doStepBack();
        } else if (event.getCode() == KeyCode.F8) {
            doBackToMenu();
        }
    }

    private void initView() {
        final int FRAME_SIZE = 5;
        boardPane.setPrefSize(getFieldPaneSize() * actShowingMatchField.getEdgeSize() + 2 * FRAME_SIZE, getFieldPaneSize() * actShowingMatchField.getEdgeSize() + 2 * FRAME_SIZE);
        boardPane.setStyle("-fx-border-width: " + FRAME_SIZE);

        fieldsMap = new HashMap<>();
        for (int x = 0; x < actShowingMatchField.getEdgeSize(); x++) {
            fieldsMap.put(x, new HashMap<>());
            for (int y = 0; y < actShowingMatchField.getEdgeSize(); y++) {
                final StackPane cell = new StackPane();
                cell.setPrefSize(getFieldPaneSize(), getFieldPaneSize());
                cell.setLayoutX(FRAME_SIZE + x * getFieldPaneSize());
                cell.setLayoutY(FRAME_SIZE + y * getFieldPaneSize());
                cell.getStyleClass().add("field");

                boardPane.getChildren().add(cell);
                fieldsMap.get(x).put(y, cell);
            }
        }
        updateView();
    }

    private void updateView() {
        for (final int x : fieldsMap.keySet()) {
            for (final int y : fieldsMap.get(x).keySet()) {
                final Field cellField = actShowingMatchField.getFieldAt(x, y);
                final StackPane cell = fieldsMap.get(x).get(y);

                cell.getStyleClass().clear();
                cell.getStyleClass().add("FIELD");
                cell.getStyleClass().add(cellField.getFieldState().toString());

                if (cellField instanceof HintField) {
                    cell.getChildren().clear();

                    final HintField hintField = (HintField) cellField;
                    final Label amountLabel = new Label(hintField.getAmount() + " " + hintField.getArrowDirection().toCharacter());

                    final int fontSize = GAME_SIZE / (actShowingMatchField.getEdgeSize() * 3);
                    amountLabel.setFont(new Font(fontSize));

                    if (hintField.getFieldState() == Field.State.BLACK) {
                        amountLabel.getStyleClass().add("whiteFont");
                    }
                    cell.getChildren().add(amountLabel);
                }
            }
        }
    }

    private boolean calculateSolutionIfNotDone(final boolean fullSolution) {
        if (solution == null || solvedMatchField == null) {
            final LoadingDialog loadingDialog = new LoadingDialog(menuBar.getScene().getWindow(), "Spielfeld wird gelöst...");

            loadingDialog.addTaskEndNotification(() -> {
                if (solvedMatchField != null && solution != null) {
                    if (fullSolution) {
                        doFullSolution();
                    } else {
                        doStep();
                    }
                } else {
                    showWarningDialog("Das geladenen Spielfeld hat keine Lösung!");
                }
            });
            loadingDialog.executeRunnable(() -> {
                solvedMatchField = solver.getSolvedMatchField();
                solution = solver.getSolvingOrder();
            });
            return true;
        }
        return false;
    }

    private void doStep() {
        if (calculateSolutionIfNotDone(false)) {
            return;
        }
        if (actStep < actShowingMatchField.getEdgeSize() * actShowingMatchField.getEdgeSize()) {
            final Field.State correctFieldState = solvedMatchField.getFieldAt(solution.get(actStep).getX(), solution.get(actStep).getY()).getFieldState();
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
        if (calculateSolutionIfNotDone(true)) {
            return;
        }
        if (actStep < actShowingMatchField.getEdgeSize() * actShowingMatchField.getEdgeSize()) {
            for (final FieldIndex fieldIndex : solution) {
                final Field.State correctFieldState = solvedMatchField.getFieldAt(fieldIndex.getX(), fieldIndex.getY()).getFieldState();
                actShowingMatchField.getFieldAt(fieldIndex.getX(), fieldIndex.getY()).setFieldState(correctFieldState);
            }
            actStep = actShowingMatchField.getEdgeSize() * actShowingMatchField.getEdgeSize();
        } else {
            showWarningDialog("Das Spielfeld ist bereits gelöst!");
        }
        updateView();
    }

    private void doBackToMenu() {
        final Stage activeStage = (Stage) menuBar.getScene().getWindow();
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
        activeStage.setScene(menuScene);
    }

    private void showWarningDialog(final String message) {
        final Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Warnung!");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private int getFieldPaneSize() {
        return GAME_SIZE / actShowingMatchField.getEdgeSize();
    }
}
