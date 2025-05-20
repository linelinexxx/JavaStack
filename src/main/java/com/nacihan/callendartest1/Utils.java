package com.nacihan.callendartest1;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Stack;

public class Utils {
    private final Stack<TaskSnapshot> undoStack = new Stack<>();


    public  void lastProccesSaver(int taskId) {
        DataBaseConnector connectNow = new DataBaseConnector();
        Connection connectDB = connectNow.getConnection();

        String query = "SELECT taskTitle, taskNote, taskDate FROM tasks WHERE taskId = ?";

        try {
            PreparedStatement statement = connectDB.prepareStatement(query);
            statement.setInt(1, taskId);

            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                String title = resultSet.getString("taskTitle");
                String note = resultSet.getString("taskNote");
                LocalDate date = resultSet.getDate("taskDate").toLocalDate();

                undoStack.push(new TaskSnapshot(taskId, title, note, date));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
