package com.course.courseud;

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
    private ComboBox<String> tablesComboBox;
    @FXML
    private TableView<ObservableList<String>> deletesTable;

    UtilsController utilsController = new UtilsController();

    @FXML
    public void initialize() {
        clearButton.setOnAction(actionEvent -> utilsController.clearTable(deletesTable));
        goToMenuButton.setOnAction(actionEvent -> utilsController.openNewWindow(goToMenuButton, "menu.fxml"));
        deleteButton.setOnAction(actionEvent -> deleteRow());
        tablesComboBox.setOnAction(actionEvent -> makeTableView());
        fillTablesComboBox();
    }

    private void fillTablesComboBox() {
        tablesComboBox.getItems().addAll(utilsController.getTablesNames());
    }

    private void makeTableView() {
        deletesTable.getColumns().clear();
        String sqlQuery = "SELECT * FROM ";
        // Получаем данные из выбранной таблицы
        sqlQuery = sqlQuery + tablesComboBox.getValue();
        utilsController.fillTableWithSqlQuery(deletesTable, sqlQuery);
    }


    private void deleteRow() {
        String sqlQuery = "DELETE FROM " + tablesComboBox.getValue() + " WHERE " +
                deletesTable.getColumns().get(0).getText() + " = " + deletesTable.getSelectionModel().getSelectedItem().get(0);
        utilsController.updateTableWithSqlQuery(sqlQuery);
        makeTableView();

    }
}