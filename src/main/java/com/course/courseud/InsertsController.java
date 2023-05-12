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
        insertsTextField.setOnAction(actionEvent -> insertRow());
        tablesComboBox.setOnAction(actionEvent -> makeTableView());
        fillTablesComboBox();
    }

    private void fillTablesComboBox() {
        tablesComboBox.getItems().addAll(utilsController.getTablesNames());
    }

    private void makeTableView() {
        insertsTable.getColumns().clear();
        String sqlQuery = "SELECT * FROM ";
        // Получаем данные из выбранной таблицы
        sqlQuery = sqlQuery + tablesComboBox.getValue();
        utilsController.fillTableWithSqlQuery(insertsTable, sqlQuery);
    }


    private void insertRow() {
        StringBuilder sqlQuery = new StringBuilder("INSERT INTO " + tablesComboBox.getValue() + "(");
        for (TableColumn<ObservableList<String>, ?> tc : insertsTable.getColumns()) {
            sqlQuery.append(tc.getText())
                    .append(", ");
        }
        sqlQuery.deleteCharAt(sqlQuery.length() - 2)
                .append(") VALUES (");
        String regex = "\\d+";
        String[] values = insertsTextField.getText().split(",");
        StringBuilder stringBuilder = new StringBuilder();
        for (String value : values) {
            value = value.trim();
            if (!value.matches(regex)) {
                value = "'" + value + "'";
            }
            stringBuilder.append(value)
                    .append(", ");
        }
        stringBuilder.deleteCharAt(stringBuilder.length() - 2);
        sqlQuery.append(stringBuilder)
                .append(");");
        utilsController.updateTableWithSqlQuery(String.valueOf(sqlQuery));
        makeTableView();
    }
}