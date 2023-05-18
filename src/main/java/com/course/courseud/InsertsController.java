package com.course.courseud;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class InsertsController {
    @FXML
    private Button goToMenuButton;
    @FXML
    private Button clearButton;
    @FXML
    private Button customQueryButton;
    @FXML
    private Button infoButton;
    @FXML
    private ComboBox<String> tablesComboBox;
    @FXML
    private TextField insertsTextField;
    @FXML
    private TableView<ObservableList<String>> insertsTable;

    UtilsController utilsController = new UtilsController();

    @FXML
    public void initialize() {
        clearButton.setOnAction(actionEvent -> utilsController.clearTable(insertsTable));
        goToMenuButton.setOnAction(actionEvent -> utilsController.openNewWindow(goToMenuButton, "menu.fxml"));
        infoButton.setOnAction(actionEvent -> utilsController.showInstructionWindow("Инструкция"));
        customQueryButton.setOnAction(actionEvent -> utilsController.openNewWindow(customQueryButton, "custom_query.fxml"));
        fillTablesComboBox();
    }

    private void fillTablesComboBox() {
        tablesComboBox.getItems().addAll(utilsController.getTablesNames());
    }

    @FXML
    private void makeTableView() {
        insertsTable.getColumns().clear();
        String sqlQuery = "SELECT * FROM " + tablesComboBox.getValue();
        utilsController.fillTableWithSqlQuery(insertsTable, sqlQuery);
    }

    @FXML
    private void insertRow() {
        StringBuilder sqlQuery = new StringBuilder("INSERT INTO " + tablesComboBox.getValue() + "(");
        for (TableColumn<ObservableList<String>, ?> tc : insertsTable.getColumns()) {
            sqlQuery.append(tc.getText())
                    .append(", ");
        }
        sqlQuery.deleteCharAt(sqlQuery.length() - 2)
                .append(") VALUES (");
        String[] values = insertsTextField.getText().split(",");
        StringBuilder stringBuilderValues = new StringBuilder();
        for (String value : values) {
            value = value.trim();
            if (!value.matches("\\d+")) {
                value = "'" + value + "'";
            }
            stringBuilderValues.append(value)
                    .append(", ");
        }
        stringBuilderValues.deleteCharAt(stringBuilderValues.length() - 2);
        sqlQuery.append(stringBuilderValues)
                .append(");");
        utilsController.updateTableWithSqlQuery(String.valueOf(sqlQuery));
        makeTableView();
    }
}