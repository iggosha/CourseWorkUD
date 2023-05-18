package com.course.courseud;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class DeletesController {
    @FXML
    private Button goToMenuButton;
    @FXML
    private Button clearButton;
    @FXML
    private Button deleteButton;
    @FXML
    private Button customQueryButton;
    @FXML
    private Button infoButton;
    @FXML
    private ComboBox<String> tablesComboBox;
    @FXML
    private TableView<ObservableList<String>> deletesTable;
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
        clearButton.setOnAction(actionEvent -> utilsController.clearTable(deletesTable));
        customQueryButton.setOnAction(actionEvent -> utilsController.openNewWindow(customQueryButton, "custom_query.fxml"));
        goToMenuButton.setOnAction(actionEvent -> utilsController.openNewWindow(goToMenuButton, "menu.fxml"));
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
        for (TableColumn<ObservableList<String>, ?> tc : deletesTable.getColumns()) {
            columns.add(tc.getText());
        }
        orderByComboBox.setItems(columns);
    }

    @FXML
    private void makeTableView() {
        deletesTable.getColumns().clear();
        String sqlQuery = "SELECT * FROM " + tablesComboBox.getValue();
        sqlQuery = utilsController.appendWhereAndOrderByToQuery(whereTextField, orderByComboBox, ascCheckBox, sqlQuery);
        utilsController.fillTableWithSqlQuery(deletesTable, sqlQuery);
    }

    @FXML
    private void deleteRow() {
        String sqlQuery = "DELETE FROM " + tablesComboBox.getValue() + " WHERE " +
                deletesTable.getColumns().get(0).getText() + " = " + deletesTable.getSelectionModel().getSelectedItem().get(0);
        utilsController.updateTableWithSqlQuery(sqlQuery);
        makeTableView();
    }
}