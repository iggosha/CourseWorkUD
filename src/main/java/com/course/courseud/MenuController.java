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

    static boolean firstTimeAuth = true;

    UtilsController utilsController = new UtilsController();

    @FXML
    public void initialize() {
        goToInsertsButton.setOnAction(actionEvent -> utilsController.openNewWindow(goToInsertsButton, "table_inserts.fxml"));
        goToSelectsButton.setOnAction(actionEvent -> utilsController.openNewWindow(goToSelectsButton, "table_selects.fxml"));
        goToUpdatesButton.setOnAction(actionEvent -> utilsController.openNewWindow(goToUpdatesButton, "table_updates.fxml"));
        goToDeletesButton.setOnAction(actionEvent -> utilsController.openNewWindow(goToDeletesButton, "table_deletes.fxml"));
        connectUser();
    }
    public void authUser() {
        if (firstTimeAuth) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Вход");
            alert.setHeaderText(null);
            VBox dialogPaneContent = new VBox();
            Label userLabel = new Label("Имя пользователя");
            TextField userTextField = new TextField();
            Label passwordLabel = new Label("Пароль");
            PasswordField passwordTextField = new PasswordField();
            dialogPaneContent.getChildren().addAll(userLabel, userTextField, passwordLabel, passwordTextField);
            alert.getDialogPane().setContent(dialogPaneContent);
            ButtonType enter = new ButtonType("Войти");
            // Remove default ButtonTypes
            alert.getButtonTypes().setAll(enter);
            Optional<ButtonType> option = alert.showAndWait();
            if (option.get() == enter) {
                DB_USER = userTextField.getText();
                DB_PASSWORD = passwordTextField.getText();
                connectUser();
            }
            firstTimeAuth = false;
        }
    }
}