package com.course.courseud;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;

import static com.course.courseud.UDApp.connection;

public class UtilsController {

    public void clearTable(TableView tableView) {
        tableView.getColumns().clear();
    }

    public void openNewWindow(Button button, String name) {
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


    public ObservableList<String> getTablesNames() {
        ObservableList<String> tablesList = FXCollections.observableArrayList();
        try {
            DatabaseMetaData dbMetaData = connection.getMetaData();
            ResultSet rsMetaData = dbMetaData.getTables(null, null, null, new String[]{"TABLE"});
            while (rsMetaData.next()) {
                tablesList.add(rsMetaData.getString("TABLE_NAME"));
            }
        } catch (SQLException e) {
            throw new RuntimeException();
        }
        return tablesList;
    }

    public void fillTableWithSqlQuery(TableView tableView, String sqlQuery) {
        try {
            // Делаем запрос и получаем метаданные
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sqlQuery);
            ResultSetMetaData metaData = resultSet.getMetaData();
            // Создаём колонки
            ArrayList<TableColumn<ObservableList<String>, String>> cols = new ArrayList<>();
            for (int i = 0; i < metaData.getColumnCount(); i++) {
                cols.add(new TableColumn<>(metaData.getColumnName(i + 1)));
            }
            tableView.getColumns().setAll(cols);
            //создаем объект класса ObservableList<ObservableList> для хранения данных таблицы
            ObservableList<ObservableList<String>> allRows = FXCollections.observableArrayList();
            //указываем соответствующие для каждого столбца значения из вложенных ObservableList
            for (int i = 0; i < cols.size(); i++) {
                final int j = i;
                cols.get(i).setCellValueFactory(param -> new SimpleStringProperty(param.getValue().get(j)));
            }
            //заполняем таблицу данными, используя цикл
            while (resultSet.next()) {
                ObservableList<String> row = FXCollections.observableArrayList();
                for (int i = 1; i <= metaData.getColumnCount(); i++) {
                    row.add(resultSet.getString(i));
                }
                allRows.add(row);
            }
            //устанавливаем данные таблицы
            tableView.setItems(allRows);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
