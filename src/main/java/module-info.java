module com.nacihan.callendartest1 {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires java.sql;
    requires jdk.jdi;


    opens com.nacihan.callendartest1 to javafx.fxml;
    exports com.nacihan.callendartest1;
}