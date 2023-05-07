package com.course.courseud;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class SelectsController {
    @FXML
    private Button goToMenuButton;
    @FXML
    private Button clearButton;
    @FXML
    private Button customQueryButton;
    @FXML
    private ComboBox<String> tablesComboBox;
    @FXML
    private TableView<ObservableList<String>> selectsTable;
    @FXML
    private TextField whereTextField;
    @FXML
    private ComboBox<String> orderByComboBox;
    @FXML
    private CheckBox ascCheckBox;

    UtilsController utilsController = new UtilsController();

    @FXML
    public void initialize() {
        clearButton.setOnAction(actionEvent -> utilsController.clearTable(selectsTable));
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
        fillTablesComboBox();
    }

    private void fillTablesComboBox() {
        tablesComboBox.getItems().addAll(utilsController.getTablesNames());
    }

    private void fillOrderByComboBox() {
        ObservableList<String> columns = FXCollections.observableArrayList();
        for (TableColumn<ObservableList<String>, ?> tc : selectsTable.getColumns()) {
            columns.add(tc.getText());
        }
        orderByComboBox.setItems(columns);
    }

    private void makeTableView() {
        selectsTable.getColumns().clear();
        String sqlQuery = "SELECT * FROM ";
        // Получаем данные из выбранной таблицы
        sqlQuery = sqlQuery + tablesComboBox.getValue();
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
        utilsController.fillTableWithSqlQuery(selectsTable, sqlQuery);
    }
}

// TODO add groupby