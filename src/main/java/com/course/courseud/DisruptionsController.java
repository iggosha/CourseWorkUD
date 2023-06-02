package com.course.courseud;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import org.apache.poi.xwpf.usermodel.*;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTFonts;

import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.nio.charset.StandardCharsets;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

public class DisruptionsController {

    @FXML
    private Button copyValuesButton;

    @FXML
    private Button createReportButton1;

    @FXML
    private Button createReportButton2;

    @FXML
    private Button deleteButton;

    @FXML
    private Button goToMenuButton;

    @FXML
    private Button infoButton;

    @FXML
    private Button insertButton;

    @FXML
    private TextField plantsTextField;

    @FXML
    private Label nameLabel;

    @FXML
    private TableView<ObservableList<String>> disruptionsTable;

    @FXML
    private Button refreshButton;

    @FXML
    private Button updateButton;

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
                2) Кнопка с изображением закрученных стрелок - обновляет и показывает выбранную таблицу.
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
                                
                9) Кнопка с подписью "Удалить" удаляет из таблицы выбранную строку
                                
                                
                Стандартный алгоритм:
                1. Выбрать таблицу в поле с выпадающим списком
                2. Ввести условие для выборки (Опционально)
                2.1. Нажать Enter, не выходя из поля ввода
                3. Выбрать столбец для сортировки в слующем поле с выпадающим списком (Опционально)
                4. Оставить или снять флажок "По возрастанию" (Опционально)
                5. Выбрать строку для удаления
                6. Нажать кнопку "Удалить"
                """;
        infoButton.setOnAction(actionEvent -> utilsController.showInstructionWindow(instr));
        goToMenuButton.setOnAction(actionEvent -> utilsController.openNewWindow(goToMenuButton, "menu.fxml"));
        makeTableView();
        fillOrderByComboBox();
    }

    @FXML
    private void fillUpdatesTextField() {
        StringBuilder stringBuilder = new StringBuilder(disruptionsTable.getSelectionModel().getSelectedItem().toString());
        stringBuilder.deleteCharAt(stringBuilder.length() - 1)
                .deleteCharAt(0);
        plantsTextField.setText(String.valueOf(stringBuilder));
    }

    private void fillOrderByComboBox() {
        ObservableList<String> columns = FXCollections.observableArrayList();
        for (TableColumn<ObservableList<String>, ?> tc : disruptionsTable.getColumns()) {
            columns.add(tc.getText());
        }
        orderByComboBox.setItems(columns);
    }

    @FXML
    public void createDocReport() {
        XWPFDocument document = new XWPFDocument();
        XWPFParagraph paragraph = document.createParagraph();
        XWPFRun run = paragraph.createRun();
        run.setText("Отчёт по данным таблицы " + nameLabel.getText());

        paragraph = document.createParagraph();
        run = paragraph.createRun();
        run.setText("Дата создания: " + LocalDate.now());

        paragraph = document.createParagraph();
        run = paragraph.createRun();
        run.setText("Время создания: " + LocalTime.now().truncatedTo(ChronoUnit.SECONDS));
        XWPFStyles styles = document.createStyles();
        CTFonts fonts = CTFonts.Factory.newInstance();
        fonts.setHAnsi("Segoe UI");
        fonts.setAscii("Segoe UI");
        styles.setDefaultFonts(fonts);
        File file = new File("Отчёт.docx");

        try (FileOutputStream fileOutputStream = new FileOutputStream(file)) {
            String sqlQuery = "SELECT * FROM " + nameLabel.getText();
            sqlQuery = utilsController.appendWhereAndOrderByToQuery(whereTextField, orderByComboBox, ascCheckBox, sqlQuery);
            Statement statement = UDApp.connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sqlQuery);
            ResultSetMetaData metaData = resultSet.getMetaData();

            XWPFTable table = document.createTable();
            XWPFTableRow headerRow = table.getRow(0);
            headerRow.getCell(0).setText(metaData.getColumnName(1));
            for (int i = 2; i <= metaData.getColumnCount(); i++) {
                headerRow.addNewTableCell().setText(metaData.getColumnName(i));
            }

            int rowCounter = 0;
            while (resultSet.next()) {
                XWPFTableRow dataRow = table.createRow();
                rowCounter++;
                for (int i = 1; i <= metaData.getColumnCount(); i++) {
                    String value = resultSet.getString(i);
                    dataRow.getCell(i - 1).setText(value);
                }
            }

            paragraph = document.createParagraph();
            run = paragraph.createRun();
            run.setText("Итого записей: " + rowCounter);
            document.write(fileOutputStream);
            Desktop.getDesktop().browse(file.toURI());
        } catch (Exception e) {
            utilsController.showSqlExceptionWindow(e);
        }
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
            fw.write("<tr><td colspan=\"999\" style=\"border:0;background-color:#B3CCFF;\"> Отчёт по данным таблицы "
                    + nameLabel.getText()
                    + "<br>Дата создания: " + LocalDate.now()
                    + "<br>Время создания: " + LocalTime.now().truncatedTo(ChronoUnit.SECONDS) + " </td></tr>");
            String sqlQuery = "SELECT * FROM " + nameLabel.getText();
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
        } catch (Exception e) {
            utilsController.showSqlExceptionWindow(e);
        }
    }

    @FXML
    private void deleteRow() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Подтверждение");
        alert.setHeaderText(null);
        VBox dialogPaneContent = new VBox();
        Label userLabel = new Label("Вы уверены, что хотите удалить запись "
                + disruptionsTable.getSelectionModel().getSelectedItem() + "?");

        dialogPaneContent.getChildren().addAll(userLabel);
        alert.getDialogPane().setContent(dialogPaneContent);
        ButtonType enterYes = new ButtonType("Да");
        ButtonType enterNo = new ButtonType("Нет");
        // Remove default ButtonTypes
        alert.getButtonTypes().setAll(enterYes, enterNo);
        Optional<ButtonType> option = alert.showAndWait();
        if (option.get() == enterYes) {
            String sqlQuery = "DELETE FROM " + nameLabel.getText() + " WHERE " +
                    disruptionsTable.getColumns().get(0).getText() + " = "
                    + disruptionsTable.getSelectionModel().getSelectedItem().get(0);
            utilsController.updateTableWithSqlQuery(sqlQuery);
            makeTableView();
        }
    }

    @FXML
    private void updateRow() {
        String sqlQuery = "CALL update_disruption( ";
        String[] oldValues = plantsTextField.getText().split(",");
        StringBuilder stringBuilderValues = new StringBuilder();
        // Разделяем полученные значения
        for (int i = 0; i < oldValues.length; i++) {
            oldValues[i] = oldValues[i].trim();
            if (!oldValues[i].matches("\\d+")) {
                oldValues[i] = "'" + oldValues[i] + "'";
            }
            stringBuilderValues
                    .append(oldValues[i])
                    .append(", ");
        }
        stringBuilderValues.deleteCharAt(stringBuilderValues.length() - 1)
                .deleteCharAt(stringBuilderValues.length() - 1)
                .append(");");
        sqlQuery += stringBuilderValues;
        utilsController.updateTableWithSqlQuery((sqlQuery));
        makeTableView();
    }

    @FXML
    private void insertRow() {
        StringBuilder sqlQuery = new StringBuilder("CALL add_disruption(");
        String[] values = plantsTextField.getText().split(",");
        StringBuilder stringBuilderValues = new StringBuilder();
        for (String value : values) {
            value = value.trim();
            if (!value.matches("\\d+")) {
                value = "'" + value + "'";
            }
            stringBuilderValues.append(value)
                    .append(", ");
        }
        sqlQuery.append(stringBuilderValues);
        sqlQuery.deleteCharAt(sqlQuery.length() - 1)
                .deleteCharAt(sqlQuery.length() - 1)
                .append(");");
        utilsController.updateTableWithSqlQuery(String.valueOf(sqlQuery));
        makeTableView();
    }


    @FXML
    private void makeTableView() {
        disruptionsTable.getColumns().clear();
        String sqlQuery = "SELECT * FROM disruptions_usable";
        sqlQuery = utilsController.appendWhereAndOrderByToQuery(whereTextField, orderByComboBox, ascCheckBox, sqlQuery);
        utilsController.fillTableWithSqlQuery(disruptionsTable, sqlQuery);
    }


}