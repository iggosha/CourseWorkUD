package com.course.courseud;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SelectsController {
    @FXML
    private Button goToMenuButton;
    @FXML
    private Button clearButton;
    @FXML
    private Button infoButton;
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
        for (TableColumn<ObservableList<String>, ?> tc : selectsTable.getColumns()) {
            columns.add(tc.getText());
        }
        orderByComboBox.setItems(columns);
    }

    @FXML
    private void makeTableView() {
        selectsTable.getColumns().clear();
        String sqlQuery = "SELECT * FROM " + tablesComboBox.getValue();
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
            while(matcher.find()) {
                String date = matcher.group();
                whereCondition = whereCondition.replace(date, "'" + date + "'");
            }

            // Поиск строковых значений столбцов
            pattern = Pattern.compile("(=\\s*)(\\p{L}+)(\\s*|AND|OR|$)",
                    Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
            matcher = pattern.matcher(whereCondition);
            whereCondition = matcher.replaceAll("$1'$2'$3");

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
        utilsController.fillTableWithSqlQuery(selectsTable, sqlQuery);
    }


    @FXML
    public void createReport() {
        File file = new File("reports.html");
        try (FileWriter fw = new FileWriter(file.getPath(), StandardCharsets.UTF_8)) {
            fw.write("""
                    <!DOCTYPE html>
                    <html>
                      <head>
                        <link rel="icon" href="icons8-report-100.png"/>
                        <meta charset="utf-8" />
                        <title>Очёт</title>
                        <style>
                          body {
                            background-color: #FFFFFF; /*цвет фона*/
                            font-family: "Segoe UI", sans-serif;
                            font-size: 18px;
                            text-align: center;
                          }
                          table {
                            margin: auto;
                            padding: 1em;
                            width: 50%;
                          }
                          tr, td {
                            border: 3px solid #6699ff;
                            padding: 0em 1em;
                          }
                          tr:hover {
                            background-color: #B3CCFF;
                          }
                        </style>
                      </head>
                      <body>
                      <table>
                      """);
            fw.write("<tr><td colspan=\"999\" style=\"border:0;background-color:#B3CCFF;\"> Отчёт по данным таблицы " + tablesComboBox.getValue()
                    + "<br>Дата создания: " + LocalDate.now()
                    + "<br>Время создания: " + LocalTime.now().truncatedTo(ChronoUnit.SECONDS) + " </td></tr>");
            String sqlQuery = "SELECT * FROM " + tablesComboBox.getValue();
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
                while(matcher.find()) {
                    String date = matcher.group();
                    whereCondition = whereCondition.replace(date, "'" + date + "'");
                }

                // Поиск строковых значений столбцов
                pattern = Pattern.compile("(=\\s*)(\\p{L}+)(\\s*|AND|OR|$)",
                        Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
                matcher = pattern.matcher(whereCondition);
                whereCondition = matcher.replaceAll("$1'$2'$3");

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
            Statement statement = UDApp.connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sqlQuery);
            int rowCount = 0;
            ResultSetMetaData metaData;
            metaData = resultSet.getMetaData();
            fw.write("<tr >");
            for (int i = 1; i <= metaData.getColumnCount(); i++) {
                fw.write("<th style=\"border:3px solid #334C80;background-color:#B3CCFF;\">");
                fw.write(metaData.getColumnName(i));
                fw.write("</th>");
            }
            fw.write("</tr>");
            while (resultSet.next()) {
                fw.write("<tr>");
                for (int i = 1; i <= metaData.getColumnCount(); i++) {
                    fw.write("<td>");
                    fw.write(resultSet.getString(i));
                    fw.write("</td>");
                }
                fw.write("</tr>");
                rowCount++;
            }
            fw.write("<tr><td colspan=\"999\" style=\"border:0;background-color:#B3CCFF;\"> Итого записей:" + rowCount + " </td></tr>");
            fw.write("""          
                    </table>
                    </body>
                    </html>
                    """);
            Desktop.getDesktop().browse(file.toURI());
        } catch (SQLException e) {
            utilsController.showSqlExceptionWindow(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}

/* TODO Добавить гостевой вход с просмотрами

Structure:
controls
init
fills
query
button
            rsMetaData = dbMetaData.getTables(null, null, null, new String[]{"VIEW"});
            while (rsMetaData.next()) {
                tablesList.add(rsMetaData.getString("TABLE_NAME"));
            }
 */