package com.course.courseud;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class UDApp extends Application {
    public static Connection connection;
    public static final String DB_URL = "jdbc:postgresql://localhost:5432/labsud";
    public static String DB_USER = "postgres";
    public static String DB_PASSWORD = "3gor";

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(UDApp.class.getResource("menu.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Комнатные растения");
        stage.getIcons().add(new Image(getClass().getResourceAsStream("appicon.png")));
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

    public static void connectUser() {
        try {
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}

// #6699ff
// guest 123 admin 3gor