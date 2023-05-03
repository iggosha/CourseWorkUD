package com.course.courseud;

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
    private TableView<String> selectsTable;

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

            ArrayList<TableColumn<String, String>> cols = new ArrayList<>();
            for (int i = 0; i < metaData.getColumnCount(); i++) {
                cols.add(new TableColumn<String, String>(metaData.getColumnName(i + 1)));
                selectsTable.getColumns().add(cols.get(i));
            }

            // next - try

            

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
