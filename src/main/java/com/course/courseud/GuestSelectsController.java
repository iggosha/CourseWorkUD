package com.course.courseud;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;


public class GuestSelectsController {
    @FXML
    private Button goToMenuButton;
    @FXML
    private Button refreshButton;
    @FXML
    private Button infoButton;
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
                
                9) Кнопка с подписью "Создать отчёт" формирует отчёт по выбранным данным
                в формате html-файла и открывает его в браузере по умолчанию.
                
                
                Стандартный алгоритм:
                1. Выбрать таблицу в поле с выпадающим списком
                2. Ввести условие для выборки (Опционально)
                2.1. Нажать Enter, не выходя из поля ввода
                3. Выбрать столбец для сортировки в слующем поле с выпадающим списком (Опционально)
                4. Оставить или снять флажок "По возрастанию" (Опционально)
                """;
        infoButton.setOnAction(actionEvent -> utilsController.showInstructionWindow(instr));
        goToMenuButton.setOnAction(actionEvent -> utilsController.openNewWindow(goToMenuButton, "guest_menu.fxml"));
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
        tablesComboBox.getItems().addAll(utilsController.getViewsNames());
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

}