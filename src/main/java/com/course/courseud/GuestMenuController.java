package com.course.courseud;

import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class GuestMenuController {

    @FXML
    private Button goToSelectsButton;
    UtilsController utilsController = new UtilsController();

    @FXML
    public void initialize() {
        goToSelectsButton.setOnAction(actionEvent -> utilsController.openNewWindow(goToSelectsButton, "guest_table_selects.fxml"));
    }
}
