package com.course.courseud;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

public class CustomController {

    @FXML
    private Button clearButton;
    @FXML
    private TableView<ObservableList<String>> customQueryTable;
    @FXML
    private TextField customQueryTextField;
    @FXML
    private Button goToSelectsButton;

    UtilsController utilsController = new UtilsController();

    @FXML
    public void initialize() {
        clearButton.setOnAction(actionEvent -> utilsController.clearTable(customQueryTable));
        goToSelectsButton.setOnAction(actionEvent -> utilsController.openNewWindow(goToSelectsButton, "table_selects.fxml"));
        customQueryTextField.setOnAction(actionEvent -> makeCustomQuery());
    }

    private void makeCustomQuery() {
        customQueryTable.getColumns().clear();
        String sqlQuery = customQueryTextField.getText();
        utilsController.fillTableWithSqlQuery(customQueryTable, sqlQuery);
    }


}
