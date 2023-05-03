package com.course.courseud;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.stage.Stage;
import javafx.scene.control.TableView;


import java.io.IOException;
import java.sql.*;
import java.util.*;

import static com.course.courseud.UDApp.connection;

public class SelectsController {
    @FXML
    private Button goToMenuButton;
    @FXML
    private Button clearButton;
    @FXML
    private Button findButton;

    @FXML
    private ComboBox<String> tablesComboBox;
    ObservableList<String> tablesList = FXCollections.observableArrayList();

    @FXML
    private TableView<ObservableList<String>> selectsTable;

    @FXML
    public void initialize() {
        clearButton.setOnAction(actionEvent -> clearAllFields());
        goToMenuButton.setOnAction(actionEvent -> openNewWindow(goToMenuButton, "menu.fxml"));
        makeComboBoxChoice();
    }


    private void clearAllFields() {
        try {
            selectsTable.getColumns().clear();
            tablesComboBox.setValue("Выбрать из доступных таблиц");
        } catch (Exception e) {
            System.out.println("Все элементы очищены");
        }
    }


    @FXML
    public void makeComboBoxChoice() {
        getAllTablesNames();
        tablesComboBox.getItems().addAll(tablesList);
        tablesComboBox.setOnAction(actionEvent -> {
            selectsTable.getColumns().clear();
            makeTableView();
        });

    }

    private void makeTableView() {
        try {
            String sqlQuery = "SELECT * FROM " + tablesComboBox.getValue();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sqlQuery);
            ResultSetMetaData metaData = resultSet.getMetaData();
            selectsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

            ArrayList<TableColumn<ObservableList<String>, String>> cols = new ArrayList<>();
            for (int i = 0; i < metaData.getColumnCount(); i++) {
                cols.add(new TableColumn<>(metaData.getColumnName(i + 1)));
                selectsTable.getColumns().add(cols.get(i));
            }

            //создаем объект класса ObservableList<ObservableList> для хранения данных таблицы
            ObservableList<ObservableList<String>> data = FXCollections.observableArrayList();

            //указываем соответствующие для каждого столбца значения из вложенных ObservableList
            for (int i = 0; i < cols.size(); i++) {
                final int j = i;
                cols.get(i).setCellValueFactory(param -> new SimpleStringProperty(param.getValue().get(j)));
                selectsTable.getColumns().add(cols.get(i));
            }

            //заполняем таблицу данными, используя цикл
            while (resultSet.next()) {
                ObservableList<String> row = FXCollections.observableArrayList();
                for (int i = 1; i <= metaData.getColumnCount(); i++) {
                    row.add(resultSet.getString(i));
                }
                System.out.println();
                data.add(row);
            }

            //устанавливаем данные таблицы
            selectsTable.setItems(data);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void openNewWindow(Button button, String name) {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource(name));
        try {
            fxmlLoader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Parent root = fxmlLoader.getRoot();
        Stage stage = (Stage) button.getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }

    public void getAllTablesNames() {
        try {
            DatabaseMetaData dbMetaData = connection.getMetaData();
            ResultSet rsMetaData = dbMetaData.getTables(null, null, null, new String[]{"TABLE"});
            while (rsMetaData.next()) {
                tablesList.add(rsMetaData.getString("TABLE_NAME"));
            }
        } catch (SQLException e) {
            throw new RuntimeException();
        }
    }
}
