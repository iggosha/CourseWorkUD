module com.course.courseud {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.desktop;
    requires poi.ooxml;
    requires poi.ooxml.schemas;

    opens com.course.courseud to javafx.fxml;
    exports com.course.courseud;
}