package com.course.courseud;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class UpdatesController {
    @FXML
    private Button goToMenuButton;
    @FXML
    private Button clearButton;
    @FXML
    private Button customQueryButton;
    @FXML
    private Button copyValuesButton;
    @FXML
    private ComboBox<String> tablesComboBox;
    @FXML
    private TableView<ObservableList<String>> updatesTable;
    @FXML
    private TextField whereTextField;
    @FXML
    private TextField updatesTextField;
    @FXML
    private ComboBox<String> orderByComboBox;
    @FXML
    private CheckBox ascCheckBox;

    UtilsController utilsController = new UtilsController();

    @FXML
    public void initialize() {
        clearButton.setOnAction(actionEvent -> utilsController.clearTable(updatesTable));
        goToMenuButton.setOnAction(actionEvent -> utilsController.openNewWindow(goToMenuButton, "menu.fxml"));
        customQueryButton.setOnAction(actionEvent -> utilsController.openNewWindow(customQueryButton, "custom_query.fxml"));
        tablesComboBox.setOnAction(actionEvent -> {
            orderByComboBox.setOnAction(null);
            orderByComboBox.setValue("");
            makeTableView();
            fillOrderByComboBox();
            orderByComboBox.setOnAction(actionEvent1 -> makeTableView());
        });
        whereTextField.setOnAction(actionEvent -> {
            orderByComboBox.setOnAction(null);
            orderByComboBox.setValue("");
            makeTableView();
            fillOrderByComboBox();
            orderByComboBox.setOnAction(actionEvent1 -> makeTableView());
        });
        orderByComboBox.setOnAction(actionEvent -> makeTableView());
        ascCheckBox.setOnAction(actionEvent -> makeTableView());
        copyValuesButton.setOnAction(actionEvent -> fillUpdatesTextField());
        updatesTextField.setOnAction(actionEvent -> updateRow());
        fillTablesComboBox();
    }

    private void fillTablesComboBox() {
        tablesComboBox.getItems().addAll(utilsController.getTablesNames());
    }

    private void fillOrderByComboBox() {
        ObservableList<String> columns = FXCollections.observableArrayList();
        for (TableColumn<ObservableList<String>, ?> tc : updatesTable.getColumns()) {
            columns.add(tc.getText());
        }
        orderByComboBox.setItems(columns);
    }

    private void fillUpdatesTextField() {
        StringBuilder stringBuilder = new StringBuilder(updatesTable.getSelectionModel().getSelectedItem().toString());
        stringBuilder.deleteCharAt(stringBuilder.length() - 1)
                .deleteCharAt(0);
        updatesTextField.setText(String.valueOf(stringBuilder));
    }

    private void makeTableView() {
        updatesTable.getColumns().clear();
        String sqlQuery = "SELECT * FROM " + tablesComboBox.getValue();
        // Добавляем WHERE, если есть
        if (!whereTextField.getText().isEmpty()) {
            sqlQuery = sqlQuery + " WHERE " + whereTextField.getText();
        }
        // Добавляем ORDER BY, если есть
        if (orderByComboBox.getValue() != null && !orderByComboBox.getValue().equals("")) {
            sqlQuery = sqlQuery + " ORDER BY " + orderByComboBox.getValue();
            if (!ascCheckBox.isSelected()) {
                sqlQuery = sqlQuery + " DESC ";
            } else {
                sqlQuery = sqlQuery + " ASC ";
            }
        }
        utilsController.fillTableWithSqlQuery(updatesTable, sqlQuery);
    }

    private void updateRow() {
        String sqlQuery = "UPDATE " + tablesComboBox.getValue() + " SET ";
        String[] oldValues = updatesTextField.getText().split(",");
        StringBuilder stringBuilderValues = new StringBuilder();
        // Разделяем полученные значения
        for (int i = 0; i < oldValues.length; i++) {
            oldValues[i] = oldValues[i].trim();
            if (!oldValues[i].matches("\\d+")) {
                oldValues[i] = "'" + oldValues[i] + "'";
            }
        }
        for (int i = 0; i < updatesTable.getColumns().size(); i++) {
            stringBuilderValues
                    .append(updatesTable.getColumns().get(i).getText())
                    .append(" = ")
                    .append(oldValues[i])
                    .append(", ");
        }
        // Добавляем WHERE для удаления одного поля
        stringBuilderValues
                .deleteCharAt(stringBuilderValues.length() - 2)
                .append(" WHERE ")
                .append(updatesTable.getColumns().get(0).getText())
                .append(" = ")
                .append(updatesTable.getSelectionModel().getSelectedItem().get(0))
                .append(";");
        sqlQuery += stringBuilderValues;
        utilsController.updateTableWithSqlQuery((sqlQuery));
        makeTableView();
    }
}