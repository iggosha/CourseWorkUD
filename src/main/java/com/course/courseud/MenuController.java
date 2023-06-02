package com.course.courseud;

import javafx.fxml.FXML;
import javafx.scene.control.*;

public class MenuController {
    @FXML
    private Button goToCareButton;
    @FXML
    private Button goToDirectoriesButton;
    @FXML
    private Button goToDisruptionsButton;
    @FXML
    private Button goToPlantsButton;
    @FXML
    private Button goToCustomButton;

    UtilsController utilsController = new UtilsController();

    @FXML
    public void initialize() {
        goToPlantsButton.setOnAction(actionEvent -> utilsController.openNewWindow(goToPlantsButton, "plants.fxml"));
        goToDisruptionsButton.setOnAction(actionEvent -> utilsController.openNewWindow(goToDisruptionsButton, "disruptions.fxml"));
        goToCareButton.setOnAction(actionEvent -> utilsController.openNewWindow(goToCareButton, "care.fxml"));
        goToDirectoriesButton.setOnAction(actionEvent -> utilsController.openNewWindow(goToDirectoriesButton, "directories.fxml"));

        goToCustomButton.setOnAction(actionEvent -> utilsController.openNewWindow(goToCustomButton, "custom_query.fxml"));
    }
}