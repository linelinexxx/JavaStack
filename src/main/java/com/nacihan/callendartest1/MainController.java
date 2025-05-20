package com.nacihan.callendartest1;

import com.sun.jdi.connect.Connector;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.scene.control.Button;
import javafx.stage.Modality;

import java.sql.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Stack;

public class MainController extends Application {
    private final Stack<TaskSnapshot> undoStack = new Stack<>();
    @FXML
    Button newNoteButton;
    @FXML
    Button refreshButton;
    @FXML
    Button deleteButton;
    @FXML
    Button undoButton;
    @FXML
    Button quitButton;
    @FXML
    Label dateShower;
    @FXML
    VBox taskListVBox;
    @FXML
    AnchorPane selectedTaskPane = null;
    @FXML
            Label mainScreenWarningLabel;

    int selectedTaskId;


    void setSelectedTaskId(int id){
        this.selectedTaskId=id;
    }
    int getSelectedTaskId(){
        return selectedTaskId;
    }

    void setSelectedTaskPane(AnchorPane pane){
        this.selectedTaskPane=pane;
    }

    AnchorPane getSelectedTaskPane(){
        return selectedTaskPane;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

    }

    public void quitButtonOnClick(ActionEvent event) {
        Stage stage = (Stage) quitButton.getScene().getWindow();
        stage.close();
    }

    public void newNoteButtonOnClick(ActionEvent e) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("CallendarTest1.fxml"));
        Parent root = fxmlLoader.load();

        NewTaskController controller = fxmlLoader.getController();

        Stage newNoteStage = new Stage();
        newNoteStage.initStyle(StageStyle.UNDECORATED);
        newNoteStage.setScene(new Scene(root, 650, 442));
        newNoteStage.initModality(Modality.APPLICATION_MODAL);
        newNoteStage.showAndWait();
    }
    public void refreshButtonOnClick(ActionEvent event) {
        dateShower.setText(LocalDate.now().toString());

        loadTasksToVBox();

    }
    public void loadTasksToVBox() {
        DataBaseConnector connectNow = new DataBaseConnector();
        Connection connectDB = connectNow.getConnection();

        String query = "SELECT taskTitle, taskDate , taskId, taskNote FROM tasks ORDER BY taskDate ASC";

        try {
            Statement statement = connectDB.createStatement();
            ResultSet resultSet = statement.executeQuery(query);

            taskListVBox.getChildren().clear();

            while (resultSet.next()) {
                String title = resultSet.getString("taskTitle");
                LocalDate date = resultSet.getDate("taskDate").toLocalDate();
                String text = resultSet.getString("taskNote");
                String dateString = date.toString();
                int idSql = resultSet.getInt("taskId");

                long daysLeft = ChronoUnit.DAYS.between(LocalDate.now(), date);
                String daysLeftMessage;
                if (daysLeft > 0) {
                    daysLeftMessage = "Remaining days " + daysLeft;
                } else if (daysLeft == 0) {
                    daysLeftMessage = "TODAY";

                } else {
                    daysLeft = -daysLeft;
                    daysLeftMessage = daysLeft + "Days past";

                }

                AnchorPane taskPane = new AnchorPane();
                taskPane.setPrefHeight(25);
                taskPane.setStyle("-fx-background-color: #76777c;  -fx-border-color: #ddd; -fx-border-radius: 5;");

                Label titleLabel = new Label(title);
                titleLabel.setPrefHeight(18);
                titleLabel.setPrefWidth(250);
                titleLabel.setStyle("-fx-font-size: 12pt; -fx-font-weight: regular;");
                titleLabel.setTextFill(Color.WHITE);
                titleLabel.setLayoutX(10);
                titleLabel.setLayoutY(10);

                Label daysLeftLabel = new Label(daysLeftMessage);
                daysLeftLabel.setTextFill(Color.WHITE);
                daysLeftLabel.setPrefWidth(150);
                daysLeftLabel.setPrefHeight(18);
                daysLeftLabel.setLayoutX(400);
                daysLeftLabel.setLayoutY(10);

                taskPane.getChildren().addAll(titleLabel, daysLeftLabel);
                taskListVBox.getChildren().add(taskPane);

                taskPane.setOnMouseClicked(event -> {
                    if (event.getClickCount() == 2) {

                        try {
                            setSelectedTaskId(idSql);
                            setSelectedTaskPane(null);

                            doubleClickOnAnAnchor(title, text, dateString);

                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    } else if (event.getClickCount() == 1) {
                        setSelectedTaskId(idSql);
                        setSelectedTaskPane(taskPane);
                    }
                });

            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void doubleClickOnAnAnchor(String title, String text, String date) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("showmessage.fxml"));
        Parent root = fxmlLoader.load();


        ShowController ShowController = fxmlLoader.getController();

        ShowController.setShowMessageTitle(title);
        ShowController.setShowMessageText(text);
        ShowController.setShowMessageDeadLine(date);
        ShowController.setSelectedTaskId(getSelectedTaskId());


        Stage messageStage = new Stage();
        messageStage.initStyle(StageStyle.UNDECORATED);
        messageStage.setScene(new Scene(root, 400, 300));
        messageStage.initModality(Modality.APPLICATION_MODAL);
        messageStage.showAndWait();

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

    public void deleteButtonOnClick(ActionEvent event) {
        int taskIdToDelete =getSelectedTaskId();
        pushStack(taskIdToDelete);
        if (taskIdToDelete == 0 || selectedTaskPane == null) {
            mainScreenWarningLabel.setText("No task selected");
            return;
        }

        DataBaseConnector connectNow = new DataBaseConnector();
        Connection connectDB = connectNow.getConnection();

        String deleteQuery = "DELETE FROM tasks WHERE taskId = ?";

        try {
            PreparedStatement preparedStatement = connectDB.prepareStatement(deleteQuery);
            preparedStatement.setInt(1, taskIdToDelete);
            int affectedRows = preparedStatement.executeUpdate();

            if (affectedRows > 0) {
                mainScreenWarningLabel.setText("Task deleted succesfully");

                // Arayüzden de sil
                taskListVBox.getChildren().remove(selectedTaskPane);
                selectedTaskPane = null;

            } else {
                mainScreenWarningLabel.setText("Task not found");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void undoButtonOnClick(ActionEvent event) {
        if (undoStack.isEmpty()) {
            mainScreenWarningLabel.setText("No changes to undo");
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

            mainScreenWarningLabel.setText("Undo successful");

            // Yeniden yükle
            loadTasksToVBox();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
