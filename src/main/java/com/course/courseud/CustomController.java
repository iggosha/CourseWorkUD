package com.course.courseud;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

public class CustomController {

    @FXML
    private Button refreshButton;
    @FXML
    private Button infoButton;
    @FXML
    private TableView<ObservableList<String>> customQueryTable;
    @FXML
    private TextField customQueryTextField;
    @FXML
    private Button goToSelectsButton;
    @FXML
    private ComboBox<String> tablesComboBox;

    UtilsController utilsController = new UtilsController();

    @FXML
    public void initialize() {
        String instruction = """
                Эта панель предназначена для ручного ввода SQL-запросов в поле для ввода.
                Запрос вводится полностью, для выполнения запроса нужно нажать Enter после ввода его в поле.
                """;
        tablesComboBox.getItems().addAll(utilsController.getTablesNames());
        tablesComboBox.getItems().addAll(utilsController.getViewsNames());
        goToSelectsButton.setOnAction(actionEvent -> utilsController.openNewWindow(goToSelectsButton, "menu.fxml"));
        infoButton.setOnAction(actionEvent -> utilsController.showInstructionWindow(instruction));
    }

    @FXML
    private void makeTableView() {
        customQueryTable.getColumns().clear();
        String sqlQuery = "SELECT * FROM " + tablesComboBox.getValue();
        utilsController.fillTableWithSqlQuery(customQueryTable, sqlQuery);
    }

    @FXML
    private void makeCustomQuery() {
        customQueryTable.getColumns().clear();
        String sqlQuery = customQueryTextField.getText();
        if (sqlQuery.contains("SELECT")) {
            utilsController.fillTableWithSqlQuery(customQueryTable, sqlQuery);
        } else {
            utilsController.updateTableWithSqlQuery(sqlQuery);
        }
    }


}
