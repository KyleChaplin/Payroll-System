package application;

import application.admin.DeletedUser;
import application.email.SingleEmail;
import application.employees.Person;
import application.help.HelpInfo;
import application.payroll.DetailedPayroll;
import application.payroll.PayrollOverview;
import application.schedule.Schedule;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.*;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Base64;

public class DatabaseController {
    private static final Map<String, String> envVariables = new HashMap<>();
    private static Connection connection = null;
    private static final Logger logger = LogManager.getLogger(DatabaseController.class);
    private static String currentLoggedInEmployeeId;

    // ********************************************
    // ************* DATABASE METHODS *************
    // ********************************************

    class DatabaseConnection {
        // Method to get a connection to the database
        private static void getConnectionToDB() {
            if (connection == null) {
                try {
                    // Establish the database connection
                    String url = DatabaseChecks.getEnvVariable("DB_URL");
                    String username = DatabaseChecks.getEnvVariable("DB_USERNAME");
                    String password = DatabaseChecks.getEnvVariable("DB_PASSWORD");

                    connection = DriverManager.getConnection(url, username, password);
                    logger.info("Connected to Oracle Database.\n");

                } catch (SQLException e) {
                    logger.info("Connection to Oracle Database failed.");
                    logger.error("Connection to Oracle Database Failed.", e);
                }
            } else {
                logger.info("Connection to Oracle Database already exists.\n");
            }
        }

