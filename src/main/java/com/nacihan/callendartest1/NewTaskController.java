package com.nacihan.callendartest1;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.scene.control.Button;
import javafx.stage.Modality;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

import java.util.Stack;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;


import java.sql.*;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class NewTaskController extends Application{
    @FXML
    TextField noteTaker;
    @FXML
    TextField titleTaker;
    @FXML
    DatePicker datePicker;
    @FXML
    Button cancelButton;
    @FXML
    Button saveButton;
    @FXML
    Label warningLabel;

    public void saveButtonOnClick(ActionEvent event) {
        if (!isValuesEmpty()) {
            if (isDateValid()) {
                saveValuesToMysql();
                warningLabel.setText("Task succesfully saved!");
            } else {
                warningLabel.setText("Please enter invalid date");
            }

        } else {
            warningLabel.setText("Please fill all boxes!");
        }


    }
    public void saveValuesToMysql() {
        DataBaseConnector connectNow = new DataBaseConnector();
        Connection connectDB = connectNow.getConnection();

        // Tarihi formatla
        String formattedDate = datePicker.getValue().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        // SQL sorgusu
        String verifyLogin = "INSERT INTO `calendarproject`.`tasks` (`taskId`, `taskDate`, `taskNote`, `taskTitle`) VALUES (NULL, ?, ?, ?)";

        try {
            // PreparedStatement kullanarak güvenli bağlantı
            PreparedStatement preparedStatement = connectDB.prepareStatement(verifyLogin);

            // Parametreleri bağla
            preparedStatement.setString(1, formattedDate);  // taskDate
            preparedStatement.setString(2, noteTaker.getText());  // taskNote
            preparedStatement.setString(3, titleTaker.getText());  // taskTitle

            // Sorguyu çalıştır
            preparedStatement.executeUpdate();
            System.out.println("Task successfully saved!");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public boolean isValuesEmpty() {
        if (noteTaker.getText().isEmpty() || titleTaker.getText().isEmpty() || datePicker.getValue() == null) {
            return true;
        } else {
            return false;
        }

    }
    public boolean isDateValid() {
        long daysLeft = ChronoUnit.DAYS.between(LocalDate.now(), datePicker.getValue());
        if (daysLeft < 0) {
            return false;
        } else {
            return true;
        }
    }
    public void cancelButtonOnClick(ActionEvent event){
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }


    @Override
    public void start(Stage stage) throws Exception {

    }
}
