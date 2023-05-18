package com.course.courseud;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class UpdatesController {
    @FXML
    private Button goToMenuButton;
    @FXML
    private Button clearButton;
    @FXML
    private Button customQueryButton;
    @FXML
    private Button copyValuesButton;
    @FXML
    private Button infoButton;
    @FXML
    private ComboBox<String> tablesComboBox;
    @FXML
    private TableView<ObservableList<String>> updatesTable;
    @FXML
    private TextField whereTextField;
    @FXML
    private TextField updatesTextField;
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
                
                9) Кнопка с подписью "Скопировать" копирует значения выбранной строки в поле ввода.
                10) Поле ввода с подсказкой "Введите изменения выбранного поля"
                перезаписывает значения в выбранную в таблице строку после нажатия Enter.
                
                Стандартный алгоритм:
                1. Выбрать таблицу в поле с выпадающим списком
                2. Ввести условие для выборки (Опционально)
                2.1. Нажать Enter, не выходя из поля ввода
                3. Выбрать столбец для сортировки в слующем поле с выпадающим списком (Опционально)
                4. Оставить или снять флажок "По возрастанию" (Опционально)
                5. Выбрать строку для перезаписи
                6. Нажать кнопку "Скопировать"
                7. Изменить значения в поле ввода
                7.1. Нажать Enter, не выходя из поля ввода
                """;
        infoButton.setOnAction(actionEvent -> utilsController.showInstructionWindow(instr));
        clearButton.setOnAction(actionEvent -> utilsController.clearTable(updatesTable));
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
        for (TableColumn<ObservableList<String>, ?> tc : updatesTable.getColumns()) {
            columns.add(tc.getText());
        }
        orderByComboBox.setItems(columns);
    }

    @FXML
    private void fillUpdatesTextField() {
        StringBuilder stringBuilder = new StringBuilder(updatesTable.getSelectionModel().getSelectedItem().toString());
        stringBuilder.deleteCharAt(stringBuilder.length() - 1)
                .deleteCharAt(0);
        updatesTextField.setText(String.valueOf(stringBuilder));
    }

    @FXML
    private void makeTableView() {
        updatesTable.getColumns().clear();
        String sqlQuery = "SELECT * FROM " + tablesComboBox.getValue();
        sqlQuery = utilsController.appendWhereAndOrderByToQuery(whereTextField, orderByComboBox, ascCheckBox, sqlQuery);
        utilsController.fillTableWithSqlQuery(updatesTable, sqlQuery);
    }

    @FXML
    private void updateRow() {
        String sqlQuery = "UPDATE " + tablesComboBox.getValue() + " SET ";
        String[] oldValues = updatesTextField.getText().split(",");
        StringBuilder stringBuilderValues = new StringBuilder();
        // Разделяем полученные значения
        for (int i = 0; i < oldValues.length; i++) {
            oldValues[i] = oldValues[i].trim();
            if (!oldValues[i].matches("\\d+")) {
                oldValues[i] = "'" + oldValues[i] + "'";
            }
        }
        for (int i = 0; i < updatesTable.getColumns().size(); i++) {
            stringBuilderValues
                    .append(updatesTable.getColumns().get(i).getText())
                    .append(" = ")
                    .append(oldValues[i])
                    .append(", ");
        }
        // Добавляем WHERE для удаления одного поля
        stringBuilderValues
                .deleteCharAt(stringBuilderValues.length() - 2)
                .append(" WHERE ")
                .append(updatesTable.getColumns().get(0).getText())
                .append(" = ")
                .append(updatesTable.getSelectionModel().getSelectedItem().get(0))
                .append(";");
        sqlQuery += stringBuilderValues;
        utilsController.updateTableWithSqlQuery((sqlQuery));
        makeTableView();
    }
}