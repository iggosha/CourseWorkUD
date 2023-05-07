package com.course.courseud;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

public class UpdatesController {
    @FXML
    private Button goToMenuButton;

    UtilsController utilsController = new UtilsController();

    @FXML
    public void initialize() {
        goToMenuButton.setOnAction(actionEvent -> utilsController.openNewWindow(goToMenuButton, "menu.fxml"));
    }

}
