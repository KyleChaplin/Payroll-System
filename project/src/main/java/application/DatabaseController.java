package application;

import application.employees.Person;
import application.help.HelpInfo;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

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
    private static String currentLoggedInEmployeeId;

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
        } else {
            System.out.println("Connection to Oracle Database already exists.\n");
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
                "NPS_HELP_INFO"
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
            addEmployee("admin", "admin", "admin", "-", "-", 0, true);
        }

        // Close connection to Oracle Database
        //closeConnectionToDb();
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
                            "PHONE VARCHAR2(20) NOT NULL," +
                            "NI_NUMBER VARCHAR2(20) NOT NULL" +
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
            case "NPS_HELP_INFO":
                try (Statement statement = connection.createStatement()) {
                    statement.executeUpdate("CREATE TABLE NPS_HELP_INFO (" +
                            "ID NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY, " +
                            "TITLE VARCHAR2(250) NOT NULL, " +
                            "DESCRIPTION VARCHAR2(1000) NOT NULL," +
                            "ERROR_CODE VARCHAR2(25) NOT NULL" +
                            ")");
                } catch (SQLException e) {
                    e.printStackTrace();
                }
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

    // Method to update employee records
    public static void updateEmployee(String employeeId, String firstName, String lastName, String email, String phone, String niNumber, int accessLevel) {
        try {
            // Check if the access level is being updated
            String oldAccessLevel = getAccessLevel(email); // Retrieve the old access level from the database
            boolean accessLevelUpdated = String.valueOf(accessLevel) != oldAccessLevel;

            // Update the employee login if the access level is being updated
            if (accessLevelUpdated) {
                // Update the login access level
                try (PreparedStatement preparedStatement = connection.prepareStatement(
                        "UPDATE NPS_LOGIN SET ACCESS_LEVEL = ? WHERE EMPLOYEE_ID = ?")) {
                    preparedStatement.setInt(1, accessLevel);
                    preparedStatement.setString(2, employeeId);

                    int rowsAffected = preparedStatement.executeUpdate();
                    if (rowsAffected > 0) {
                        System.out.println("Login access level updated successfully.");
                    } else {
                        System.out.println("Failed to update login access level.");
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

            // Check if the email is being updated
            String oldEmail = getEmailById(employeeId); // Retrieve the old email from the database
            boolean emailUpdated = !email.equals(oldEmail);

            // Update the employee login if the email is being updated
            if (emailUpdated) {
                // Update the login username
                try (PreparedStatement preparedStatement = connection.prepareStatement(
                        "UPDATE NPS_LOGIN SET USERNAME = ? WHERE EMPLOYEE_ID = ?")) {
                    preparedStatement.setString(1, email);
                    preparedStatement.setString(2, employeeId);

                    int rowsAffected = preparedStatement.executeUpdate();
                    if (rowsAffected > 0) {
                        System.out.println("Login username updated successfully.");
                    } else {
                        System.out.println("Failed to update login username.");
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

            // Update the employee record
            try (PreparedStatement preparedStatement = connection.prepareStatement(
                    "UPDATE NPS_EMPLOYEE SET FIRST_NAME = ?, LAST_NAME = ?, EMAIL = ?, PHONE = ?, NI_NUMBER = ? WHERE ID = ?")) {
                preparedStatement.setString(1, firstName);
                preparedStatement.setString(2, lastName);
                preparedStatement.setString(3, email);
                preparedStatement.setString(4, phone);
                preparedStatement.setString(5, niNumber);
                preparedStatement.setString(6, employeeId);

                int rowsAffected = preparedStatement.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println("Employee record updated successfully.");
                } else {
                    System.out.println("No employee found with the given ID.");
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



    // ********************************************
    // *********** TABLE INSERT METHODS ***********
    // ********************************************

    // Method to add an employee record
    public static void addEmployee(String firstName, String lastName, String email, String phone, String niNumber, int accessLevel, boolean firstLogin) {
        // Establish the database connection
        getConnectionToDB();

        // Add employee record
        try (PreparedStatement preparedStatement = connection.prepareStatement(
                "INSERT INTO NPS_EMPLOYEE (FIRST_NAME, LAST_NAME, EMAIL, PHONE, NI_NUMBER) VALUES (?, ?, ?, ?, ?)")) {
            preparedStatement.setString(1, firstName);
            preparedStatement.setString(2, lastName);
            preparedStatement.setString(3, email);
            preparedStatement.setString(4, phone);
            preparedStatement.setString(5, niNumber);

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Employee added successfully.");

                // Create a login for the employee
                int employeeId = getEmployeeId(email);
                createLogin(employeeId, email, firstName, niNumber, accessLevel, firstLogin);

            } else {
                System.out.println("Failed to add employee.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // Close connection to Oracle Database
            //closeConnectionToDb();
        }
    }

    // Method to create a login using the employee ID
    private static void createLogin(int employeeId, String email, String firstName, String niNumber, int accessLevel, boolean firstLogin) {
        try (PreparedStatement preparedStatement = connection.prepareStatement(
                "INSERT INTO NPS_LOGIN (EMPLOYEE_ID, ACCESS_LEVEL, USERNAME, PASSWORD) VALUES (?, ?, ?, ?)")) {
            preparedStatement.setInt(1, employeeId);
            preparedStatement.setInt(2, accessLevel);

            // If first login, use default username and password
            if (firstLogin) {
                preparedStatement.setString(3, "admin");
                preparedStatement.setString(4, "admin");
            } else {
                preparedStatement.setString(3, email);

                // Get last 4 digits of NI number
                niNumber = niNumber.substring(niNumber.length() - 4);
                preparedStatement.setString(4, firstName + "_" + niNumber);
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

    // Method to add help info
    public static void addHelp(String errorCode, String Title, String Description) {
        // Add employee record
        try (PreparedStatement preparedStatement = connection.prepareStatement(
                "INSERT INTO NPS_HELP_INFO (ERROR_CODE, TITLE, DESCRIPTION) VALUES (?, ?, ?)")) {
            preparedStatement.setString(1, errorCode);
            preparedStatement.setString(2, Title);
            preparedStatement.setString(3, Description);

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Help info added successfully.");
            } else {
                System.out.println("Failed to add help info.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    // ********************************************
    // *********** TABLE DELETE METHODS ***********
    // ********************************************

    // Method to delete an employee record
    public static void deleteEmployee(String employeeId) {
        // Check that you're not trying to delete the current logged-in user
        if (!employeeId.equals(currentLoggedInEmployeeId)) {
            // Delete the login details
            try (PreparedStatement preparedStatement = connection.prepareStatement(
                    "DELETE FROM NPS_LOGIN WHERE EMPLOYEE_ID = ?")) {
                preparedStatement.setString(1, employeeId);

                int rowsAffected = preparedStatement.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println("Login record deleted successfully.");
                } else {
                    System.out.println("No login found with the given ID.");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

            // Delete the employee details
            try (PreparedStatement preparedStatement = connection.prepareStatement(
                    "DELETE FROM NPS_EMPLOYEE WHERE ID = ?")) {
                preparedStatement.setString(1, employeeId);

                int rowsAffected = preparedStatement.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println("Employee record deleted successfully.");
                } else {
                    System.out.println("No employee found with the given ID.");
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }
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
                    currentLoggedInEmployeeId = String.valueOf(getEmployeeId(username));
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
    public static int getEmployeeId(String email) throws SQLException {
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

    // Method to get all employees from the database and return an ObservableList
    public static ObservableList<Person> getAllEmployees() {
        ObservableList<Person> data = FXCollections.observableArrayList();

        try (PreparedStatement preparedStatement = connection.prepareStatement(
                "SELECT * FROM NPS_EMPLOYEE")) {
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                String id = resultSet.getString("ID");
                String firstName = resultSet.getString("FIRST_NAME");
                String lastName = resultSet.getString("LAST_NAME");
                String email = resultSet.getString("EMAIL");
                String phone = resultSet.getString("PHONE");
                String niNumber = resultSet.getString("NI_NUMBER");

                // Get access level
                String accessLevel = getAccessLevel(email);

                Person person = new Person(id, firstName, lastName, email, phone, accessLevel, niNumber);

                data.add(person);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return data;
    }

    // Method to get an employees details by ID
    public static Person getEmployeeInfo() {
        Person person = null;

        try (PreparedStatement preparedStatement = connection.prepareStatement(
                "SELECT * FROM NPS_EMPLOYEE WHERE ID = ?")) {
            preparedStatement.setString(1, currentLoggedInEmployeeId);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                String id = currentLoggedInEmployeeId;
                String firstName = resultSet.getString("FIRST_NAME");
                String lastName = resultSet.getString("LAST_NAME");
                String email = resultSet.getString("EMAIL");
                String phone = resultSet.getString("PHONE");
                String niNumber = resultSet.getString("NI_NUMBER");

                // Get access level
                String accessLevel = getAccessLevel(email);

                person = new Person(id, firstName, lastName, email, phone, accessLevel, niNumber);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }


        return person;
    }

    // Method to get employee access level by email
    private static String getAccessLevel(String email) throws SQLException {
        String accessLevel = "";
        try (PreparedStatement preparedStatement = connection.prepareStatement(
                "SELECT ACCESS_LEVEL FROM NPS_LOGIN WHERE USERNAME = ?")) {
            preparedStatement.setString(1, email);

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                accessLevel = resultSet.getString("ACCESS_LEVEL");
            }
        }
        return accessLevel;
    }

    // Method to retrieve the email by employee ID
    private static String getEmailById(String employeeId) throws SQLException {
        String email = null;
        try (PreparedStatement preparedStatement = connection.prepareStatement(
                "SELECT EMAIL FROM NPS_EMPLOYEE WHERE ID = ?")) {
            preparedStatement.setString(1, employeeId);

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                email = resultSet.getString("EMAIL");
            }
        }
        return email;
    }

    // Method to get all help info
    public static ObservableList<HelpInfo> getHelpInfo() {
        ObservableList<HelpInfo> data = FXCollections.observableArrayList();

        try (PreparedStatement preparedStatement = connection.prepareStatement(
                "SELECT * FROM NPS_HELP_INFO")) {
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                String title = resultSet.getString("TITLE");
                String description = resultSet.getString("DESCRIPTION");
                String errorCode = resultSet.getString("ERROR_CODE");

                HelpInfo info = new HelpInfo(errorCode, title, description);

                data.add(info);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return data;
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