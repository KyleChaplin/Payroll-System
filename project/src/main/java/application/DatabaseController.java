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
    private static Connection connection = null;



    // ********************************************
    // ************* DATABASE METHODS *************
    // ********************************************

    // Method to get a connection to the database
    private static void getConnectionToDB() {
        if (connection == null) {
            try {
                // Establish the database connection
                String url = DatabaseController.getEnvVariable("DB_URL");
                String username = DatabaseController.getEnvVariable("DB_USERNAME");
                String password = DatabaseController.getEnvVariable("DB_PASSWORD");

                connection = DriverManager.getConnection(url, username, password);
                System.out.println("Connected to Oracle Database.\n");

            } catch (SQLException e) {
                System.out.println("Connection to Oracle Database failed.");
                e.printStackTrace();
            }
        }
    }

    // Method to close the connection to the database
    private static void closeConnectionToDb() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // Method to start the database checks - Called from Main.java in the start() method
    public static void databaseChecks() throws SQLException {

        getConnectionToDB();

        // Check if tables exist
        DatabaseMetaData metaData = connection.getMetaData();
        String[] tableNames = {
                "NPS_EMPLOYEE",
                "NPS_BANK_DETAILS",
                "NPS_PAYROLL",
                "NPS_LOGIN",
                "NPS_EMERGENCY_CONTACT",
                "NPS_ADDRESSES",
                //"NPS_SCHEDULE",
        };

        // Loop to check all tables
        for (String tableName : tableNames) {
            ResultSet tablesExists = metaData.getTables(null, null, tableName, null);

            // If table exists, print message
            if (tablesExists.next()) {
                System.out.println("Table " + tableName + " exists.\n");
            } else {
                // Create table if it does not exist
                createTables(connection, tableName);
                // Update tables to include foreign keys and other constraints
                updateTables(connection, tableName);
            }
        }

        // Check if an admin account exists
        if (checkLoginWithAccessLevelZero()) {
            System.out.println("Admin account exists.\n");
        } else {
            System.out.println("Admin account does not exist.\n");
            addEmployee("Admin", "Admin", "Admin@admin.com", "01234567890", 0, true);
            getLoginInfoForAccessLevelZero();
        }

        // Close connection to Oracle Database
        closeConnectionToDb();
    }

    // Method to load environment variables from the .env file
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

    // Method to get an environment variable
    private static String getEnvVariable(String key) {
        return envVariables.get(key);
    }



    // ********************************************
    // *********** TABLE CREATE METHODS ***********
    // ********************************************

    // Method to create tables if they do not exist
    private static void createTables(Connection connection, String tableName) {
        System.out.println("Table " + tableName + " does not exist.");
        System.out.println("Creating table " + tableName + "...");

        // Create tables if they do not exist
        switch (tableName) {
            case "NPS_EMPLOYEE":
                try (Statement statement = connection.createStatement()) {
                    statement.executeUpdate("CREATE TABLE NPS_EMPLOYEE (" +
                            "ID NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY, " +
                            "FIRST_NAME VARCHAR2(50) NOT NULL, " +
                            "LAST_NAME VARCHAR2(50) NOT NULL, " +
                            "EMAIL VARCHAR2(100) UNIQUE NOT NULL, " +
                            "PHONE VARCHAR2(20) NOT NULL" +
                            ")");
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                break;
            case "NPS_BANK_DETAILS":
                try (Statement statement = connection.createStatement()) {
                    statement.executeUpdate("CREATE TABLE NPS_BANK_DETAILS (" +
                            "ID NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY, " +
                            "EMPLOYEE_ID NUMBER NOT NULL, " +
                            "BANK_NAME VARCHAR2(50) NOT NULL, " +
                            "ACCOUNT_NUMBER VARCHAR2(20) NOT NULL, " +
                            "SORT_CODE VARCHAR2(20) NOT NULL" +
                            ")");
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                break;
            case "NPS_PAYROLL":
                try (Statement statement = connection.createStatement()) {
                    statement.executeUpdate("CREATE TABLE NPS_PAYROLL (" +
                            "ID NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY, " +
                            "EMPLOYEE_ID NUMBER NOT NULL, " +
                            "PAY_DATE DATE NOT NULL, " +
                            "HOURS_WORKED DECIMAL(10, 2) NOT NULL, " +
                            "OVERTIME_HOURS DECIMAL(10, 2) NOT NULL, " +
                            "OVERTIME_PAY DECIMAL(10, 2) NOT NULL, " +
                            "GROSS_PAY DECIMAL(10, 2) NOT NULL, " +
                            "TAXES DECIMAL(10, 2) NOT NULL, " +
                            "NET_PAY DECIMAL(10, 2) NOT NULL" +
                            ")");
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                break;
            case "NPS_LOGIN":
                try (Statement statement = connection.createStatement()) {
                    statement.executeUpdate("CREATE TABLE NPS_LOGIN (" +
                            "ID NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY, " +
                            "EMPLOYEE_ID NUMBER NOT NULL, " +
                            "ACCESS_LEVEL NUMBER NOT NULL, " +
                            "USERNAME VARCHAR2(50) UNIQUE NOT NULL, " +
                            "PASSWORD VARCHAR2(50) NOT NULL" +
                            ")");
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                break;
            case "NPS_EMERGENCY_CONTACT":
                try (Statement statement = connection.createStatement()) {
                    statement.executeUpdate("CREATE TABLE NPS_EMERGENCY_CONTACT (" +
                            "ID NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY, " +
                            "EMPLOYEE_ID NUMBER NOT NULL, " +
                            "FIRST_NAME VARCHAR2(50) NOT NULL, " +
                            "LAST_NAME VARCHAR2(50) NOT NULL, " +
                            "PHONE VARCHAR2(20) NOT NULL, " +
                            "RELATIONSHIP VARCHAR2(50) NOT NULL" +
                            ")");
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                break;
            case "NPS_ADDRESSES":
                try (Statement statement = connection.createStatement()) {
                    statement.executeUpdate("CREATE TABLE NPS_ADDRESSES (" +
                            "ID NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY, " +
                            "EMPLOYEE_ID NUMBER NOT NULL, " +
                            "ADDRESS_LINE_1 VARCHAR2(100) NOT NULL, " +
                            "ADDRESS_LINE_2 VARCHAR2(100), " +
                            "CITY VARCHAR2(50) NOT NULL, " +
                            "POSTCODE VARCHAR2(10) NOT NULL" +
                            ")");
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                break;
            // Add cases for other tables...
        }
    }



    // ********************************************
    // *********** TABLE UPDATE METHODS ***********
    // ********************************************

    // Method to update tables to include foreign keys and other constraintss
    private static void updateTables(Connection connection, String tableName) {
        System.out.println("Updating table " + tableName + "...");

        // Update tables to include foreign keys and other constraints
        switch (tableName) {
            case "NPS_BANK_DETAILS":
                try (Statement statement = connection.createStatement()) {
                    statement.executeUpdate("ALTER TABLE NPS_BANK_DETAILS ADD CONSTRAINT fk_bank_employee " +
                            "FOREIGN KEY (EMPLOYEE_ID) REFERENCES NPS_EMPLOYEE(ID)");
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                break;
            case "NPS_PAYROLL":
                try (Statement statement = connection.createStatement()) {
                    statement.executeUpdate("ALTER TABLE NPS_PAYROLL ADD CONSTRAINT fk_payroll_employee " +
                            "FOREIGN KEY (EMPLOYEE_ID) REFERENCES NPS_EMPLOYEE(ID)");
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                break;
            case "NPS_LOGIN":
                try (Statement statement = connection.createStatement()) {
                    statement.executeUpdate("ALTER TABLE NPS_LOGIN ADD CONSTRAINT fk_login_employee " +
                            "FOREIGN KEY (EMPLOYEE_ID) REFERENCES NPS_EMPLOYEE(ID)");
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                break;
            case "NPS_EMERGENCY_CONTACT":
                try (Statement statement = connection.createStatement()) {
                    statement.executeUpdate("ALTER TABLE NPS_EMERGENCY_CONTACT ADD CONSTRAINT fk_emergency_employee " +
                            "FOREIGN KEY (EMPLOYEE_ID) REFERENCES NPS_EMPLOYEE(ID)");
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                break;
            case "NPS_ADDRESSES":
                try (Statement statement = connection.createStatement()) {
                    statement.executeUpdate("ALTER TABLE NPS_ADDRESSES ADD CONSTRAINT fk_employee " +
                            "FOREIGN KEY (EMPLOYEE_ID) REFERENCES NPS_EMPLOYEE(ID)");
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                break;
            // Add cases for other tables...
        }
    }



    // ********************************************
    // *********** TABLE INSERT METHODS ***********
    // ********************************************

    // Method to add an employee record
    public static void addEmployee(String firstName, String lastName, String email, String phone, int accessLevel, boolean firstLogin) {
        // Establish the database connection
        getConnectionToDB();

        // Add employee record
        try (PreparedStatement preparedStatement = connection.prepareStatement(
                "INSERT INTO NPS_EMPLOYEE (FIRST_NAME, LAST_NAME, EMAIL, PHONE) VALUES (?, ?, ?, ?)")) {
            preparedStatement.setString(1, firstName);
            preparedStatement.setString(2, lastName);
            preparedStatement.setString(3, email);
            preparedStatement.setString(4, phone);

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Employee added successfully.");

                // Create a login for the employee
                int employeeId = getEmployeeId(email);
                createLogin(employeeId, firstName, accessLevel, firstLogin);

            } else {
                System.out.println("Failed to add employee.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // Close connection to Oracle Database
            closeConnectionToDb();
        }
    }

    // Method to create a login using the employee ID
    private static void createLogin(int employeeId, String firstName, int accessLevel, boolean firstLogin) {
        try (PreparedStatement preparedStatement = connection.prepareStatement(
                "INSERT INTO NPS_LOGIN (EMPLOYEE_ID, ACCESS_LEVEL, USERNAME, PASSWORD) VALUES (?, ?, ?, ?)")) {
            preparedStatement.setInt(1, employeeId);
            preparedStatement.setInt(2, accessLevel);

            // If first login, use default username and password
            if (firstLogin) {
                preparedStatement.setString(3, "admin");
                preparedStatement.setString(4, "admin");
            } else {
                preparedStatement.setString(3, firstName + "_" + employeeId);
                preparedStatement.setString(4, createPassword());
            }

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Login created successfully.");
            } else {
                System.out.println("Failed to create login.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



    // ********************************************
    // ************* READ DB METHODS **************
    // ********************************************

    // Method to check if the username and password match
    public static boolean checkLogin(String username, String password) {
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

    // Method to get the employee ID by email
    private static int getEmployeeId(String email) throws SQLException {
        int employeeId = -1;
        try (PreparedStatement preparedStatement = connection.prepareStatement(
                "SELECT ID FROM NPS_EMPLOYEE WHERE EMAIL = ?")) {
            preparedStatement.setString(1, email);

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                employeeId = resultSet.getInt("ID");
            }
        }
        return employeeId;
    }

    // Method to check if an admin account exists
    private static boolean checkLoginWithAccessLevelZero() {
        boolean loginExists = false;
        try (PreparedStatement preparedStatement = connection.prepareStatement(
                "SELECT * FROM NPS_LOGIN WHERE ACCESS_LEVEL = 0")) {
            ResultSet resultSet = preparedStatement.executeQuery();
            loginExists = resultSet.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return loginExists;
    }

    // Method to get the login information of a user
    // Used to get the username and password when the first account is created
    private static void getLoginInfoForAccessLevelZero() {
        try (PreparedStatement preparedStatement = connection.prepareStatement(
                "SELECT * FROM NPS_LOGIN WHERE ACCESS_LEVEL = 0")) {
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                String username = resultSet.getString("USERNAME");
                String password = resultSet.getString("PASSWORD");

                // Display or use retrieved login information
                System.out.println("Username: " + username);
                System.out.println("Password: " + password);
            } else {
                System.out.println("No login found with access level 0");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



    // ********************************************
    // ************** OTHER METHODS ***************
    // ********************************************

    // Method to create a random password
    private static String createPassword() {
        String password = "";
        String[] passwordCharacters = {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j",
                "k", "l", "m", "n", "o", "p", "q", "r", "s", "t",
                "u", "v", "w", "x", "y", "z", "1", "2", "3", "4",
                "5", "6", "7", "8", "9", "0", "!", "@", "#", "$",
                "%", "^", "&", "*", "(", ")", "-", "_", "+", "=",
                "{", "}", "[", "]", "|", "\\", ":", ";", "\"", "'",
                "<", ">", ",", ".", "?", "/", "`", "~"};
        for (int i = 0; i < 12; i++) {
            int randomIndex = (int) (Math.random() * passwordCharacters.length);
            password += passwordCharacters[randomIndex];
        }
        return password;
    }

    // Method to encrypt a string << NOT IMPLEMENTED >>
    private static String encryptString(String stringToEncrypt) {
        String encryptedString = stringToEncrypt;

        return encryptedString;
    }
}