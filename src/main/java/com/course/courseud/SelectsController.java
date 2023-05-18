package com.course.courseud;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import java.awt.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.io.File;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;

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
        String instr = """
                1) Кнопка "<" - возвращает в главное меню.
                2) Кнопка с изображением метлы - очищает таблицу.
                3) Кнопка "i" (эта кнопка) - выводит окно справки.
                4) Кнопка с изображением головы и шестерни - открывает окно для написания
                непосредественно SQL-запросов в приложении разработчиком.
                                
                5) Поле с выпадающим списком и подсказкой "Выбрать из доступных таблиц..." -
                открывает для выбора список всех таблиц базы данных.
                6) Поле ввода условий и подсказкой "Условие для выборки..." -
                позволяет записывать условия для выборки из выбранной таблицы.
                Значения нужно записывать в форматах:
                а) "столбец = значение" для поиска нужного,
                б) "столбец >/< значение" для фильтрации/
                Условия разделять словами "AND","OR", "и", "или", либо заятой (что равно "и").
                7) Поле с выпадающим списком и подсказкой "Сортировать по колонке..." -
                открывает для выбора список всех столбцов выбранной таблицы.
                8) Флажок "По возрастанию" сортирует таблицу по выбранному столбцу по возрастанию,
                либо по убыванию, если флажок снят.
                                
                9) Кнопка с подписью "Создать отчёт" формирует отчёт по выбранным данным
                в формате html-файла и открывает его в браузере по умолчанию.
                                
                                
                Стандартный алгоритм:
                1. Выбрать таблицу в поле с выпадающим списком
                2. Ввести условие для выборки (Опционально)
                2.1. Нажать Enter, не выходя из поля ввода
                3. Выбрать столбец для сортировки в слующем поле с выпадающим списком (Опционально)
                4. Оставить или снять флажок "По возрастанию" (Опционально)
                5. Создать отчёт (Опционально)
                """;
        infoButton.setOnAction(actionEvent -> utilsController.showInstructionWindow(instr));
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
        sqlQuery = utilsController.appendWhereAndOrderByToQuery(whereTextField, orderByComboBox, ascCheckBox, sqlQuery);
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
            sqlQuery = utilsController.appendWhereAndOrderByToQuery(whereTextField, orderByComboBox, ascCheckBox, sqlQuery);
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


    @FXML
    public void createDocReport() {
        // Creating a blank Document
        XWPFDocument document = new XWPFDocument();
        XWPFTable table = document.createTable();
        File file = new File("Отчёт.docx");

        // Writing the Document in file system
        try (FileOutputStream fileOutputStream = new FileOutputStream(file)) {
            // Creating a table
            String sqlQuery = "SELECT * FROM " + tablesComboBox.getValue();
            sqlQuery = utilsController.appendWhereAndOrderByToQuery(whereTextField, orderByComboBox, ascCheckBox, sqlQuery);
            Statement statement = UDApp.connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sqlQuery);
            ResultSetMetaData metaData = resultSet.getMetaData();

            XWPFTableRow headerRow = table.getRow(0);
            headerRow.getCell(0).setText(metaData.getColumnName(1));
            for (int i = 2; i <= metaData.getColumnCount(); i++) {
                headerRow.addNewTableCell().setText(metaData.getColumnName(i));
            }

            while (resultSet.next()) {
                XWPFTableRow dataRow = table.createRow();
                for (int i = 1; i <= metaData.getColumnCount(); i++) { // обход столбцов по индексу
                    String value = resultSet.getString(i);
                    dataRow.getCell(i - 1).setText(value);
                }
            }

            document.write(fileOutputStream);
            Desktop.getDesktop().browse(file.toURI());
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (SQLException e) {
            utilsController.showSqlExceptionWindow(e);
            throw new RuntimeException(e);
        }
    }
}