        // Method to close the connection to the database
        private static void closeConnectionToDb() {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    logger.error("Closing connection to Oracle Database failed.", e);
                }
            }
        }
    }

    class DatabaseChecks {
        // Method to start the database checks - Called from Main.java in the start() method
        public static void databaseChecks() throws SQLException {

            DatabaseConnection.getConnectionToDB();

            // Check if tables exist
            DatabaseMetaData metaData = connection.getMetaData();
            String[] tableNames = {
                    "NPS_EMPLOYEE",
                    "NPS_BANK_DETAILS",
                    "NPS_PAYROLL",
                    "NPS_LOGIN",
                    "NPS_EMERGENCY_CONTACT",
                    "NPS_ADDRESSES",
                    "NPS_HELP_INFO",
                    "NPS_EMAIL_INFO",
                    "NPS_SCHEDULE_0",
                    "NPS_SCHEDULE_1",
                    "NPS_SCHEDULE_2",
                    "NPS_SCHEDULE_3",
                    "NPS_DELETED_USERS"
            };

            // Loop to check all tables
            for (String tableName : tableNames) {
                ResultSet tablesExists = metaData.getTables(null, null, tableName, null);

                // If table exists, print message
                if (tablesExists.next()) {
                    logger.info("Table " + tableName + " exists.\n");
                } else {
                    // Create table if it does not exist
                    CreateTables.createTables(connection, tableName);
                    // Update tables to include foreign keys and other constraints
                    UpdateTablesConstraints.updateTables(tableName);
                }
            }

            // Check if an admin account exists
            if (CheckTableData.checkLoginWithAccessLevelZero()) {
                logger.info("Admin account exists.\n");
            } else {
                logger.info("Admin account does not exist... creating one.\n");
                // Set currently logged in employee ID to 1 (admin) for the first login
                //currentLoggedInEmployeeId = "1";

                AddTableData.addEmployee("admin", "admin", "admin", "0", "0.0",
                        "0", 0, "admin", "delete me", "-1",
                        "IT",
                        "admin", true);
            }

            // Check if emailInfo exists
            if (!CheckTableData.checkEmailInfo()) {
                // Create base email info
                // Get current date and time
                Date date = new Date();
                Timestamp timestamp = new Timestamp(date.getTime());
                String formattedDate = new java.text.SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(timestamp);

                AddTableData.addEmailInfo("your_email", "your_password", formattedDate);

                logger.info("Email info generated.");
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
                    logger.info(".env files has been created.\n");

                    // Write default content to the file if needed
                    FileWriter writer = new FileWriter(envFile);
                    writer.write("DB_URL=you_db_url\n");
                    writer.write("DB_USERNAME=your_db_username\n");
                    writer.write("DB_PASSWORD=your_db_password\n");
                    writer.close();

                } else {
                    logger.info(".env file already exists.\n");
                }
            } catch (IOException e) {
                logger.error("Error creating the .env file", e);
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
                logger.error("Loading environment data failed", e);
            }
        }

        // Method to get an environment variable
        private static String getEnvVariable(String key) {
            return envVariables.get(key);
        }

    }

    // ********************************************
    // *********** TABLE CREATE METHODS ***********
    // ********************************************

    class CreateTables {
        // Method to create tables if they do not exist
        private static void createTables(Connection connection, String tableName) {
            logger.info("Table " + tableName + " does not exist... Creating table...");

            // Create tables if they do not exist
            switch (tableName) {
                case "NPS_EMPLOYEE":
                    try (Statement statement = connection.createStatement()) {
                        statement.executeUpdate("CREATE TABLE NPS_EMPLOYEE (" +
                                "ID NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY, " +
                                "FIRST_NAME VARCHAR2(55) NOT NULL, " +
                                "LAST_NAME VARCHAR2(55) NOT NULL, " +
                                "EMAIL VARCHAR2(105) UNIQUE NOT NULL, " +
                                "PHONE VARCHAR2(25) NOT NULL," +
                                "SALARY DECIMAL (10, 2) NOT NULL," +
                                "NI_NUMBER VARCHAR2(25) NOT NULL," +
                                "LOCATION VARCHAR2(105) NOT NULL," +
                                "CONTRACT_TYPE VARCHAR2(55) NOT NULL," +
                                "CONTRACT_HOURS DECIMAL (10, 2) NOT NULL," +
                                "DEPARTMENT VARCHAR2(55) NOT NULL," +
                                "JOB_TITLE VARCHAR2(25) NOT NULL" +
                                ")");
                    } catch (SQLException e) {
                        logger.error("Failed to create " + tableName + " ", e);
                    }
                    break;
                case "NPS_BANK_DETAILS":
                    try (Statement statement = connection.createStatement()) {
                        statement.executeUpdate("CREATE TABLE NPS_BANK_DETAILS (" +
                                "ID NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY, " +
                                "EMPLOYEE_ID NUMBER NOT NULL, " +
                                "BANK_NAME VARCHAR2(55) NOT NULL, " +
                                "ACCOUNT_NUMBER VARCHAR2(25) NOT NULL, " +
                                "SORT_CODE VARCHAR2(25) NOT NULL" +
                                ")");
                    } catch (SQLException e) {
                        logger.error("Failed to create " + tableName + " ", e);
                    }
                    break;
                case "NPS_PAYROLL":
                    try (Statement statement = connection.createStatement()) {
                        statement.executeUpdate("CREATE TABLE NPS_PAYROLL (" +
                                "ID NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY, " +
                                "EMPLOYEE_ID NUMBER NOT NULL, " +
                                "PAY_DATE VARCHAR2(30) NOT NULL, " +
                                "PAY_MONTH VARCHAR2(10) NOT NULL, " +
                                "YEAR NUMBER(4) NOT NULL, " +
                                "HOURS_WORKED DECIMAL(10, 2) NOT NULL, " +
                                "PENSION VARCHAR(3) NOT NULL, " +
                                "PENSION_PAID DECIMAL(10, 2) NOT NULL, " +
                                "OVERTIME_HOURS DECIMAL(10, 2) NOT NULL, " +
                                "OVERTIME_PAY DECIMAL(10, 2) NOT NULL, " +
                                "GROSS_PAY DECIMAL(10, 2) NOT NULL, " +
                                "TAXES DECIMAL(10, 2) NOT NULL, " +
                                "NET_PAY DECIMAL(10, 2) NOT NULL" +
                                ")");
                    } catch (SQLException e) {
                        logger.error("Failed to create " + tableName + " ", e);
                    }
                    break;
                case "NPS_LOGIN":
                    try (Statement statement = connection.createStatement()) {
                        statement.executeUpdate("CREATE TABLE NPS_LOGIN (" +
                                "ID NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY, " +
                                "EMPLOYEE_ID NUMBER NOT NULL, " +
                                "ACCESS_LEVEL NUMBER NOT NULL, " +
                                "USERNAME VARCHAR2(1000) UNIQUE NOT NULL, " +
                                "PASSWORD VARCHAR2(1000) NOT NULL" +
                                ")");
                    } catch (SQLException e) {
                        logger.error("Failed to create " + tableName + " ", e);
                    }
                    break;
                case "NPS_EMERGENCY_CONTACT":
                    try (Statement statement = connection.createStatement()) {
                        statement.executeUpdate("CREATE TABLE NPS_EMERGENCY_CONTACT (" +
                                "ID NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY, " +
                                "EMPLOYEE_ID NUMBER NOT NULL, " +
                                "FIRST_NAME VARCHAR2(55) NOT NULL, " +
                                "LAST_NAME VARCHAR2(55) NOT NULL, " +
                                "PHONE VARCHAR2(25) NOT NULL, " +
                                "RELATIONSHIP VARCHAR2(55) NOT NULL" +
                                ")");
                    } catch (SQLException e) {
                        logger.error("Failed to create " + tableName + " ", e);
                    }
                    break;
                case "NPS_ADDRESSES":
                    try (Statement statement = connection.createStatement()) {
                        statement.executeUpdate("CREATE TABLE NPS_ADDRESSES (" +
                                "ID NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY, " +
                                "EMPLOYEE_ID NUMBER NOT NULL, " +
                                "ADDRESS_LINE_1 VARCHAR2(125) NOT NULL, " +
                                "ADDRESS_LINE_2 VARCHAR2(125), " +
                                "CITY VARCHAR2(75) NOT NULL, " +
                                "POSTCODE VARCHAR2(25) NOT NULL" +
                                ")");
                    } catch (SQLException e) {
                        logger.error("Failed to create " + tableName + " ", e);
                    }
                    break;
                case "NPS_HELP_INFO":
                    try (Statement statement = connection.createStatement()) {
                        statement.executeUpdate("CREATE TABLE NPS_HELP_INFO (" +
                                "ID NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY, " +
                                "TITLE VARCHAR2(250) NOT NULL, " +
                                "DESCRIPTION VARCHAR2(1000) NOT NULL," +
                                "ERROR_CODE VARCHAR2(25) NOT NULL," +
                                "ADDED_BY VARCHAR2(100) NOT NULL" +
                                ")");
                    } catch (SQLException e) {
                        logger.error("Failed to create " + tableName + " ", e);
                    }
                    break;
                case "NPS_EMAIL_INFO":
                    try (Statement statement = connection.createStatement()) {
                        statement.executeUpdate("CREATE TABLE NPS_EMAIL_INFO (" +
                                "ID NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY, " +
                                "FROM_USER_EMAIL VARCHAR2(100) NOT NULL, " +
                                "FROM_USER_PASSWORD VARCHAR2(100) NOT NULL, " +
                                "EMAIL_DATE VARCHAR2(30) NOT NULL " +
                                ")");
                    } catch (SQLException e) {
                        logger.error("Failed to create " + tableName + " ", e);
                    }
                    break;
                case "NPS_SCHEDULE":
                    try (Statement statement = connection.createStatement()) {
                        statement.executeUpdate("CREATE TABLE NPS_SCHEDULE (" +
                                "ID NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY, " +
                                "EMPLOYEE_ID NUMBER NOT NULL, " +
                                "WEEK_ID NUMBER NOT NULL, " +
                                "DAY VARCHAR(10) NOT NULL, " +
                                "START_TIME VARCHAR(5), " +
                                "END_TIME VARCHAR(5)" +
                                ")");
                    } catch (SQLException e) {
                        logger.error("Failed to create " + tableName + " ", e);
                    }
                    break;
                case "NPS_SCHEDULE_0":
                    createScheduleTable("0", tableName);
                    break;
                case "NPS_SCHEDULE_1":
                    createScheduleTable("1", tableName);
                    break;
                case "NPS_SCHEDULE_2":
                    createScheduleTable("2", tableName);
                    break;
                case "NPS_SCHEDULE_3":
                    createScheduleTable("3", tableName);
                    break;
                case "NPS_DELETED_USERS":
                    try (Statement statement = connection.createStatement()) {
                        statement.executeUpdate("CREATE TABLE NPS_DELETED_USERS (" +
                                "ID NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY, " +
                                "ADDED_DATE VARCHAR2(45) NOT NULL, " +
                                "DELETE_DATE VARCHAR2(45) NOT NULL, " +
                                "DELETED_BY VARCHAR2(105) NOT NULL, " +
                                "FIRST_NAME VARCHAR2(55) NOT NULL, " +
                                "LAST_NAME VARCHAR2(55) NOT NULL, " +
                                "EMAIL VARCHAR2(105) UNIQUE NOT NULL, " +
                                "PHONE VARCHAR2(25) NOT NULL," +
                                "NI_NUMBER VARCHAR2(25) NOT NULL," +
                                "ADDRESS_LINE_1 VARCHAR2(125) NOT NULL, " +
                                "ADDRESS_LINE_2 VARCHAR2(125), " +
                                "CITY VARCHAR2(75) NOT NULL, " +
                                "POSTCODE VARCHAR2(25) NOT NULL, " +
                                "BANK_NAME VARCHAR2(55) NOT NULL, " +
                                "ACCOUNT_NUMBER VARCHAR2(25) NOT NULL, " +
                                "SORT_CODE VARCHAR2(25) NOT NULL" +
                                ")");
                    } catch (SQLException e) {
                        logger.error("Failed to create " + tableName + " ", e);
                    }
                    break;
            }
        }

        private static void createScheduleTable(String weekID, String tableName) {
            try (Statement statement = connection.createStatement()) {
                statement.executeUpdate("CREATE TABLE NPS_SCHEDULE_" + weekID + " (" +
                        "ID NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY, " +
                        "EMPLOYEE_ID NUMBER NOT NULL, " +
                        "MON_START_TIME VARCHAR(10), " +
                        "MON_END_TIME VARCHAR(10), " +
                        "TUE_START_TIME VARCHAR(10), " +
                        "TUE_END_TIME VARCHAR(10), " +
                        "WED_START_TIME VARCHAR(10), " +
                        "WED_END_TIME VARCHAR(10), " +
                        "THU_START_TIME VARCHAR(10), " +
                        "THU_END_TIME VARCHAR(10), " +
                        "FRI_START_TIME VARCHAR(10), " +
                        "FRI_END_TIME VARCHAR(10), " +
                        "SAT_START_TIME VARCHAR(10), " +
                        "SAT_END_TIME VARCHAR(10), " +
                        "SUN_START_TIME VARCHAR(10), " +
                        "SUN_END_TIME VARCHAR(10)" +
                        ")");
            } catch (SQLException e) {
                logger.error("Failed to create " + tableName + " ", e);
            }
        }
    }

    // ********************************************
    // ************ CONSTRAINT METHODS ************
    // ********************************************

    class UpdateTablesConstraints {
        // Method to update tables to include foreign keys and other constraints
        private static void updateTables(String tableName) {
            logger.info("Updating table " + tableName);

            // Update tables to include foreign keys and other constraints
            switch (tableName) {
                case "NPS_BANK_DETAILS":
                    employeeIDForeignKey(tableName, "fk_bank_employee");
                    break;
                //case "NPS_PAYROLL":
                    //employeeIDForeignKey(tableName, "fk_payroll_employee");
                    //break;
                case "NPS_LOGIN":
                    employeeIDForeignKey(tableName, "fk_login_employee");
                    break;
                case "NPS_EMERGENCY_CONTACT":
                    employeeIDForeignKey(tableName, "fk_emergency_employee");
                    break;
                case "NPS_ADDRESSES":
                    employeeIDForeignKey(tableName, "fk_employee");
                    break;
                case "NPS_SCHEDULE_0":
                    employeeIDForeignKey(tableName, "fk_schedule_0_employee");
                    break;
                case "NPS_SCHEDULE_1":
                    employeeIDForeignKey(tableName, "fk_schedule_1_employee");
                    break;
                case "NPS_SCHEDULE_2":
                    employeeIDForeignKey(tableName, "fk_schedule_2_employee");
                    break;
                case "NPS_SCHEDULE_3":
                    employeeIDForeignKey(tableName, "fk_schedule_3_employee");
                    break;
                // Add cases for other tables...
                default:
                    logger.info("No constraints set for " + tableName);
            }
        }

        private static void employeeIDForeignKey(String tableName, String constraintName) {
            try (Statement statement = connection.createStatement()) {
                statement.executeUpdate("ALTER TABLE " + tableName + " ADD CONSTRAINT " + constraintName + " " +
                        "FOREIGN KEY (EMPLOYEE_ID) REFERENCES NPS_EMPLOYEE(ID)");
            } catch (SQLException e) {
                logger.error("Failed to update " + tableName + " ", e);
            }
        }
    }

    // ********************************************
    // ************ TABLE ADD METHODS *************
    // ********************************************

    public class AddTableData {
        // Method to add an employee record
        public static void addEmployee(String firstName, String lastName, String email, String phone, String salary,
                                       String niNumber, int accessLevel, String location, String contractType,
                                       String contactHours, String department, String jobTitle, boolean firstLogin) {
            // Establish the database connection
            DatabaseConnection.getConnectionToDB();

            // Add employee record
            try (PreparedStatement preparedStatement = connection.prepareStatement(
                    "INSERT INTO NPS_EMPLOYEE (FIRST_NAME, LAST_NAME, EMAIL, PHONE, SALARY, NI_NUMBER, LOCATION," +
                            "CONTRACT_TYPE, CONTRACT_HOURS, DEPARTMENT, JOB_TITLE) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)")) {
                preparedStatement.setString(1, Encryption.encryptString(firstName));
                preparedStatement.setString(2, Encryption.encryptString(lastName));
                preparedStatement.setString(3, Encryption.encryptString(email));
                preparedStatement.setString(4, Encryption.encryptString(phone));
                preparedStatement.setDouble(5, Double.parseDouble(salary));
                preparedStatement.setString(6, Encryption.encryptString(niNumber));
                preparedStatement.setString(7, Encryption.encryptString(location));
                preparedStatement.setString(8, Encryption.encryptString(contractType));
                preparedStatement.setDouble(9, Double.parseDouble(contactHours));
                preparedStatement.setString(10, Encryption.encryptString(department));
                preparedStatement.setString(11, Encryption.encryptString(jobTitle));

                int rowsAffected = preparedStatement.executeUpdate();
                if (rowsAffected > 0) {
                    logger.info("Employee added successfully.");

                    // Create a login for the employee
                    int employeeId = GetTableData.getEmployeeId(email);
                    createLogin(employeeId, email, firstName, niNumber, accessLevel, firstLogin);

                    // Add bank details for the employee
                    addBankDetails(String.valueOf(employeeId), Encryption.encryptString("Bank Name"),
                            Encryption.encryptString("Account Number"), Encryption.encryptString("Sort Code"));

                    // Add payroll info for the employee
                    AddTableData.addPayrollInfo(String.valueOf(employeeId), GetTableData.getEmailDateInfo(),
                            MiscMethod.getCurrentMonthString(), MiscMethod.getCurrentYear(),
                            0.0, "0%", 0.0, 0.0, 0.0, 0.0,
                            0.0, 0.0);

                    // Add emergency contact info for the employee
                    addEmergencyDetails(Encryption.encryptString("email"), Encryption.encryptString("First Name"),
                            Encryption.encryptString("Last Name"), Encryption.encryptString("Mobile"),
                            Encryption.encryptString("Relationship"), employeeId);

                    // Add schedule info for the employee
                    InitialiseTables.initialiseScheduleForAllEmployees();

                    if (!firstLogin)
                    {
                        SingleEmail.sendAccountCreationEmail(email, firstName);
                    }

                } else {
                    logger.info("Failed to add employee.");
                }
            } catch (SQLException e) {
                logger.error("Failure during SQL query - adding employee data", e);
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
                    preparedStatement.setString(3, Encryption.hash("admin"));
                    preparedStatement.setString(4, Encryption.hash("admin"));
                } else {
                    preparedStatement.setString(3, Encryption.hash(email));

                    // Get last 4 character of NI number
                    niNumber = niNumber.substring(niNumber.length() - 4);
                    // Sets the password to the employee's first name an "_" and the last 4 characters of NIN
                    String hashedPswd = Encryption.hash(firstName + "_" + niNumber);
                    preparedStatement.setString(4, hashedPswd);
                }

                int rowsAffected = preparedStatement.executeUpdate();
                if (rowsAffected > 0) {
                    logger.info("Login created successfully.");
                } else {
                    logger.info("Failed to create login.");
                }
            } catch (SQLException e) {
                logger.error("Failure during SQL query - creating login", e);
            }
        }

        // Method to add bank details
        public static void addBankDetails(String employeeId, String bankName, String accountNumber, String sortCode) {
            // Add record
            try (PreparedStatement preparedStatement = connection.prepareStatement(
                    "INSERT INTO NPS_BANK_DETAILS (EMPLOYEE_ID, BANK_NAME, ACCOUNT_NUMBER, SORT_CODE) VALUES (?, ?, ?, ?)")) {
                preparedStatement.setInt(1, Integer.parseInt(employeeId));
                preparedStatement.setString(2, bankName);
                preparedStatement.setString(3, accountNumber);
                preparedStatement.setString(4, sortCode);

                int rowsAffected = preparedStatement.executeUpdate();
                if (rowsAffected > 0) {
                    logger.info("Bank details added successfully.");
                } else {
                    logger.info("Failed to add bank details.");
                }
            } catch (SQLException e) {
                logger.error("Failure during SQL query - adding bank details", e);
            }
        }

        // Methods that will decide whether to add or update a payroll record - depends if we're in the same month currently
        public static void addOrUpdatePayrollInfo(String employeeId, String payDate, String month, int year, double hoursWorked,
                                                  String pension,double pensionPaid, double overtimeHours, double overtimePay, double grossPay,
                                                  double taxes, double netPay) {
            // Check if payroll info already exists for the given month and year
            boolean payrollExists = CheckTableData.checkPayrollExists(employeeId, month, year);
            if (payrollExists) {
                // Update existing payroll info
                UpdateTableData.updatePayrollInfo(employeeId, hoursWorked, pensionPaid, overtimeHours, overtimePay, grossPay, taxes, netPay);
            } else {
                // Add new payroll info
                AddTableData.addPayrollInfo(employeeId, payDate, month, year, hoursWorked, pension, pensionPaid, overtimeHours, overtimePay, grossPay, taxes, netPay);
            }
        }

        // Method to add payroll info
        public static void addPayrollInfo(String employeeId, String payDate, String month, int year, double hoursWorked,
                                          String pension,double pensionPaid, double overtimeHours, double overtimePay, double grossPay,
                                          double taxes, double netPay) {


            if (payDate == null) {
                MiscMethod.getCurrentMonthString();
            }

            payDate = payDate.substring(0, 10);

            // Add record
            try (PreparedStatement preparedStatement = connection.prepareStatement(
                    "INSERT INTO NPS_PAYROLL (EMPLOYEE_ID, PAY_DATE, PAY_MONTH, YEAR, HOURS_WORKED, PENSION, " +
                            "PENSION_PAID, OVERTIME_HOURS, OVERTIME_PAY, GROSS_PAY, TAXES, NET_PAY) " +
                            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)")) {

                preparedStatement.setInt(1, Integer.parseInt(employeeId));
                preparedStatement.setString(2, payDate);
                preparedStatement.setString(3, month);
                preparedStatement.setInt(4, year);
                preparedStatement.setDouble(5, hoursWorked);
                preparedStatement.setString(6, pension);
                preparedStatement.setDouble(7, pensionPaid);
                preparedStatement.setDouble(8, overtimeHours);
                preparedStatement.setDouble(9, overtimePay);
                preparedStatement.setDouble(10, grossPay);
                preparedStatement.setDouble(11, taxes);
                preparedStatement.setDouble(12, netPay);

                int rowsAffected = preparedStatement.executeUpdate();
                if (rowsAffected > 0) {
                    logger.info("Payroll info added successfully.");
                } else {
                    logger.info("Failed to add payroll info.");
                }
            } catch (SQLException e) {
                logger.error("Failure during SQL query - adding payroll info", e);
            }
        }


        // Method to add emergency contact info
        public static void addEmergencyDetails(String email, String fName, String lName, String mobile, String relationship, int employeeID) {
            // Add record
            try (PreparedStatement preparedStatement = connection.prepareStatement(
                    "INSERT INTO NPS_EMERGENCY_CONTACT (EMPLOYEE_ID, FIRST_NAME, LAST_NAME, PHONE, RELATIONSHIP) " +
                            "VALUES (?, ?, ?, ?, ?)")) {
                preparedStatement.setInt(1, employeeID);
                preparedStatement.setString(2, fName);
                preparedStatement.setString(3, lName);
                preparedStatement.setString(4, mobile);
                preparedStatement.setString(5, relationship);

                int rowsAffected = preparedStatement.executeUpdate();
                if (rowsAffected > 0) {
                    logger.info("Emergency contact info added successfully.");
                } else {
                    logger.info("Failed to add emergency contact info.");
                }
            } catch (SQLException e) {
                logger.error("Failure during SQL query - adding emergency contact details", e);
            }
        }

        // Method to add help info
        public static void addHelp(String errorCode, String Title, String Description, String addedBy) {
            // Add record
            try (PreparedStatement preparedStatement = connection.prepareStatement(
                    "INSERT INTO NPS_HELP_INFO (ERROR_CODE, TITLE, DESCRIPTION, ADDED_BY) VALUES (?, ?, ?, ?)")) {
                preparedStatement.setString(1, errorCode);
                preparedStatement.setString(2, Title);
                preparedStatement.setString(3, Description);
                preparedStatement.setString(4, addedBy);

                int rowsAffected = preparedStatement.executeUpdate();
                if (rowsAffected > 0) {
                    logger.info("Help info added successfully.");
                } else {
                    logger.info("Failed to add help info.");
                }
            } catch (SQLException e) {
                logger.error("Failure during SQL query - adding help", e);
            }
        }

        // Method to add email info
        public static void addEmailInfo(String fromUserEmail, String fromUserPassword, String emailDate) {
            // Add record
            try (PreparedStatement preparedStatement = connection.prepareStatement(
                    "INSERT INTO NPS_EMAIL_INFO (FROM_USER_EMAIL, FROM_USER_PASSWORD, EMAIL_DATE) VALUES (?, ?, ?)")) {
                preparedStatement.setString(1, Encryption.encryptString(fromUserEmail));
                preparedStatement.setString(2, Encryption.encryptString(fromUserPassword));
                preparedStatement.setString(3, emailDate);

                int rowsAffected = preparedStatement.executeUpdate();
                if (rowsAffected > 0) {
                    logger.info("Email info added successfully.");
                } else {
                    logger.info("Failed to add email info.");
                }
            } catch (SQLException e) {
                logger.error("Failure during SQL query - adding email", e);
            }
        }

        // Method to add the employees details to the deleted users table when they are removed - NPS_DELETED_USERS
        public static boolean addEmployeeToDeletedUsersTable(String deletedBy, String firstName, String lastName, String email, String phone, String niNumber,
                                                          String addressLine1, String addressLine2, String city, String postcode,
                                                          String bankName, String accountNumber, String sortCode) {

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
            // Get the current timestamp
            LocalDateTime now = LocalDateTime.now();
            String addedDate = now.format(formatter);
            // Add 1 year to the current timestamp
            LocalDateTime addedOnPlusOneYear  = now.plusYears(1);
            String deleteDate = addedOnPlusOneYear.format(formatter);

            // Add employee record
            try (PreparedStatement preparedStatement = connection.prepareStatement(
                    "INSERT INTO NPS_DELETED_USERS (ADDED_DATE, DELETE_DATE, DELETED_BY, FIRST_NAME, LAST_NAME, " +
                            "EMAIL, PHONE, NI_NUMBER, ADDRESS_LINE_1, ADDRESS_LINE_2, CITY, POSTCODE, BANK_NAME," +
                            "ACCOUNT_NUMBER, SORT_CODE) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)")) {
                preparedStatement.setString(1, Encryption.encryptString(addedDate));
                preparedStatement.setString(2, Encryption.encryptString(deleteDate));
                preparedStatement.setString(3, Encryption.encryptString(deletedBy));
                preparedStatement.setString(4, Encryption.encryptString(firstName));
                preparedStatement.setString(5, Encryption.encryptString(lastName));
                preparedStatement.setString(6, Encryption.encryptString(email));
                preparedStatement.setString(7, Encryption.encryptString(phone));
                preparedStatement.setString(8, Encryption.encryptString(niNumber));
                preparedStatement.setString(9, Encryption.encryptString(addressLine1));
                preparedStatement.setString(10, Encryption.encryptString(addressLine2));
                preparedStatement.setString(11, Encryption.encryptString(city));
                preparedStatement.setString(12, Encryption.encryptString(postcode));
                preparedStatement.setString(13, Encryption.encryptString(bankName));
                preparedStatement.setString(14, Encryption.encryptString(accountNumber));
                preparedStatement.setString(15, Encryption.encryptString(sortCode));

                int rowsAffected = preparedStatement.executeUpdate();

                if (rowsAffected > 0) {
                    logger.info("Employee added successfully.");
                    return true;
                } else {
                    logger.info("Failed to add employee.");
                    return false;
                }
            } catch (SQLException e) {
                logger.error("Failure during SQL query - adding employee data", e);
                return false;
            }
        }
    }

    // ********************************************
    // *********** TABLE UPDATE METHODS ***********
    // ********************************************

    public class UpdateTableData {
        public static void updateEmployeeProfile(String employeeId, String firstName, String lastName, String email,
                                                 String phone, String niNumber, String address1, String address2,
                                                 String postcode, String city, String bankName, String accountNumber,
                                                 String sortCode, String emergencyContactFName, String emergencyContactLName,
                                                 String emergencyContactMobile, String emergencyContactRelationship,
                                                 String pensionCon) {

            // Check if the email is being updated
            String oldEmail = GetTableData.getEmailById(employeeId); // Retrieve the old email from the database
            boolean emailUpdated = !Encryption.hash(email).equals(oldEmail);

            // Update the employee login if the email is being updated
            if (emailUpdated) {
                // Update the login username
                try (PreparedStatement preparedStatement = connection.prepareStatement(
                        "UPDATE NPS_LOGIN SET USERNAME = ? WHERE EMPLOYEE_ID = ?")) {
                    preparedStatement.setString(1, Encryption.hash(email));
                    preparedStatement.setString(2, employeeId);

                    int rowsAffected = preparedStatement.executeUpdate();

                    if (rowsAffected > 0) {
                        logger.info("Username successfully updated");
                    } else {
                        logger.error("Failed to update username");
                    }
                } catch (SQLException e) {
                    logger.error("Failure during SQL query - updating login info", e);
                }
            }

            // Update the employee record
            try (PreparedStatement preparedStatement = connection.prepareStatement(
                    "UPDATE NPS_EMPLOYEE SET FIRST_NAME = ?, LAST_NAME = ?, EMAIL = ?, PHONE = ?, NI_NUMBER = ? WHERE ID = ?")) {
                preparedStatement.setString(1, Encryption.encryptString(firstName));
                preparedStatement.setString(2, Encryption.encryptString(lastName));
                preparedStatement.setString(3, Encryption.encryptString(email));
                preparedStatement.setString(4, Encryption.encryptString(phone));
                preparedStatement.setString(5, Encryption.encryptString(niNumber));
                preparedStatement.setString(6, employeeId);

                int rowsAffected = preparedStatement.executeUpdate();
                if (rowsAffected > 0) {
                    logger.info("Employee record updated successfully.");
                } else {
                    logger.info("No employee found with the given ID.");
                }
            } catch (SQLException e) {
                logger.error("Failure during SQL query - updating employee personal data", e);
            }

            // Update the employee address
            try (PreparedStatement preparedStatement = connection.prepareStatement(
                    "UPDATE NPS_ADDRESSES SET ADDRESS_LINE_1 = ?, ADDRESS_LINE_2 = ?, CITY = ?, POSTCODE = ? WHERE EMPLOYEE_ID = ?")) {
                preparedStatement.setString(1, Encryption.encryptString(address1));
                preparedStatement.setString(2, Encryption.encryptString(address2));
                preparedStatement.setString(3, Encryption.encryptString(city));
                preparedStatement.setString(4, Encryption.encryptString(postcode));
                preparedStatement.setString(5, employeeId);

                int rowsAffected = preparedStatement.executeUpdate();
                if (rowsAffected > 0) {
                    logger.info("Employee address updated successfully.");
                } else {
                    logger.info("No employee found with the given ID.");
                    // Add the address if it doesn't exist
                    try (PreparedStatement preparedStatement2 = connection.prepareStatement(
                            "INSERT INTO NPS_ADDRESSES (EMPLOYEE_ID, ADDRESS_LINE_1, ADDRESS_LINE_2, CITY, POSTCODE) VALUES (?, ?, ?, ?, ?)")) {
                        preparedStatement2.setInt(1, Integer.parseInt(employeeId));
                        preparedStatement2.setString(2, Encryption.encryptString(address1));
                        preparedStatement2.setString(3, Encryption.encryptString(address2));
                        preparedStatement2.setString(4, Encryption.encryptString(city));
                        preparedStatement2.setString(5, Encryption.encryptString(postcode));

                        int rowsAffected2 = preparedStatement2.executeUpdate();
                        if (rowsAffected2 > 0) {
                            logger.info("Employee address added successfully.");
                        } else {
                            logger.info("Failed to add employee address.");
                        }
                    } catch (SQLException e) {
                        logger.error("Failure during SQL query - adding address data", e);
                    }
                }
            } catch (SQLException e) {
                logger.error("Failure during SQL query - updating address data", e);
            }

            // Update the employee bank details
            try (PreparedStatement preparedStatement = connection.prepareStatement(
                    "UPDATE NPS_BANK_DETAILS SET BANK_NAME = ?, ACCOUNT_NUMBER = ?, SORT_CODE = ? WHERE EMPLOYEE_ID = ?")) {
                preparedStatement.setString(1, Encryption.encryptString(bankName));
                preparedStatement.setString(2, Encryption.encryptString(accountNumber));
                preparedStatement.setString(3, Encryption.encryptString(sortCode));
                preparedStatement.setString(4, employeeId);

                int rowsAffected = preparedStatement.executeUpdate();
                if (rowsAffected > 0) {
                    logger.info("Employee bank details updated successfully.");
                } else {
                    logger.info("No employee found with the given ID.");
                    // Add the bank details if they don't exist
                    try (PreparedStatement preparedStatement2 = connection.prepareStatement(
                            "INSERT INTO NPS_BANK_DETAILS (EMPLOYEE_ID, BANK_NAME, ACCOUNT_NUMBER, SORT_CODE) VALUES (?, ?, ?, ?)")) {
                        preparedStatement2.setInt(1, Integer.parseInt(employeeId));
                        preparedStatement2.setString(2, Encryption.encryptString(bankName));
                        preparedStatement2.setString(3, Encryption.encryptString(accountNumber));
                        preparedStatement2.setString(4, sortCode);

                        int rowsAffected2 = preparedStatement2.executeUpdate();
                        if (rowsAffected2 > 0) {
                            logger.info("Employee bank details added successfully.");
                        } else {
                            logger.info("Failed to add employee bank details.");
                        }
                    } catch (SQLException e) {
                        logger.error("Failure during SQL query - updating bank details", e);
                    }
                }
            } catch (SQLException e) {
                logger.error("Failure during SQL query - updating employee profile", e);
            }

            // Update the employee emergency contact details
            try (PreparedStatement preparedStatement = connection.prepareStatement(
                    "UPDATE NPS_EMERGENCY_CONTACT SET FIRST_NAME = ?, LAST_NAME = ?, PHONE = ?, RELATIONSHIP = ? WHERE EMPLOYEE_ID = ?")) {
                preparedStatement.setString(1, Encryption.encryptString(emergencyContactFName));
                preparedStatement.setString(2, Encryption.encryptString(emergencyContactLName));
                preparedStatement.setString(3, Encryption.encryptString(emergencyContactMobile));
                preparedStatement.setString(4, Encryption.encryptString(emergencyContactRelationship));
                preparedStatement.setString(5, employeeId);

                int rowsAffected = preparedStatement.executeUpdate();
                if (rowsAffected > 0) {
                    logger.info("Employee emergency contact details updated successfully.");
                } else {
                    logger.info("No employee found with the given ID.");
                    // Add the emergency contact details if they don't exist
                    try (PreparedStatement preparedStatement2 = connection.prepareStatement(
                            "INSERT INTO NPS_EMERGENCY_CONTACT (EMPLOYEE_ID, FIRST_NAME, LAST_NAME, PHONE, RELATIONSHIP) VALUES (?, ?, ?, ?, ?)")) {
                        preparedStatement2.setInt(1, Integer.parseInt(employeeId));
                        preparedStatement2.setString(2, Encryption.encryptString(emergencyContactFName));
                        preparedStatement2.setString(3, Encryption.encryptString(emergencyContactLName));
                        preparedStatement2.setString(4, Encryption.encryptString(emergencyContactMobile));
                        preparedStatement2.setString(5, Encryption.encryptString(emergencyContactRelationship));

                        int rowsAffected2 = preparedStatement2.executeUpdate();
                        if (rowsAffected2 > 0) {
                            logger.info("Employee emergency contact details added successfully.");
                        } else {
                            logger.info("Failed to add employee emergency contact details.");
                        }
                    } catch (SQLException e) {
                        logger.error("Failure during SQL query - adding emergency contact", e);
                    }
                }
            } catch (SQLException e) {
                logger.error("Failure during SQL query - updating emergency contact", e);
            }

            // Update the payroll - specifically the pension contribution
            try (PreparedStatement preparedStatement = connection.prepareStatement(
                    "UPDATE NPS_PAYROLL SET PENSION = ? WHERE EMPLOYEE_ID = ? AND PAY_MONTH = ?")) {
                preparedStatement.setString(1, pensionCon);
                preparedStatement.setString(2, employeeId);
                preparedStatement.setString(3, MiscMethod.getCurrentMonthString());

                int rowsAffected = preparedStatement.executeUpdate();
                if (rowsAffected > 0) {
                    logger.info("Employee pension updated successfully.");
                }
                else {
                    logger.info("No payroll for employee found - creating payroll info.");
                    AddTableData.addPayrollInfo(employeeId, GetTableData.getEmailDateInfo(), MiscMethod.getCurrentMonthString(), MiscMethod.getCurrentYear(), 0.0,
                            pensionCon, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0);
                }
            } catch (SQLException e) {
                logger.error("Failure during SQL query - updating payroll", e);
            }
        }

        // Method to update employee records
        public static void updateEmployee(String employeeId, String firstName, String lastName, String email, String phone,
                                          String salary, String niNumber, int accessLevel, String location,
                                          String contractType, String contractHours, String department, String jobTitle) {
            // Check if the access level is being updated
            String oldAccessLevel = GetTableData.getAccessLevel(email); // Retrieve the old access level from the database
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
                        logger.info("Login access level updated successfully.");
                    } else {
                        logger.info("Failed to update login access level.");
                    }
                } catch (SQLException e) {
                    logger.error("Failure during SQL query - updating employee access level", e);
                }
            }

            // Check if the email is being updated
            String oldEmail = GetTableData.getEmailById(employeeId); // Retrieve the old email from the database
            boolean emailUpdated = ! Encryption.hash(email).equals(oldEmail);

            // Update the employee login if the email is being updated
            if (emailUpdated) {
                // Update the login username
                try (PreparedStatement preparedStatement = connection.prepareStatement(
                        "UPDATE NPS_LOGIN SET USERNAME = ? WHERE EMPLOYEE_ID = ?")) {
                    preparedStatement.setString(1, Encryption.hash(email));
                    preparedStatement.setString(2, employeeId);

                    int rowsAffected = preparedStatement.executeUpdate();
                    if (rowsAffected > 0) {
                        logger.info("Login username updated successfully.");
                    } else {
                        logger.info("Failed to update login username.");
                    }
                } catch (SQLException e) {
                    logger.error("Failure during SQL query - updating employee email", e);
                }
            }

            // Update the employee record
            try (PreparedStatement preparedStatement = connection.prepareStatement(
                    "UPDATE NPS_EMPLOYEE SET FIRST_NAME = ?, LAST_NAME = ?, EMAIL = ?, PHONE = ?, SALARY = ?, " +
                            "NI_NUMBER = ?, LOCATION = ?, CONTRACT_TYPE = ?, CONTRACT_HOURS = ?, DEPARTMENT = ?, J" +
                            "OB_TITLE = ? WHERE ID = ?")) {
                //BigDecimal salaryDecimal = new BigDecimal(salary);

                preparedStatement.setString(1, Encryption.encryptString(firstName));
                preparedStatement.setString(2, Encryption.encryptString(lastName));
                preparedStatement.setString(3, Encryption.encryptString(email));
                preparedStatement.setString(4, Encryption.encryptString(phone));
                preparedStatement.setDouble(5, Double.parseDouble(salary));
                preparedStatement.setString(6, Encryption.encryptString(niNumber));
                preparedStatement.setString(7, Encryption.encryptString(location));
                preparedStatement.setString(8, Encryption.encryptString(contractType));
                preparedStatement.setDouble(9, Double.parseDouble(contractHours));
                preparedStatement.setString(10, Encryption.encryptString(department));
                preparedStatement.setString(11, Encryption.encryptString(jobTitle));
                preparedStatement.setString(12, employeeId);

                int rowsAffected = preparedStatement.executeUpdate();
                if (rowsAffected > 0) {
                    logger.info("Employee record updated successfully.");
                } else {
                    logger.info("No employee found with the given ID.");
                }

            } catch (SQLException e) {
                logger.error("Failure during SQL query - updating employee personal data", e);
            }
        }

        // Method to update the email info
        public static void updateEmailInfo(String fromUserEmail, String fromUserPassword, String emailDate) {
            try (PreparedStatement preparedStatement = connection.prepareStatement(
                    "UPDATE NPS_EMAIL_INFO SET FROM_USER_EMAIL = ?, FROM_USER_PASSWORD = ?, EMAIL_DATE = ?")) {
                preparedStatement.setString(1, Encryption.encryptString(fromUserEmail));
                preparedStatement.setString(2, Encryption.encryptString(fromUserPassword));
                preparedStatement.setString(3, emailDate);

                int rowsAffected = preparedStatement.executeUpdate();
                if (rowsAffected > 0) {
                    logger.info("Email info updated successfully.");
                } else {
                    logger.info("Failed to update email info.");
                }
            } catch (SQLException e) {
                logger.error("Failure during SQL query - updating email data", e);
            }
        }

        // Check if schedule entry exists, if not, add it and update it if it does
        public static void updateSchedule(Schedule schedule) {
            // Check if schedule entry exists
            if (CheckTableData.scheduleRecordExists(schedule.getEmployeeID(), schedule.getWeekId())) {
                // Update the schedule entry for each day of the week
                for (String day : schedule.getDays()) {
                    String shortDay = MiscMethod.getFirstThreeLetter(day);

                    try (PreparedStatement preparedStatement = connection.prepareStatement(
                            "UPDATE NPS_SCHEDULE_" + schedule.getWeekId() + " SET " +
                                    shortDay + "_START_TIME = ?, " + shortDay + "_END_TIME = ? " +
                                    "WHERE EMPLOYEE_ID = ?")) {
                        preparedStatement.setString(1, schedule.getStartTime(day));
                        preparedStatement.setString(2, schedule.getEndTime(day));
                        preparedStatement.setInt(3, Integer.parseInt(schedule.getEmployeeID()));

                        int rowsAffected = preparedStatement.executeUpdate();
                        if (rowsAffected > 0) {
                            logger.info("Schedule updated");
                        } else {
                            logger.info("Schedule failed to update");
                        }
                    } catch (SQLException e) {
                        logger.error("Failure during SQL query - updating schedule for each day", e);
                    }
                }
            } else {
                // Add a new schedule entry for the employee
                try (PreparedStatement preparedStatement = connection.prepareStatement(
                        "INSERT INTO NPS_SCHEDULE_" + schedule.getWeekId() + "  (EMPLOYEE_ID) VALUES (?)")) {
                    preparedStatement.setInt(1, Integer.parseInt(schedule.getEmployeeID()));

                    int rowsAffected = preparedStatement.executeUpdate();
                    if (rowsAffected > 0) {
                        logger.info("Employee record added successfully.");
                    } else {
                        logger.info("Failed to add employee record.");
                    }
                } catch (SQLException e) {
                    logger.error("Failure during SQL query - creating employee record in schedule", e);
                }

                // Update the schedule entry for each day of the week
                for (String day : schedule.getDays()) {
                    String shortDay = MiscMethod.getFirstThreeLetter(day);

                    try (PreparedStatement preparedStatement = connection.prepareStatement(
                            "UPDATE NPS_SCHEDULE_" + schedule.getWeekId() + "  SET " +
                                    shortDay + "_START_TIME = ?, " + shortDay + "_END_TIME = ? " +
                                    "WHERE EMPLOYEE_ID = ?")) {
                        preparedStatement.setString(1, schedule.getStartTime(day));
                        preparedStatement.setString(2, schedule.getEndTime(day));
                        preparedStatement.setInt(3, Integer.parseInt(schedule.getEmployeeID()));

                        int rowsAffected = preparedStatement.executeUpdate();
                        if (rowsAffected > 0) {
                            logger.info("Schedule updated successfully.");
                        } else {
                            logger.info("Failed to update schedule.");
                        }
                    } catch (SQLException e) {
                        logger.error("Failure during SQL query - updating schedule for each day", e);
                    }
                }
            }
        }

        // Method to update email date in email info table
        public static void updateEmailDate(String newEmailDate) {
            try (PreparedStatement preparedStatement = connection.prepareStatement(
                    "UPDATE NPS_EMAIL_INFO SET EMAIL_DATE = ?")) {
                preparedStatement.setString(1, newEmailDate);
                preparedStatement.executeUpdate();
            } catch (SQLException e) {
                logger.error("Failure during SQL query - updating email date", e);
            }
        }

        public static void updatePayrollInfo(String employeeId, double hoursWorked, double pensionPaid,
                                             double overTimeWorked, double overtimePay, double grossPay, double taxes,
                                             double netPay) {

            try (PreparedStatement preparedStatement = connection.prepareStatement(
                    "UPDATE NPS_PAYROLL SET HOURS_WORKED = ?, PENSION_PAID = ?, OVERTIME_HOURS = ?, OVERTIME_PAY = ?, " +
                            "GROSS_PAY = ?, TAXES = ?, NET_PAY = ? WHERE EMPLOYEE_ID = ? AND PAY_MONTH = ?")) {

                preparedStatement.setDouble(1, hoursWorked);
                preparedStatement.setDouble(2, pensionPaid);
                preparedStatement.setDouble(3, overTimeWorked);
                preparedStatement.setDouble(4, overtimePay);
                preparedStatement.setDouble(5, grossPay);
                preparedStatement.setDouble(6, taxes);
                preparedStatement.setDouble(7, netPay);
                preparedStatement.setString(8, employeeId);
                preparedStatement.setString(9, MiscMethod.getCurrentMonthString());

                int rowsAffected = preparedStatement.executeUpdate();
                if (rowsAffected > 0) {
                    logger.info("Payroll info updated successfully for employee ID: " + employeeId);
                } else {
                    logger.info("No records to update for employee ID: " + employeeId);
                }
            } catch (SQLException e) {
                logger.error("Failure during SQL query - updating payroll info", e);
            }
        }

        // Method to update help info
        public static void updateHelp(String errorCode, String title, String description, String addedBy) {
            try (PreparedStatement preparedStatement = connection.prepareStatement(
                    "UPDATE NPS_HELP_INFO SET TITLE = ?, DESCRIPTION = ?, ADDED_BY = ? WHERE ERROR_CODE = ?")) {
                preparedStatement.setString(1, title);
                preparedStatement.setString(2, description);
                preparedStatement.setString(3, addedBy);
                preparedStatement.setString(4, errorCode);

                int rowsAffected = preparedStatement.executeUpdate();
                if (rowsAffected > 0) {
                    logger.info("Help info updated successfully.");
                } else {
                    logger.info("No records updated. Help info with error code '" + errorCode + "' not found.");
                }
            } catch (SQLException e) {
                logger.error("Failure during SQL query - updating help", e);
            }
        }
    }

    // ********************************************
    // ************ TABLE READ METHODS ************
    // ********************************************

    public class CheckTableData {
        // Method to check if the username and password match
        public static boolean checkLogin(String username, String password) {
            // Connection details
            String url = DatabaseController.DatabaseChecks.getEnvVariable("DB_URL");
            String dbUsername = DatabaseController.DatabaseChecks.getEnvVariable("DB_USERNAME");
            String dbPassword = DatabaseController.DatabaseChecks.getEnvVariable("DB_PASSWORD");

            // SQL query to check if the username and password match
            String query = "SELECT COUNT(*) AS count FROM NPS_LOGIN WHERE USERNAME = ? AND PASSWORD = ?";

            try (Connection connection = DriverManager.getConnection(url, dbUsername, dbPassword);
                 PreparedStatement preparedStatement = connection.prepareStatement(query)) {

                // Set parameters for the query
                preparedStatement.setString(1, Encryption.hash(username));
                preparedStatement.setString(2, Encryption.hash(password));

                ResultSet resultSet = preparedStatement.executeQuery();

                // Check if a record with the given username and password exists
                if (resultSet.next()) {
                    int count = resultSet.getInt("count");

                    if (count > 0) {
                        logger.info("Login successful.");
                        currentLoggedInEmployeeId = String.valueOf(GetTableData.getEmployeeId(username));
                    } else {
                        logger.info("Login failed.");
                    }

                    return count > 0; // If count > 0, a match is found
                }
            } catch (SQLException e) {
                logger.error("Failure during SQL query - checking login details", e);
            }

            return false; // Default to false if an exception occurs or no match is found
        }

        private static boolean checkPayrollExists(String employeeId, String month, int year) {
            try (PreparedStatement preparedStatement = connection.prepareStatement(
                    "SELECT COUNT(*) FROM NPS_PAYROLL WHERE EMPLOYEE_ID = ? AND PAY_MONTH = ? AND YEAR = ?")) {
                preparedStatement.setInt(1, Integer.parseInt(employeeId));
                preparedStatement.setString(2, month);
                preparedStatement.setInt(3, year);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        int rowCount = resultSet.getInt(1);
                        return rowCount > 0;
                    }
                }
            } catch (SQLException e) {
                logger.error("Failure during SQL query - checking payroll existence", e);
            }
            return false;
        }

        // Method to check if an admin account exists
        private static boolean checkLoginWithAccessLevelZero() {
            boolean loginExists = false;
            try (PreparedStatement preparedStatement = connection.prepareStatement(
                    "SELECT * FROM NPS_LOGIN WHERE ACCESS_LEVEL = 0")) {
                ResultSet resultSet = preparedStatement.executeQuery();
                loginExists = resultSet.next();
            } catch (SQLException e) {
                logger.error("Failure during SQL query - checking if a login has level zero", e);
            }
            return loginExists;
        }

        // Method to check if there is email info in the database
        private static boolean checkEmailInfo() {
            boolean emailExists = false;

            try (PreparedStatement preparedStatement = connection.prepareStatement(
                    "SELECT * FROM NPS_EMAIL_INFO")) {
                ResultSet resultSet = preparedStatement.executeQuery();
                emailExists = resultSet.next();
            } catch (SQLException e) {
                logger.error("Failure during SQL query - checking if email info exists", e);
            }

            return emailExists;
        }

        // Method to check if a schedule record exists
        private static boolean scheduleRecordExists(String employeeId, String weekID) {
            try (PreparedStatement preparedStatement = connection.prepareStatement(
                    "SELECT * FROM NPS_SCHEDULE_" + weekID + " WHERE EMPLOYEE_ID = ?")) {
                preparedStatement.setInt(1, Integer.parseInt(employeeId));

                ResultSet resultSet = preparedStatement.executeQuery();
                return resultSet.next();
            } catch (SQLException e) {
                logger.error("Failure during SQL query - checking if schedule exists", e);
                return false;
            }
        }

        // Check if an employee exists
        public static boolean employeeExists(String email) {
            boolean employeeExists = false;
            try (PreparedStatement preparedStatement = connection.prepareStatement(
                    "SELECT * FROM NPS_EMPLOYEE WHERE EMAIL = ?")) {
                preparedStatement.setString(1, Encryption.encryptString(email));
                ResultSet resultSet = preparedStatement.executeQuery();
                employeeExists = resultSet.next();
            } catch (SQLException e) {
                logger.error("Failure during SQL query - checking if an employee exists", e);
            }
            return employeeExists;
        }
    }

    public class GetTableData {
        // Method to get the employee ID by email
        public static int getEmployeeId(String email) {
            int employeeId = -1;
            try (PreparedStatement preparedStatement = connection.prepareStatement(
                    "SELECT ID FROM NPS_EMPLOYEE WHERE EMAIL = ?")) {
                preparedStatement.setString(1, Encryption.encryptString(email));

                ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    employeeId = resultSet.getInt("ID");
                }
            } catch (SQLException e) {
                logger.error("Failure during SQL query - getting employee ID by email", e);
            }
            return employeeId;
        }

        // Method to get all employees from the database and return an ObservableList for Employees table
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
                    String salary = resultSet.getString("SALARY");
                    String niNumber = resultSet.getString("NI_NUMBER");
                    String location = resultSet.getString("LOCATION");
                    String contractType = resultSet.getString("CONTRACT_TYPE");
                    String contractHours = resultSet.getString("CONTRACT_HOURS");
                    String department = resultSet.getString("DEPARTMENT");
                    String jobTitle = resultSet.getString("JOB_TITLE");

                    // Get access level
                    String accessLevel = GetTableData.getAccessLevel(Encryption.decryptString(email));



                    Person person = new Person(id, Encryption.decryptString(firstName), Encryption.decryptString(lastName),
                            Encryption.decryptString(email), Encryption.decryptString(phone),
                            salary, accessLevel, Encryption.decryptString(niNumber),
                            Encryption.decryptString(location), Encryption.decryptString(contractType),
                            contractHours, Encryption.decryptString(department), Encryption.decryptString(jobTitle));

                    data.add(person);
                }

            } catch (SQLException e) {
                logger.error("Failure during SQL query - getting all employee data", e);
            }

            return data;
        }

        public static List<Person> getAllEmployeeInfo() {
            List<Person> employees = new ArrayList<>();

            try (PreparedStatement preparedStatement = connection.prepareStatement(
                    "SELECT * FROM NPS_EMPLOYEE")) {
                ResultSet resultSet = preparedStatement.executeQuery();

                while (resultSet.next()) {
                    String id = resultSet.getString("ID");
                    String firstName = resultSet.getString("FIRST_NAME");
                    String lastName = resultSet.getString("LAST_NAME");
                    String email = resultSet.getString("EMAIL");
                    String niNumber = resultSet.getString("NI_NUMBER");

                    // Get access level
                    //String accessLevel = GetTableData.getAccessLevel(email);

                    Person person = new Person(id, Encryption.decryptString(firstName), Encryption.decryptString(lastName),
                            Encryption.decryptString(email), null, null,
                            null, Encryption.decryptString(niNumber), null, null,
                            null, null, null);

                    employees.add(person);
                }
            } catch (SQLException e) {
                logger.error("Failure during SQL query - getting all employee data", e);
            }

            return employees;
        }

        // Method to get an employees details by ID
        public static Person getEmployeeInfo() {
            return getEmployeeInfoByID(currentLoggedInEmployeeId);
        }

        public static Person getEmployeeInfoByID(String ID) {
            Person person = null;

            try (PreparedStatement preparedStatement = connection.prepareStatement(
                    "SELECT * FROM NPS_EMPLOYEE WHERE ID = ?")) {
                preparedStatement.setString(1, ID);
                ResultSet resultSet = preparedStatement.executeQuery();

                if (resultSet.next()) {
                    String id = resultSet.getString("ID");
                    String firstName = resultSet.getString("FIRST_NAME");
                    String lastName = resultSet.getString("LAST_NAME");
                    String email = resultSet.getString("EMAIL");
                    String phone = resultSet.getString("PHONE");
                    String salary = resultSet.getString("SALARY");
                    String niNumber = resultSet.getString("NI_NUMBER");
                    String location = resultSet.getString("LOCATION");
                    String contractType = resultSet.getString("CONTRACT_TYPE");
                    String contractHours = resultSet.getString("CONTRACT_HOURS");
                    String department = resultSet.getString("DEPARTMENT");
                    String jobTitle = resultSet.getString("JOB_TITLE");

                    // Get access level
                    String accessLevel = GetTableData.getAccessLevel(Encryption.decryptString(email));

                    person = new Person(id, Encryption.decryptString(firstName), Encryption.decryptString(lastName),
                            Encryption.decryptString(email), Encryption.decryptString(phone), salary,
                            accessLevel, Encryption.decryptString(niNumber),
                            Encryption.decryptString(location), Encryption.decryptString(contractType),
                            contractHours, Encryption.decryptString(department),
                            Encryption.decryptString(jobTitle));
                }
            } catch (SQLException e) {
                logger.error("Failure during SQL query - getting employee data", e);
            }

            // Get data from BANK_DETAILS Table
            try (PreparedStatement preparedStatement = connection.prepareStatement(
                    "SELECT * FROM NPS_BANK_DETAILS WHERE EMPLOYEE_ID = ?")) {
                preparedStatement.setString(1, ID);
                ResultSet resultSet = preparedStatement.executeQuery();

                if (resultSet.next()) {
                    String bankName = resultSet.getString("BANK_NAME");
                    String accountNumber = resultSet.getString("ACCOUNT_NUMBER");
                    String sortCode = resultSet.getString("SORT_CODE");

                    person.setBankName(Encryption.decryptString(bankName));
                    person.setAccountNumber(Encryption.decryptString(accountNumber));
                    person.setSortCode(Encryption.decryptString(sortCode));
                }
            } catch (SQLException e) {
                logger.error("Failure during SQL query - getting bank details data", e);
            }

            // Get data from PAYROLL Table
            try (PreparedStatement preparedStatement = connection.prepareStatement(
                    "SELECT * FROM NPS_PAYROLL WHERE EMPLOYEE_ID = ?")) {
                preparedStatement.setString(1, ID);
                ResultSet resultSet = preparedStatement.executeQuery();

                if (resultSet.next()) {
                    person.setHoursWorked(resultSet.getString("HOURS_WORKED"));
                    person.setOvertimeHours(resultSet.getString("OVERTIME_HOURS"));
                    person.setOvertimePay(resultSet.getString("OVERTIME_PAY"));
                    person.setGrossPay(resultSet.getString("GROSS_PAY"));
                    person.setTaxes(resultSet.getString("TAXES"));
                    person.setNetPay(resultSet.getString("NET_PAY"));
                    person.setPension(resultSet.getString("PENSION"));
                }
            } catch (SQLException e) {
                logger.error("Failure during SQL query - getting payroll data", e);
            }

            // Get data from EMERGENCY_CONTACT Table
            try (PreparedStatement preparedStatement = connection.prepareStatement(
                    "SELECT * FROM NPS_EMERGENCY_CONTACT WHERE EMPLOYEE_ID = ?")) {
                preparedStatement.setString(1, ID);
                ResultSet resultSet = preparedStatement.executeQuery();

                if (resultSet.next()) {
                    String emergencyContactFName = resultSet.getString("FIRST_NAME");
                    String emergencyContactLName = resultSet.getString("LAST_NAME");
                    String emergencyContactMobile = resultSet.getString("PHONE");
                    String emergencyContactRelationship = resultSet.getString("RELATIONSHIP");

                    person.setEFirstName(Encryption.decryptString(emergencyContactFName));
                    person.setELastName(Encryption.decryptString(emergencyContactLName));
                    person.setEMobile(Encryption.decryptString(emergencyContactMobile));
                    person.setERelationship(Encryption.decryptString(emergencyContactRelationship));
                }
            } catch (SQLException e) {
                logger.error("Failure during SQL query - getting emergency contact data", e);
            }

            // Get data from ADDRESSES Table
            try (PreparedStatement preparedStatement = connection.prepareStatement(
                    "SELECT * FROM NPS_ADDRESSES WHERE EMPLOYEE_ID = ?")) {
                preparedStatement.setString(1, ID);
                ResultSet resultSet = preparedStatement.executeQuery();

                if (resultSet.next()) {
                    String addressLine1 = resultSet.getString("ADDRESS_LINE_1");
                    String addressLine2 = resultSet.getString("ADDRESS_LINE_2");
                    String city = resultSet.getString("CITY");
                    String postcode = resultSet.getString("POSTCODE");

                    person.setAddressLine1(Encryption.decryptString(addressLine1));
                    person.setAddressLine2(Encryption.decryptString(addressLine2));
                    person.setCity(Encryption.decryptString(city));
                    person.setPostcode(Encryption.decryptString(postcode));
                }
            } catch (SQLException e) {
                logger.error("Failure during SQL query - getting address table data", e);
            }

            return person;
        }

        // Method to get employee access level by email
        public static String getAccessLevel(String email) {
            String accessLevel = "";
            try (PreparedStatement preparedStatement = connection.prepareStatement(
                    "SELECT ACCESS_LEVEL FROM NPS_LOGIN WHERE USERNAME = ?")) {
                preparedStatement.setString(1, Encryption.hash(email));

                ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    accessLevel = resultSet.getString("ACCESS_LEVEL");
                }
            } catch (SQLException e) {
                logger.error("Failure during SQL query - getting access level by email", e);
            }
            return accessLevel;
        }

        // Method to retrieve the email by employee ID
        public static String getEmailById(String employeeId) {
            String email = null;
            try (PreparedStatement preparedStatement = connection.prepareStatement(
                    "SELECT EMAIL FROM NPS_EMPLOYEE WHERE ID = ?")) {
                preparedStatement.setString(1, employeeId);

                ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    email = resultSet.getString("EMAIL");
                }
            } catch (SQLException e) {
                logger.error("Failure during SQL query - getting email by ID", e);
            }
            return Encryption.decryptString(email);
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
                    String addedBy = resultSet.getString("ADDED_BY");

                    HelpInfo info = new HelpInfo(errorCode, title, description, addedBy);

                    data.add(info);
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

            return data;
        }

        // Method to get email from email info table
        public static String getEmailInfo() {
            String email = null;
            try (PreparedStatement preparedStatement = connection.prepareStatement(
                    "SELECT FROM_USER_EMAIL FROM NPS_EMAIL_INFO")) {
                ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    email = resultSet.getString("FROM_USER_EMAIL");
                }
            } catch (SQLException e) {
                logger.error("Failure during SQL query - getting email info", e);
            }
            return Encryption.decryptString(email);
        }

        // Method to get password from email info table
        public static String getPasswordInfo() {
            String password = null;
            try (PreparedStatement preparedStatement = connection.prepareStatement(
                    "SELECT FROM_USER_PASSWORD FROM NPS_EMAIL_INFO")) {
                ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    password = resultSet.getString("FROM_USER_PASSWORD");
                }
            } catch (SQLException e) {
                logger.error("Failure during SQL query - getting password info", e);
            }

            return Encryption.decryptString(password);
        }

        // Method to get email date from email info table
        public static String getEmailDateInfo() {
            String emailDate = null;
            try (PreparedStatement preparedStatement = connection.prepareStatement(
                    "SELECT EMAIL_DATE FROM NPS_EMAIL_INFO")) {
                ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    emailDate = resultSet.getString("EMAIL_DATE");
                }
            } catch (SQLException e) {
                logger.error("Failure during SQL query - getting email data info", e);
            }
            return emailDate;
        }

        // Method to get all schedule entries for a specific week id
        public static ObservableList<Schedule> getScheduleData(String weekId) {
            ObservableList<Schedule> data = FXCollections.observableArrayList();

            try (PreparedStatement preparedStatement = connection.prepareStatement(
                    "SELECT * FROM NPS_SCHEDULE_" + weekId)) {
                ResultSet resultSet = preparedStatement.executeQuery();

                Map<String, Schedule> scheduleMap = new HashMap<>();

                while (resultSet.next()) {
                    String employeeId = resultSet.getString("EMPLOYEE_ID");

                    // Get employee first and last name
                    String firstName = "";
                    String lastName = "";
                    try (PreparedStatement preparedStatement2 = connection.prepareStatement(
                            "SELECT FIRST_NAME, LAST_NAME FROM NPS_EMPLOYEE WHERE ID = ?")) {
                        preparedStatement2.setString(1, employeeId);

                        ResultSet resultSet2 = preparedStatement2.executeQuery();
                        if (resultSet2.next()) {
                            firstName = resultSet2.getString("FIRST_NAME");
                            lastName = resultSet2.getString("LAST_NAME");
                        }
                    } catch (SQLException e) {
                        logger.error("Failure during SQL query - getting schedule data - getting employee name", e);
                    }

                    String key = employeeId; // Unique key for each employee
                    Schedule schedule = scheduleMap.getOrDefault(key, new Schedule(Encryption.decryptString(firstName)
                            + " " + Encryption.decryptString(lastName), employeeId));

                    // Set start and end times
                    schedule.setWeekID(weekId);
                    schedule.setMonday(resultSet.getString("MON_START_TIME"), resultSet.getString("MON_END_TIME"));
                    schedule.setTuesday(resultSet.getString("TUE_START_TIME"), resultSet.getString("TUE_END_TIME"));
                    schedule.setWednesday(resultSet.getString("WED_START_TIME"), resultSet.getString("WED_END_TIME"));
                    schedule.setThursday(resultSet.getString("THU_START_TIME"), resultSet.getString("THU_END_TIME"));
                    schedule.setFriday(resultSet.getString("FRI_START_TIME"), resultSet.getString("FRI_END_TIME"));
                    schedule.setSaturday(resultSet.getString("SAT_START_TIME"), resultSet.getString("SAT_END_TIME"));
                    schedule.setSunday(resultSet.getString("SUN_START_TIME"), resultSet.getString("SUN_END_TIME"));

                    scheduleMap.put(key, schedule);
                }

                data.addAll(scheduleMap.values());
            } catch (SQLException e) {
                logger.error("Failure during SQL query - getting schedule data ", e);
            }
            return data;
        }

        // Method to get only the contracted hours for an employee
        public static double getContractedHours(String employeeID) {
            double hours = -2.0;

            try (PreparedStatement preparedStatement = connection.prepareStatement(
                    "SELECT CONTRACT_HOURS FROM NPS_EMPLOYEE WHERE ID = ?")) {
                preparedStatement.setString(1, employeeID);

                ResultSet resultSet = preparedStatement.executeQuery();
                while (resultSet.next()) {
                    hours = resultSet.getDouble("CONTRACT_HOURS");
                }

            } catch (SQLException e) {
                logger.error("Failure during SQL query - getting contracted hours", e);
            }
            return hours;
        }

        // Methods to read from payroll table
        // Retrieve payroll overview data for a specific month
        public static ObservableList<PayrollOverview> getPayrollOverviewForMonth() {
            ObservableList<PayrollOverview> data = FXCollections.observableArrayList();

            try (PreparedStatement preparedStatement = connection.prepareStatement(
                    "SELECT PAY_MONTH, YEAR, PAY_DATE, " +
                            "       SUM(GROSS_PAY) AS TOTAL_AMOUNT, " +
                            "       COUNT(DISTINCT EMPLOYEE_ID) AS EMPLOYEE_COUNT " +
                            "FROM NPS_PAYROLL GROUP BY PAY_MONTH, YEAR, PAY_DATE")) {

                ResultSet resultSet = preparedStatement.executeQuery();

                while (resultSet.next()) {
                    String month = resultSet.getString("PAY_MONTH");
                    int year = resultSet.getInt("YEAR");
                    String payDate = resultSet.getString("PAY_DATE");
                    double totalAmount = resultSet.getDouble("TOTAL_AMOUNT");
                    int employeeCount = resultSet.getInt("EMPLOYEE_COUNT");

                    PayrollOverview payrollOverview = new PayrollOverview(month, year, payDate, totalAmount, employeeCount);
                    data.add(payrollOverview);
                }
            } catch (SQLException e) {
                logger.error("Failure during SQL query - getting payroll overview", e);
            }

            return data;
        }

        public static ObservableList<DetailedPayroll> getEmployeeDetailsForMonthAndYear(String selectedMonth, int selectedYear) {
            ObservableList<DetailedPayroll> data = FXCollections.observableArrayList();

            try (PreparedStatement preparedStatement = connection.prepareStatement(
                    "SELECT EMPLOYEE_ID, HOURS_WORKED, OVERTIME_HOURS, OVERTIME_PAY, TAXES, GROSS_PAY " +
                            "FROM NPS_PAYROLL WHERE PAY_MONTH = ? AND YEAR = ?")) {

                preparedStatement.setString(1, selectedMonth);
                preparedStatement.setInt(2, selectedYear);

                ResultSet resultSet = preparedStatement.executeQuery();

                while (resultSet.next()) {
                    // Get employee first and last name
                    DetailedPayroll employeeDetails = new DetailedPayroll();

                    String employeeId = resultSet.getString("EMPLOYEE_ID");

                    try (PreparedStatement preparedStatement2 = connection.prepareStatement(
                            "SELECT FIRST_NAME, LAST_NAME, SALARY FROM NPS_EMPLOYEE WHERE ID = ?")) {
                        preparedStatement2.setString(1, employeeId);

                        ResultSet resultSet2 = preparedStatement2.executeQuery();
                        if (resultSet2.next()) {
                            employeeDetails.setFirstName(Encryption.decryptString(resultSet2.getString("FIRST_NAME")));
                            employeeDetails.setLastName(Encryption.decryptString(resultSet2.getString("LAST_NAME")));
                            employeeDetails.setSalary(resultSet2.getInt("SALARY"));
                        }
                    } catch (SQLException e) {
                        logger.error("Failure during SQL query - getting employee details for month and year - " +
                                "employee info", e);
                    }

                    employeeDetails.setEmployeeID(employeeId);
                    employeeDetails.setHoursWorked(resultSet.getDouble("HOURS_WORKED"));
                    employeeDetails.setBasePay(resultSet.getDouble("GROSS_PAY"));
                    employeeDetails.setOvertimeHours(resultSet.getDouble("OVERTIME_HOURS"));
                    employeeDetails.setOvertimePay(resultSet.getDouble("OVERTIME_PAY"));
                    employeeDetails.setTaxPaid(resultSet.getDouble("TAXES"));

                    data.add(employeeDetails);
                }
            } catch (SQLException e) {
                logger.error("Failure during SQL query - getting employee details for month and year", e);
            }

            return data;
        }

        public static DetailedPayroll getSpecificEmployeePayroll(String employeeID) {
            DetailedPayroll employeeDetails = new DetailedPayroll();

            try (PreparedStatement preparedStatement = connection.prepareStatement(
                    "SELECT * FROM NPS_PAYROLL WHERE EMPLOYEE_ID = ?")) {
                preparedStatement.setString(1, employeeID);
                ResultSet resultSet = preparedStatement.executeQuery();

                while (resultSet.next()) {
                    try (PreparedStatement preparedStatement2 = connection.prepareStatement(
                            "SELECT FIRST_NAME, LAST_NAME, SALARY FROM NPS_EMPLOYEE WHERE ID = ?")) {
                        preparedStatement2.setString(1, employeeID);

                        ResultSet resultSet2 = preparedStatement2.executeQuery();
                        if (resultSet2.next()) {
                            employeeDetails.setFirstName(Encryption.decryptString(resultSet2.getString("FIRST_NAME")));
                            employeeDetails.setLastName(Encryption.decryptString(resultSet2.getString("LAST_NAME")));
                            employeeDetails.setSalary(resultSet2.getDouble("SALARY"));
                        }
                    } catch (SQLException e) {
                        logger.error("Failure during SQL query - getting employee details for month - " +
                                "employee info", e);
                    }

                    employeeDetails.setEmployeeID(employeeID);
                    employeeDetails.setPayDay(resultSet.getString("PAY_DATE"));
                    employeeDetails.setHoursWorked(resultSet.getDouble("HOURS_WORKED"));
                    employeeDetails.setPension(resultSet.getString("PENSION"));
                    employeeDetails.setPensionPaid(resultSet.getDouble("PENSION_PAID"));
                    employeeDetails.setBasePay(resultSet.getDouble("GROSS_PAY"));
                    employeeDetails.setOvertimeHours(resultSet.getDouble("OVERTIME_HOURS"));
                    employeeDetails.setOvertimePay(resultSet.getDouble("OVERTIME_PAY"));
                    employeeDetails.setTaxPaid(resultSet.getDouble("TAXES"));
                    employeeDetails.setNetPay(resultSet.getDouble("NET_PAY"));
                }

            } catch (SQLException e) {
                logger.error("Failure during SQL query - getting specific employee details for payroll", e);
            }

            return employeeDetails;
        }

        // Method to get the current logged in employee ID
        public static String getCurrentLoggedInEmployeeId() {
            return currentLoggedInEmployeeId;
        }

        public static ObservableList<DeletedUser> getAllDeletedUserInfo() {
            ObservableList<DeletedUser> deletedUsers = FXCollections.observableArrayList();

            try (PreparedStatement preparedStatement = connection.prepareStatement(
                    "SELECT * FROM NPS_DELETED_USERS")) {
                ResultSet resultSet = preparedStatement.executeQuery();

                while (resultSet.next()) {
                    String addedDate = resultSet.getString("ADDED_DATE");
                    String deleteDate = resultSet.getString("DELETE_DATE");
                    String deletedBy = resultSet.getString("DELETED_BY");
                    String firstName = resultSet.getString("FIRST_NAME");
                    String lastName = resultSet.getString("LAST_NAME");
                    String email = resultSet.getString("EMAIL");
                    String phone = resultSet.getString("PHONE");
                    String niNumber = resultSet.getString("NI_NUMBER");
                    String addressLine1 = resultSet.getString("ADDRESS_LINE_1");
                    String addressLine2 = resultSet.getString("ADDRESS_LINE_2");
                    String city = resultSet.getString("CITY");
                    String postcode = resultSet.getString("POSTCODE");
                    String bankName = resultSet.getString("BANK_NAME");
                    String accountNumber = resultSet.getString("ACCOUNT_NUMBER");
                    String sortCode = resultSet.getString("SORT_CODE");

                    DeletedUser deletedUser = new DeletedUser(Encryption.decryptString(addedDate),
                            Encryption.decryptString(deleteDate), Encryption.decryptString(deletedBy),
                            Encryption.decryptString(firstName), Encryption.decryptString(lastName),
                            Encryption.decryptString(email), Encryption.decryptString(phone),
                            Encryption.decryptString(niNumber), Encryption.decryptString(addressLine1),
                            Encryption.decryptString(addressLine2), Encryption.decryptString(city),
                            Encryption.decryptString(postcode), Encryption.decryptString(bankName),
                            Encryption.decryptString(accountNumber), Encryption.decryptString(sortCode));

                    deletedUsers.add(deletedUser);
                }
            } catch (SQLException e) {
                logger.error("Failure during SQL query - getting all deleted user data", e);
            }

            return deletedUsers;
        }
    }

    // ********************************************
    // *********** TABLE DELETE METHODS ***********
    // ********************************************

    public class DeleteTableData {
        // Method to delete an employee record
        public static void deleteEmployee(String employeeId, String deletedBy) {
            Person employee = DatabaseController.GetTableData.getEmployeeInfoByID(employeeId);

            // Check that you're not trying to delete the current logged-in user
            if (!Objects.equals(employeeId, currentLoggedInEmployeeId) && employee != null) {
                if (AddTableData.addEmployeeToDeletedUsersTable(deletedBy, employee.getFirstName(), employee.getLastName(),
                        employee.getEmail(), employee.getPhone(), employee.getNiNumber(), employee.getAddress1(),
                        employee.getAddress2(), employee.getCity(), employee.getPostcode(), employee.getBankName(),
                        employee.getAccountNumber(), employee.getSortCode())) {

                    // Remove the employees data from the other tables
                    deleteFromTable("NPS_LOGIN", employeeId);
                    deleteFromTable("NPS_BANK_DETAILS", employeeId);
                    deleteFromTable("NPS_EMERGENCY_CONTACT", employeeId);
                    deleteFromTable("NPS_ADDRESSES", employeeId);
                    deleteFromTable("NPS_SCHEDULE_0", employeeId);
                    deleteFromTable("NPS_SCHEDULE_1", employeeId);
                    deleteFromTable("NPS_SCHEDULE_2", employeeId);
                    deleteFromTable("NPS_SCHEDULE_3", employeeId);
                    deleteEmployee(employeeId);
                }

            } else {
                if (employee == null) {
                    logger.error("Employee is null.");
                } else {
                    logger.error("You cannot delete the currently logged-in user.");
                }
            }
        }

        private static void deleteFromTable(String tableName, String employeeId) {
            try (PreparedStatement preparedStatement = connection.prepareStatement(
                    "DELETE FROM " + tableName + " WHERE EMPLOYEE_ID = ?")) {
                preparedStatement.setString(1, employeeId);

                int rowsAffected = preparedStatement.executeUpdate();
                if (rowsAffected > 0) {
                    logger.info( "record deleted successfully for " + tableName);
                } else {
                    logger.info("No record found with the given ID in " + tableName);
                }
            } catch (SQLException e) {
                logger.error("Failure during SQL query - deleting table - " + tableName, e);
            }
        }

        private static void deleteEmployee(String ID) {
            try (PreparedStatement preparedStatement = connection.prepareStatement(
                    "DELETE FROM NPS_EMPLOYEE WHERE ID = ?")) {
                preparedStatement.setString(1, ID);

                int rowsAffected = preparedStatement.executeUpdate();
                if (rowsAffected > 0) {
                    logger.info("Schedule entries deleted successfully.");
                } else {
                    logger.info("No schedule entries found for the given employee ID.");
                }
            } catch (SQLException e) {
                logger.error("Failure during SQL query - deleting schedule data for employee", e);
            }
        }

        // Method that will perm delete employee data - removes it from the deleted users table - NPS_DELETED_USERS
        public static void removeUserData(String email, String deletedBy) {
            try (PreparedStatement preparedStatement = connection.prepareStatement(
                    "DELETE FROM NPS_DELETED_USERS WHERE EMAIL = ? AND DELETED_BY = ?")) {
                preparedStatement.setString(1, Encryption.encryptString(email));
                preparedStatement.setString(2, Encryption.encryptString(deletedBy));

                int rowsAffected = preparedStatement.executeUpdate();
                if (rowsAffected > 0) {
                    logger.info("Schedule entries deleted successfully.");
                } else {
                    logger.info("No schedule entries found for the given employee ID.");
                }
            } catch (SQLException e) {
                logger.error("Failure during SQL query - deleting schedule data for employee", e);
            }
        }

        // Method to delete a help record
        public static void deleteHelp(String errorCode) {
            try (PreparedStatement preparedStatement = connection.prepareStatement(
                    "DELETE FROM NPS_HELP_INFO WHERE ERROR_CODE = ?")) {
                preparedStatement.setString(1, errorCode);

                int rowsAffected = preparedStatement.executeUpdate();
                if (rowsAffected > 0) {
                    logger.info("Help record deleted successfully.");
                } else {
                    logger.info("No help record found with the given error code.");
                }
            } catch (SQLException e) {
                logger.error("Failure during SQL query - deleting help by error code", e);
            }
        }
    }

    // ********************************************
    // *************** MISC METHODS ***************
    // ********************************************

    public class InitialiseTables {
        // Ensure that every employee has a schedule entry for every day of the week for every week
        public static void initialiseScheduleForAllEmployees() {
            // Get all employee IDs
            ObservableList<Person> employees = GetTableData.getAllEmployees();

            for (int weekID = 0; weekID <= 3; weekID++) {
                for (Person employee : employees) {
                    String employeeId = employee.getEmployeeID();

                    // Check if a record already exists for the employee and day
                    if (!CheckTableData.scheduleRecordExists(employeeId, String.valueOf(weekID))) {
                        // If no record exists, add a default record (empty start and end time)
                        Schedule defaultSchedule = new Schedule(employee.getFirstName() + " " + employee.getLastName(),
                                employeeId);
                        defaultSchedule.setWeekID(String.valueOf(weekID));
                        UpdateTableData.updateSchedule(defaultSchedule);
                    }
                }
            }
        }
    }

    class Encryption {
        private static final String ALGORITHM = "AES";
        private static final String SECRET_KEY = "1vFk4S1Q1cZalOES9J6hAt6w"; // 16, 24, or 32 bytes

        public static String encryptString(String data) {
            try {
                if (data == null) {
                    return null;
                }
                Key key = new SecretKeySpec(SECRET_KEY.getBytes(), ALGORITHM);
                Cipher cipher = Cipher.getInstance(ALGORITHM);
                cipher.init(Cipher.ENCRYPT_MODE, key);
                byte[] encryptedBytes = cipher.doFinal(data.getBytes());
                return Base64.getEncoder().encodeToString(encryptedBytes);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        public static String decryptString(String encryptedData) {
            if (encryptedData == null) {
                return null;
            }
            try {
                Key key = new SecretKeySpec(SECRET_KEY.getBytes(), ALGORITHM);
                Cipher cipher = Cipher.getInstance(ALGORITHM);
                cipher.init(Cipher.DECRYPT_MODE, key);
                byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedData));
                return new String(decryptedBytes);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        public static String hash(String plaintext) {
            try {
                MessageDigest digest = MessageDigest.getInstance("SHA-256");
                byte[] hashBytes = digest.digest(plaintext.getBytes());
                StringBuilder hexString = new StringBuilder();
                for (byte hashByte : hashBytes) {
                    String hex = Integer.toHexString(0xff & hashByte);
                    if (hex.length() == 1) hexString.append('0');
                    hexString.append(hex);
                }
                return hexString.toString();
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            }
        }
    }

    class MiscMethod {
        private static String getFirstThreeLetter(String input) {
            String firstThreeLetters = input.substring(0, 3);
            return firstThreeLetters.toUpperCase();
        }

        private static String getCurrentMonthString() {
            // Get the current date
            LocalDate currentDate = LocalDate.now();

            // Format the current date to get the month name
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM", Locale.ENGLISH);

            return currentDate.format(formatter);
        }

        private static int getCurrentYear() {
            // Get the current year
            Year currentYear = Year.now();
            return currentYear.getValue();
        }

        public static String toLowerCase(String input) {
            if (input == null) {
                return null;  // Handle null input gracefully
            }
            return input.toLowerCase();
        }
    }
}