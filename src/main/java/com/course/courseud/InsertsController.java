package com.course.courseud;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class InsertsController {
    @FXML
    private Button goToMenuButton;
    @FXML
    private Button refreshButton;
    @FXML
    private Button customQueryButton;
    @FXML
    private Button infoButton;
    @FXML
    private ComboBox<String> tablesComboBox;
    @FXML
    private TextField insertsTextField;
    @FXML
    private TableView<ObservableList<String>> insertsTable;

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
                6) Поле ввода новых значений - вставляет введённые через запятую значения в выбранную таблицу.
                Для добавления значений нажать на клавиатуре кнопку Enter после ввода.
                
                Стандартный алгоритм:
                1. Выбрать таблицу в поле с выпадающим списком
                2. Ввести нужные значения в нужном количестве через запятую
                2.1. Нажать Enter, не выходя из поля ввода
                """;
        infoButton.setOnAction(actionEvent -> utilsController.showInstructionWindow(instr));
        goToMenuButton.setOnAction(actionEvent -> utilsController.openNewWindow(goToMenuButton, "menu.fxml"));
        customQueryButton.setOnAction(actionEvent -> utilsController.openNewWindow(customQueryButton, "custom_query.fxml"));
        fillTablesComboBox();
    }

    private void fillTablesComboBox() {
        tablesComboBox.getItems().addAll(utilsController.getTablesNames());
    }

    @FXML
    private void makeTableView() {
        insertsTable.getColumns().clear();
        String sqlQuery = "SELECT * FROM " + tablesComboBox.getValue();
        utilsController.fillTableWithSqlQuery(insertsTable, sqlQuery);
    }

    @FXML
    private void insertRow() {
        StringBuilder sqlQuery = new StringBuilder("INSERT INTO " + tablesComboBox.getValue() + "(");
        for (TableColumn<ObservableList<String>, ?> tc : insertsTable.getColumns()) {
            sqlQuery.append(tc.getText())
                    .append(", ");
        }
        sqlQuery.deleteCharAt(sqlQuery.length() - 2)
                .append(") VALUES (");
        String[] values = insertsTextField.getText().split(",");
        StringBuilder stringBuilderValues = new StringBuilder();
        for (String value : values) {
            value = value.trim();
            if (!value.matches("\\d+")) {
                value = "'" + value + "'";
            }
            stringBuilderValues.append(value)
                    .append(", ");
        }
        stringBuilderValues.deleteCharAt(stringBuilderValues.length() - 2);
        sqlQuery.append(stringBuilderValues)
                .append(");");
        utilsController.updateTableWithSqlQuery(String.valueOf(sqlQuery));
        makeTableView();
    }
}