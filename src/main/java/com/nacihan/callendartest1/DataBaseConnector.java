package com.nacihan.callendartest1;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DataBaseConnector {
    public Connection databaseLink;

    public Connection getConnection() {
        String databaseName = "calendarproject";
        String databaseUser = "root";
        String databasePassword = "root";
        String url = "jdbc:mysql://localhost:3306/" + databaseName;

        try {

            Class.forName("com.mysql.cj.jdbc.Driver");

            databaseLink = DriverManager.getConnection(url, databaseUser, databasePassword);


        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return databaseLink;
    }
}
