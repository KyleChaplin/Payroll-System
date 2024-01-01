package application;

import javax.xml.transform.Result;
import java.sql.*;

public class DatabaseController {
    public static void openConnection() {
        // Connection details
        String url = "jdbc:oracle:thin:@localhost:1521:ORCL";
        String username = "system";
        String password = "Zj80cQ)pzp):h>H.";

        // Connect to Oracle Database
        try {
            Connection connection = DriverManager.getConnection(url, username, password);
            System.out.println("Connected to Oracle Database.");

            // Check if tables exist
            DatabaseMetaData metaData = connection.getMetaData();
            String[] tableNames = {"NPS_LOGIN"};

            // Loop to check all tables
            for (String tableName : tableNames) {
                ResultSet tablesExists = metaData.getTables(null, null, tableName, null);

                // If table exists, print message
                if (tablesExists.next()) {
                    System.out.println("Table " + tableName + " exists.");
                } else {
                    System.out.println("Table " + tableName + " does not exist.");
                    System.out.println("Creating table " + tableName + "...");
                    // Create table if it does not exist

                    //************************************************
                    //             DO THIS FOR ALL TABLES?
                    //************************************************
                    try(Statement statement = connection.createStatement()) {
                        statement.executeUpdate("CREATE TABLE NPS_LOGIN (" +
                                "ID NUMBER(*) GENERATED ALWAYS AS IDENTITY PRIMARY KEY, " +
                                "USERNAME VARCHAR2(100) UNIQUE NOT NULL, " +
                                "PASSWORD VARCHAR2(100) NOT NULL" +
                                ")");
                        System.out.println("Table " + tableName + " created.");
                    } catch (SQLException e) {
                        System.out.println("Table " + tableName + " could not be created.");
                        e.printStackTrace();
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Connection to Oracle Database failed.");
            e.printStackTrace();
            return;
        }
    }

    public static boolean checkLogin(String username, String password) {
        // Connection details
        String url = "jdbc:oracle:thin:@localhost:1521:ORCL";
        String dbUsername = "system";
        String dbPassword = "Zj80cQ)pzp):h>H.";

        // SQL query to check if the username and password match
        String query = "SELECT COUNT(*) AS count FROM NPS_LOGIN WHERE USERNAME = ? AND PASSWORD = ?";

        try (Connection connection = DriverManager.getConnection(url, dbUsername, dbPassword);
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            // Set parameters for the query
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);

            ResultSet resultSet = preparedStatement.executeQuery();

            // Check if a record with the given username and password exists
            if (resultSet.next()) {
                int count = resultSet.getInt("count");

                if (count > 0) {
                    System.out.println("Login successful.");
                } else {
                    System.out.println("Login failed.");
                }

                return count > 0; // If count > 0, a match is found
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false; // Default to false if an exception occurs or no match is found
    }


}
