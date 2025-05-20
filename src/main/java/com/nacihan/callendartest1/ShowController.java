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

public class ShowController extends Application {
    @FXML
    Label showMessageTitle;
    @FXML
    Label showMessageDeadLine;
    @FXML
    Label showMessageText;
    @FXML
    Button showMessageEditButton;
    @FXML
    Button showMessageQuit;


    int selectedTaskId;

    public void setSelectedTaskId(int id) {
        this.selectedTaskId = id;
    }
    public int getSelectedTaskId(){
        return selectedTaskId;
    }

    public void setShowMessageTitle(String title) {
        this.showMessageTitle.setText(title);
    }

    public void setShowMessageDeadLine(String deadLine) {
        this.showMessageDeadLine.setText(deadLine);
    }

    public void setShowMessageText(String text) {
        this.showMessageText.setText(text);
    }



    public void showMessageQuitOnClick(ActionEvent event) {
        Stage stage = (Stage) showMessageQuit.getScene().getWindow();
        stage.close();
    }

    public void showMessageEditButtonOnClick(ActionEvent event) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("editscreen.fxml"));
        Parent root = fxmlLoader.load();


        String title = showMessageTitle.getText();
        String text = showMessageText.getText();
        String date = showMessageDeadLine.getText();

        LocalDate dateLocalDate = LocalDate.parse(date);

        EditController controller = fxmlLoader.getController();
        controller.setEditScreenText(text);
        controller.setEditScreenTitle(title);
        controller.setEditScreenDatePicker(dateLocalDate);
        controller.setSelectedTaskId(getSelectedTaskId());



        Stage editStage = new Stage();
        editStage.initStyle(StageStyle.UNDECORATED);
        editStage.setScene(new Scene(root, 400, 300));
        editStage.initModality(Modality.APPLICATION_MODAL);
        editStage.showAndWait();


    }








    @Override
    public void start(Stage stage) throws Exception {

    }
}
