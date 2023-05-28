package com.course.courseud;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.course.courseud.UDApp.connection;

public class UtilsController {

    public void showSqlExceptionWindow(Exception e) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Произошла ошибка при работе с БД");
        alert.setHeaderText(null);
        VBox dialogPaneContent = new VBox();
        Label label = new Label(e.getMessage());
        String stackTrace = Arrays.stream(e.getStackTrace())
                .map(StackTraceElement::toString)
                .collect(Collectors.joining("\n"));
        TextArea textArea = new TextArea();
        textArea.setText(stackTrace);
        dialogPaneContent.getChildren().addAll(label, textArea);
        alert.getDialogPane().setContent(dialogPaneContent);
        alert.showAndWait();
    }

    public void showInstructionWindow(String instruction) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Справка");
        alert.setHeaderText(null);
        VBox dialogPaneContent = new VBox();
        Label label = new Label(instruction);
        dialogPaneContent.getChildren().addAll(label);
        alert.getDialogPane().setContent(dialogPaneContent);
        alert.showAndWait();
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
        } catch (Exception e) {
            showSqlExceptionWindow(e);
        }
        return tablesList;
    }

    public ObservableList<String> getViewsNames() {
        ObservableList<String> tablesList = FXCollections.observableArrayList();
        try {
            DatabaseMetaData dbMetaData = connection.getMetaData();
            ResultSet rsMetaData = dbMetaData.getTables(null, null, null, new String[]{"VIEW"});
            while (rsMetaData.next()) {
                tablesList.add(rsMetaData.getString("TABLE_NAME"));
            }
        } catch (Exception e) {
            showSqlExceptionWindow(e);
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
        } catch (Exception e) {
            showSqlExceptionWindow(e);
        }
    }

    public void updateTableWithSqlQuery(String sqlQuery) {
        try {
            // Делаем запрос и получаем метаданные
            Statement statement = connection.createStatement();
            statement.executeUpdate(sqlQuery);
        } catch (Exception e) {
            showSqlExceptionWindow(e);
        }
    }

    public HashMap<String, String> getColumnsAndTypes(String sqlQuery) {
        HashMap<String, String> columnsAndTypes = new HashMap<>();
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sqlQuery);
            ResultSetMetaData metaData = resultSet.getMetaData();
            for (int i = 0; i < metaData.getColumnCount(); i++) {
                columnsAndTypes.put(metaData.getColumnName(i + 1), metaData.getColumnTypeName(i + 1));
            }
        } catch (Exception e) {
            showSqlExceptionWindow(e);
        }
        return columnsAndTypes;
    }

    public String appendWhereAndOrderByToQuery (TextField whereTextField, ComboBox orderByComboBox, CheckBox ascCheckBox, String sqlQuery) {
        // Добавляем WHERE, если есть
        if (!whereTextField.getText().isEmpty()) {
            String whereCondition = whereTextField.getText().trim();
            // Упрощение синтаксиса при вводе
            whereCondition = whereCondition.replaceAll(",", " AND ");
            whereCondition = whereCondition.replaceAll(" и ", " AND ");
            whereCondition = whereCondition.replaceAll(" или ", " OR ");

            // Поиск даты
            Pattern pattern = Pattern.compile("\\d{4}-\\d{2}-\\d{2}");
            Matcher matcher = pattern.matcher(whereCondition);
            while (matcher.find()) {
                String date = matcher.group();
                whereCondition = whereCondition.replace(date, "'" + date + "'");
            }

            // Поиск строковых значений столбцов
            pattern = Pattern.compile("(?<==\\s)[\\p{L}\\p{Zs}]+(?=\\s(OR|AND|$)|$)",
                    Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
            matcher = pattern.matcher(whereCondition);
            whereCondition = matcher.replaceAll("'$0'");
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
        return sqlQuery;
    }

}
