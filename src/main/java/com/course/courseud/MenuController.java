package com.course.courseud;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

public class MenuController {

    @FXML
    private Button goToDeletesButton;
    @FXML
    private Button goToInsertsButton;
    @FXML
    private Button goToSelectsButton;
    @FXML
    private Button goToUpdatesButton;

    @FXML
    public void initialize() {
        goToInsertsButton.setOnAction(actionEvent -> openNewWindow(goToInsertsButton, "table_inserts.fxml"));
        goToSelectsButton.setOnAction(actionEvent -> openNewWindow(goToSelectsButton, "table_selects.fxml"));
        goToUpdatesButton.setOnAction(actionEvent -> openNewWindow(goToUpdatesButton, "table_updates.fxml"));
        goToDeletesButton.setOnAction(actionEvent -> openNewWindow(goToDeletesButton, "table_deletes.fxml"));
    }

    private void openNewWindow(Button button, String name) {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource(name));
        try {
            fxmlLoader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Parent root = fxmlLoader.getRoot();
        Stage stage = (Stage) button.getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }
}