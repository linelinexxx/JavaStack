package com.nacihan.callendartest1;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.scene.control.Label;

import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Stack;

public class EditController extends Application {
    private final Stack<TaskSnapshot> undoStack = new Stack<>();
    @FXML
    TextField editScreenTitle;
    @FXML
    TextField editScreenText;
    @FXML
    DatePicker editScreenDatePicker;
    @FXML
    Button editScreenSaveButton;
    @FXML
    Button editScreenQuit;
    @FXML
    Button editUndo;
    @FXML
    Label editScreenWarning;

    int selectedTaskId;

    public void setSelectedTaskId(int id) {
        this.selectedTaskId = id;

    }

    public int getSelectedTaskId() {
        return this.selectedTaskId;
    }

    public void setEditScreenTitle(String title) {
        editScreenTitle.setText(title);
    }

    public void setEditScreenText(String text) {
        editScreenText.setText(text);
    }

    public void setEditScreenDatePicker(LocalDate date) {
        this.editScreenDatePicker.setValue(date);
    }

    public void editScreenSaveButtonOnClick(ActionEvent event) {

        pushStack(selectedTaskId);

        String newTitle = editScreenTitle.getText();
        LocalDate newDate = editScreenDatePicker.getValue();
        String newText = editScreenText.getText();
        String formattedDate = newDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));


        if (newTitle.isEmpty() || newDate == null || newText.isEmpty()) {

            return;
        }

        DataBaseConnector connectNow = new DataBaseConnector();
        Connection connectDB = connectNow.getConnection();

        String updateQuery = "UPDATE tasks SET taskTitle = ?, taskDate = ?, taskNote = ? WHERE taskId = ?";

        try {
            PreparedStatement preparedStatement = connectDB.prepareStatement(updateQuery);
            preparedStatement.setString(1, newTitle);
            preparedStatement.setString(2, formattedDate);  // yyyy-MM-dd formatında olmalı
            preparedStatement.setString(3, newText);
            preparedStatement.setInt(4, getSelectedTaskId());

            preparedStatement.executeUpdate();


            Stage stage = (Stage) editScreenSaveButton.getScene().getWindow();


        } catch (SQLException e) {
            e.printStackTrace();
        }


    }

    public void editScreenQuitOnClick(ActionEvent event) {
        Stage stage = (Stage) editScreenQuit.getScene().getWindow();
        stage.close();
    }

    public void pushStack(int id) {
        DataBaseConnector connectNow = new DataBaseConnector();

        try (Connection connectDB = connectNow.getConnection()) {
            String query = "SELECT taskTitle, taskDate, taskId, taskNote FROM tasks WHERE taskId = ?";
            PreparedStatement preparedStatement = connectDB.prepareStatement(query);
            preparedStatement.setInt(1, id);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                String title = resultSet.getString("taskTitle");
                LocalDate date = resultSet.getDate("taskDate").toLocalDate();
                String text = resultSet.getString("taskNote");
                int idSql = resultSet.getInt("taskId");

                undoStack.push(new TaskSnapshot(idSql, title, text, date));
            } else {
                throw new SQLException("No task found with the given ID.");
            }
        } catch (SQLException e) {
            System.err.println("Error while pushing to stack: " + e.getMessage());
            e.printStackTrace();

        }
    }

    public void editUndoOnClick(ActionEvent event) {
        if (undoStack.isEmpty()) {
            editScreenWarning.setText("No changes to undo");
            return;
        }

        TaskSnapshot snapshot = undoStack.pop();    

        DataBaseConnector connectNow = new DataBaseConnector();
        Connection connectDB = connectNow.getConnection();

        String query = "REPLACE INTO tasks (taskId, taskTitle, taskNote, taskDate) VALUES (?, ?, ?, ?)";

        try {
            PreparedStatement preparedStatement = connectDB.prepareStatement(query);
            preparedStatement.setInt(1, snapshot.taskId);
            preparedStatement.setString(2, snapshot.title);
            preparedStatement.setString(3, snapshot.note);
            preparedStatement.setDate(4, Date.valueOf(snapshot.date));

            preparedStatement.executeUpdate();

            editScreenWarning.setText("Undo successful");


        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void start(Stage stage) throws Exception {

    }
}
