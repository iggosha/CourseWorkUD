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
        boolean useSerial = true;
        String insertFieldString = insertsTextField.getText().trim();
        for (TableColumn<ObservableList<String>, ?> tc : insertsTable.getColumns()) {
            // Если не вносим первое значение, а используем SERIAL в SQL
            if (insertFieldString.startsWith(",") && useSerial) {
                insertFieldString = insertFieldString.replace(',',' ');
                useSerial = false;
                continue;
            }
            sqlQuery.append(tc.getText())
                    .append(", ");
        }
        sqlQuery.deleteCharAt(sqlQuery.length() - 1)
                .deleteCharAt(sqlQuery.length() - 1)
                .append(") VALUES (");
        String[] values = insertFieldString.split(",");
        for (String value : values) {
            value = "'" + value.trim() + "'";
            sqlQuery.append(value)
                    .append(", ");
        }
        sqlQuery.deleteCharAt(sqlQuery.length() - 1)
                .deleteCharAt(sqlQuery.length() - 1)
                .append(");");
        utilsController.updateTableWithSqlQuery(String.valueOf(sqlQuery));
        makeTableView();
    }
}