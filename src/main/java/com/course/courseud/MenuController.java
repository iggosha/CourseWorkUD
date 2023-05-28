package com.course.courseud;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.util.Optional;

import static com.course.courseud.UDApp.*;

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
    private Button goToGuestMenuButton;

    UtilsController utilsController = new UtilsController();

    @FXML
    public void initialize() {
        goToInsertsButton.setOnAction(actionEvent -> utilsController.openNewWindow(goToInsertsButton, "table_inserts.fxml"));
        goToSelectsButton.setOnAction(actionEvent -> utilsController.openNewWindow(goToSelectsButton, "table_selects.fxml"));
        goToUpdatesButton.setOnAction(actionEvent -> utilsController.openNewWindow(goToUpdatesButton, "table_updates.fxml"));
        goToDeletesButton.setOnAction(actionEvent -> utilsController.openNewWindow(goToDeletesButton, "table_deletes.fxml"));
    }
}