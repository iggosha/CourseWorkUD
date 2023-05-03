module com.course.courseud {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    opens com.course.courseud to javafx.fxml;
    exports com.course.courseud;
}