package application;

import java.sql.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class DatabaseController {
    private static Map<String, String> envVariables = new HashMap<>();

    public static void loadEnvVariables() {
        String envPath = "src/main/resources/.env";

        // Check if the .env file exists
        File envFile = new File(envPath);

        try {
            if (envFile.createNewFile()) {
                System.out.println(".env file created.");

                // Write default content to the file if needed
                FileWriter writer = new FileWriter(envFile);
                writer.write("DB_URL=you_db_url\n");
                writer.write("DB_USERNAME=your_db_username\n");
                writer.write("DB_PASSWORD=your_db_password\n");
                writer.close();

            } else {
                System.out.println(".env file already exists.");
            }
        } catch (IOException e) {
            System.out.println("An error occurred while creating the file.");
            e.printStackTrace();
        }

        try (BufferedReader br = new BufferedReader(new FileReader(envPath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split("=", 2);
                if (parts.length >= 2) {
                    String key = parts[0].trim();
                    String value = parts[1].trim();
                    envVariables.put(key, value);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getEnvVariable(String key) {
        return envVariables.get(key);
    }

    public static void openConnection() {
        // Connection details
        String url = DatabaseController.getEnvVariable("DB_URL");
        String username = DatabaseController.getEnvVariable("DB_USERNAME");
        String password = DatabaseController.getEnvVariable("DB_PASSWORD");

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
        // Connection details
        String url = DatabaseController.getEnvVariable("DB_URL");
        String dbUsername = DatabaseController.getEnvVariable("DB_USERNAME");
        String dbPassword = DatabaseController.getEnvVariable("DB_PASSWORD");

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
