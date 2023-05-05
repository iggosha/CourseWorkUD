package com.course.courseud;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

public class InsertsController {
    @FXML
    private Button goToMenuButton;

    UtilsController utilsController = new UtilsController();
    @FXML
    public void initialize() {
        goToMenuButton.setOnAction(actionEvent -> openNewWindow(goToMenuButton, "menu.fxml"));
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
