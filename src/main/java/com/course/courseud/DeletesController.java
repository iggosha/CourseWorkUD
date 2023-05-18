package com.course.courseud;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class DeletesController {
    @FXML
    private Button goToMenuButton;
    @FXML
    private Button clearButton;
    @FXML
    private Button deleteButton;
    @FXML
    private Button customQueryButton;
    @FXML
    private Button infoButton;
    @FXML
    private ComboBox<String> tablesComboBox;
    @FXML
    private TableView<ObservableList<String>> deletesTable;
    @FXML
    private TextField whereTextField;
    @FXML
    private ComboBox<String> orderByComboBox;
    @FXML
    private CheckBox ascCheckBox;

    UtilsController utilsController = new UtilsController();

    @FXML
    public void initialize() {
        clearButton.setOnAction(actionEvent -> utilsController.clearTable(deletesTable));
        customQueryButton.setOnAction(actionEvent -> utilsController.openNewWindow(customQueryButton, "custom_query.fxml"));
        goToMenuButton.setOnAction(actionEvent -> utilsController.openNewWindow(goToMenuButton, "menu.fxml"));
        infoButton.setOnAction(actionEvent -> utilsController.showInstructionWindow("Инструкция"));
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
        fillTablesComboBox();
    }

    private void fillTablesComboBox() {
        tablesComboBox.getItems().addAll(utilsController.getTablesNames());
    }

    private void fillOrderByComboBox() {
        ObservableList<String> columns = FXCollections.observableArrayList();
        for (TableColumn<ObservableList<String>, ?> tc : deletesTable.getColumns()) {
            columns.add(tc.getText());
        }
        orderByComboBox.setItems(columns);
    }

    @FXML
    private void makeTableView() {
        deletesTable.getColumns().clear();
        String sqlQuery = "SELECT * FROM " + tablesComboBox.getValue();
        // Добавляем WHERE, если есть
        if (!whereTextField.getText().isEmpty()) {
            String whereCondition = whereTextField.getText().trim();
            whereCondition = whereCondition.replaceAll("\\d{4}-\\d{2}-\\d{2}","'"+whereCondition+"'");
            whereCondition = whereCondition.replaceAll(",", " AND ");
            whereCondition = whereCondition.replaceAll(" и ", " AND ");
            whereCondition = whereCondition.replaceAll(" или ", " OR ");
            sqlQuery += " WHERE " + whereCondition;
        }
        // Добавляем ORDER BY, если есть
        if (orderByComboBox.getValue() != null && !orderByComboBox.getValue().equals("")) {
            sqlQuery += " ORDER BY " + orderByComboBox.getValue();
            if (!ascCheckBox.isSelected()) {
                sqlQuery += " DESC ";
            } else {
                sqlQuery += " ASC ";
            }
        }
        utilsController.fillTableWithSqlQuery(deletesTable, sqlQuery);
    }

    @FXML
    private void deleteRow() {
        String sqlQuery = "DELETE FROM " + tablesComboBox.getValue() + " WHERE " +
                deletesTable.getColumns().get(0).getText() + " = " + deletesTable.getSelectionModel().getSelectedItem().get(0);
        utilsController.updateTableWithSqlQuery(sqlQuery);
        makeTableView();
    }
}