package de.dhbw.mosbach.gui;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

public class LoadingDialog {

    private final String text;

    private final ProgressIndicator progressIndicator = new ProgressIndicator(ProgressIndicator.INDETERMINATE_PROGRESS);
    private final Stage dialogStage = new Stage(StageStyle.UNDECORATED);

    public ObservableList<Void> resultNotificationList = FXCollections.observableArrayList();

    public LoadingDialog(Window owner, String text) {
        dialogStage.initModality(Modality.WINDOW_MODAL);
        dialogStage.initOwner(owner);
        dialogStage.setResizable(false);
        this.text = text;
    }

    public void addTaskEndNotification(Runnable notify) {
        resultNotificationList.addListener((ListChangeListener<Void>) n -> {
            resultNotificationList.clear();
            notify.run();
        });
    }

    public void executeRunnable(Runnable func) {
        setupDialog();
        runRunnable(func);
    }

    private void setupDialog() {
        final Group rootGroup = new Group();
        final Scene scene = new Scene(rootGroup, 330, 120, Color.WHITE);
        final BorderPane mainPane = new BorderPane();
        final VBox vbox = new VBox();
        rootGroup.getChildren().add(mainPane);
        vbox.setSpacing(5);
        vbox.setAlignment(Pos.CENTER);
        vbox.setMinSize(330, 120);
        final Label label = new Label(text);
        vbox.getChildren().addAll(label, progressIndicator);
        mainPane.setTop(vbox);
        dialogStage.setScene(scene);
        dialogStage.show();
    }

    private void runRunnable(Runnable task) {
        Task<Void> executionTask = new Task<>() {
            @Override
            public Void call() {
                task.run();
                return null;
            }
        };
        EventHandler<WorkerStateEvent> notifyListeners = event -> {
            progressIndicator.progressProperty().unbind();
            dialogStage.close();
            try {
                resultNotificationList.add(null);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };
        executionTask.setOnSucceeded(notifyListeners);
        executionTask.setOnFailed(notifyListeners);

        new Thread(executionTask).start();
    }
}