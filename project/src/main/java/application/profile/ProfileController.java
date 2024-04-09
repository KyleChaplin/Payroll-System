package application.profile;

import application.DatabaseController;
import application.SceneController;
import application.ThemeManager;
import application.employees.Person;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.format.TextStyle;
import java.time.LocalDate;
import java.util.Locale;
import java.util.Objects;
import java.util.ResourceBundle;
import java.nio.file.Path;
import java.nio.file.Paths;

import static application.DatabaseController.getEmployeeInfo;

public class ProfileController implements Initializable {

    private Stage stage;
    private Scene scene;

    @FXML
    private Label lblID;
    @FXML
    private Label lblName;
    @FXML
    private TextField txtEmail;
    @FXML
    private TextField txtNiNumber;
    @FXML
    private TextField txtPhone;
    @FXML
    private TextField txtfName;
    @FXML
    private TextField txtlName;
    @FXML
    private TextField txtSalaryHourly;
    @FXML
    private ComboBox cboPension;
    @FXML
    private TextField txtBasePay;
    @FXML
    private TextField txtBankName;
    @FXML
    private TextField txtAccountNumber;
    @FXML
    private TextField txtSortCode;
    @FXML
    private TextField txtJobTitle;
    @FXML
    private TextField txtDepartment;
    @FXML
    private TextField txtContractType;
    @FXML
    private TextField txtContractHours;
    @FXML
    private TextField txtLocation;
    @FXML
    private TextField txtAddress1;
    @FXML
    private TextField txtAddress2;
    @FXML
    private TextField txtCity;
    @FXML
    private TextField txtPostcode;
    @FXML
    private TextField txtEFirstName;
    @FXML
    private TextField txtELastName;
    @FXML
    private TextField txtERelationship;
    @FXML
    private TextField txtEMobile;
    @FXML
    private Label txtEmptyError;
    @FXML
    private Button btnGreen; // This button will either be "Edit" or "Update"
    @FXML
    private Button btnAdmin;
    @FXML
    private VBox vBoxDownload;
    @FXML
    private AnchorPane anchorPane;

    private String id;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        if (Integer.parseInt(DatabaseController.getAccessLevel(DatabaseController.getEmailById(DatabaseController.getCurrentLoggedInEmployeeId()))) == 0) {
            btnAdmin.setVisible(true);
        }

        // Populate the text fields with the current user's information
        Person person = getEmployeeInfo();

        lblName.setText("Employee: " + person.getFirstName() + " " + person.getLastName());
        id = person.getEmployeeID();
        lblID.setText("ID: #" + id);

        // Populate the text fields with the current user's information
        txtfName.setText(person.getFirstName());
        txtlName.setText(person.getLastName());
        txtEmail.setText(person.getEmail());
        txtPhone.setText(person.getPhone());
        txtNiNumber.setText(person.getNiNumber());

        // Populate the text fields with the payroll information
        txtSalaryHourly.setText(String.valueOf(person.getHourlySalary()));
        cboPension.setValue(person.getPension());

        cboPension.getItems().addAll(
                "0%",
                "5%",
                "10%"
        );

        txtBankName.setText(person.getBankName());
        txtBasePay.setText(String.valueOf(((Double.parseDouble(person.getContractedHours()) * Double.parseDouble(person.getHourlySalary()) * 4) * 12)));
        txtAccountNumber.setText(person.getAccountNumber());
        txtSortCode.setText(person.getSortCode());

        // Populate the text fields with the job information
        txtJobTitle.setText(person.getJobTitle());
        txtDepartment.setText(person.getDepartment());
        txtContractType.setText(person.getContractType());
        txtContractHours.setText(person.getContractedHours());
        txtLocation.setText(person.getLocation());

        // Populate the text fields with the address information
        txtAddress1.setText(person.getAddress1());
        txtAddress2.setText(person.getAddress2());
        txtCity.setText(person.getCity());
        txtPostcode.setText(person.getPostcode());

        // Populate the text fields with the emergency contact information
        txtEFirstName.setText(person.getEFirstName());
        txtELastName.setText(person.getELastName());
        txtEMobile.setText(person.getEMobile());
        txtERelationship.setText(person.getERelationship());

        // Generate download links for payroll
        generateDownloadLinks();

        // Initialize the ComboBox as non-editable
        cboPension.setEditable(false);
        cboPension.setFocusTraversable(false);
        cboPension.setMouseTransparent(true);
    }

    // Method to generate download links for payroll PDFs
    public void generateDownloadLinks() {
        // Get the past 6 months and years
        String[][] pastSixMonths = getPastSixMonths();

        // Directory
        String savePath = "src/main/resources/PDF/";
        String employeeDirectory = savePath + "/" + DatabaseController.getCurrentLoggedInEmployeeId();

        // Iterate over the past 6 months
        for (String[] monthAndYear : pastSixMonths) {
            // Extract month and year
            String month = monthAndYear[0];
            String year = monthAndYear[1];

            // Construct the PDF path
            String pdfPath = employeeDirectory + "/" + month + "_" + year + ".pdf";

            // Check if the PDF file exists
            Path path = Paths.get(pdfPath);
            if (Files.exists(path)) {
                // Create a download link for the PDF
                Hyperlink downloadLink = new Hyperlink(month + " " + year);
                downloadLink.setOnAction(this::handleDownload);

                // Add the download link to the VBox
                vBoxDownload.getChildren().add(downloadLink);
            }
        }

        if (vBoxDownload.getChildren().isEmpty()) {
            Label label = new Label("No payslips available");
            vBoxDownload.getChildren().add(label);
        }
    }

    // Method to handle download action when a download link is clicked
    private void handleDownload(ActionEvent event) {
        // Get the clicked hyperlink
        Hyperlink clickedLink = (Hyperlink) event.getSource();

        // Extract month and year from the hyperlink text
        String text = clickedLink.getText();
        String[] parts = text.split(" ");
        String month = parts[0];
        String year = parts[1];

        // Construct the PDF path
        String savePath = "src/main/resources/PDF/";
        String employeeDirectory = savePath + "/" + DatabaseController.getCurrentLoggedInEmployeeId();
        String pdfPath = employeeDirectory + "/" + month + "_" + year + ".pdf";

        // Prompt the user to select a download location
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select Download Location");

        File selectedDirectory = directoryChooser.showDialog(stage);

        // If a valid directory is selected
        if (selectedDirectory != null) {
            // Construct the destination file path
            String destinationPath = selectedDirectory.getAbsolutePath() + File.separator + month + "_" + year + ".pdf";

            // Copy the PDF file to the selected directory
            try {
                Files.copy(Paths.get(pdfPath), Paths.get(destinationPath), StandardCopyOption.REPLACE_EXISTING);
                System.out.println("File downloaded successfully to: " + destinationPath);
            } catch (Exception e) {
                System.out.println("Error downloading file: " + e.getMessage());
            }
        }
    }

    // Method to get the past 6 months
    private static String[][] getPastSixMonths() {
        String[][] pastSixMonths = new String[6][2];

        // Get the current date
        LocalDate currentDate = LocalDate.now();

        // Iterate for the past 6 months
        for (int i = 0; i < 6; i++) {
            // Subtract i months from the current date
            LocalDate pastDate = currentDate.minusMonths(i);

            // Extract the month and year
            String month = pastDate.getMonth().getDisplayName(TextStyle.FULL, Locale.getDefault()).toLowerCase();
            int year = pastDate.getYear();

            pastSixMonths[i][0] = month;
            pastSixMonths[i][1] = Integer.toString(year);
        }

        return pastSixMonths;
    }

    public void btnToggleUpdate() {
        // Check if the text fields are editable
        if (Objects.equals(btnGreen.getText(), "Save")) {

            TextField[] textFields = {
                    txtfName, txtlName, txtEFirstName, txtELastName, txtEmail, txtPhone, txtNiNumber,
                    txtAddress1, txtAddress2, txtCity, txtPostcode, txtBankName,
                    txtAccountNumber, txtSortCode, txtEMobile, txtERelationship
            };

            boolean hasEmptyField = false;

            // Remove highlight to every textfield
            for (TextField textField : textFields) {
                SceneController.removeErrorHighlight(textField);
                SceneController.removeEditableHighlight(textField);
            }

            for (TextField textField : textFields) {
                if (textField.getText().isEmpty()) {
                    hasEmptyField = true;
                    SceneController.highlightError(textField); // Highlight only the empty fields
                }
            }

            if (hasEmptyField) {
                txtEmptyError.setText("Fields should not be empty!");
            } else {
                // Perform additional input validation
                if (!txtEmail.getText().matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")) {
                    txtEmptyError.setText("Invalid email address!");
                    SceneController.highlightError(txtEmail);
                    return;
                }

                if (!txtPhone.getText().matches("^[0-9]{11}$")) {
                    txtEmptyError.setText("Invalid mobile number!");
                    SceneController.highlightError(txtPhone);
                    return;
                }

                if (!txtNiNumber.getText().matches("^[A-CEGHJ-PR-TW-Z]{1}[A-CEGHJ-NPR-TW-Z]{1}[0-9]{6}[A-D\\s]{1}$")) {
                    txtEmptyError.setText("Invalid NI number!");
                    SceneController.highlightError(txtNiNumber);
                    return;
                }

                if (!txtAccountNumber.getText().matches("^[0-9]{8}$")) {
                    txtEmptyError.setText("Invalid account number!");
                    SceneController.highlightError(txtAccountNumber);
                    return;
                }

                if (!txtSortCode.getText().matches("^[0-9]{6}$")) {
                    txtEmptyError.setText("Invalid sort code!");
                    SceneController.highlightError(txtSortCode);
                    return;
                }

                if (!txtPostcode.getText().matches("^([A-Z]{1,2}[0-9R][0-9A-Z]? [0-9][ABD-HJLNP-UW-Z]{2})$")) {
                    txtEmptyError.setText("Invalid postal code!");
                    SceneController.highlightError(txtPostcode);
                    return;
                }

                if (!txtEMobile.getText().matches("^[0-9]{11}$")) {
                    txtEmptyError.setText("Invalid emergency contact mobile number!");
                    SceneController.highlightError(txtEMobile);
                    return;
                }

                // Once validation is passed, update the employee profile
                DatabaseController.updateEmployeeProfile(id, txtlName.getText(), txtlName.getText(),
                        txtEmail.getText(), txtPhone.getText(), txtNiNumber.getText(), txtAddress1.getText(),
                        txtAddress2.getText(), txtPostcode.getText(), txtCity.getText(), txtBankName.getText(),
                        txtAccountNumber.getText(), txtSortCode.getText(), txtEFirstName.getText(),
                        txtELastName.getText(), txtEMobile.getText(), txtERelationship.getText(),
                        cboPension.getValue().toString());

                // Clear the error message
                txtEmptyError.setText("");

                // Toggle the text fields to be editable or not
                setEditable();

                btnGreen.setText("Edit");
            }
        } else {
            setEditable();

            TextField[] textFields = {
                    txtfName, txtlName, txtEFirstName, txtELastName, txtEmail, txtPhone, txtNiNumber,
                    txtAddress1, txtAddress2, txtCity, txtPostcode, txtBankName,
                    txtAccountNumber, txtSortCode, txtEMobile, txtERelationship
            };

            for (TextField textField : textFields) {
                SceneController.highlightEditable(textField);
            }

            btnGreen.setText("Save");
        }
    }

    private void setEditable() {
        TextField[] textFields = {
                txtfName, txtlName, txtEFirstName, txtELastName, txtEmail, txtPhone, txtNiNumber,
                txtAddress1, txtAddress2, txtCity, txtPostcode, txtBankName,
                txtAccountNumber, txtSortCode, txtEMobile, txtERelationship
        };

        for (TextField textField : textFields) {
            textField.setEditable(!textField.isEditable());
        }

        // Toggle the editability of the ComboBox
        cboPension.setEditable(!cboPension.isEditable());
        cboPension.setMouseTransparent(!cboPension.isEditable());
    }

    public void openDashboard(ActionEvent event) throws IOException {
        SceneController.openScene(event, "home", stage, scene);
    }

    public void openSchedule(ActionEvent event) throws IOException {
        SceneController.openScene(event, "schedule", stage, scene);
    }

    public void openPayroll(ActionEvent event) throws IOException {
        SceneController.openScene(event, "payroll", stage, scene);
    }

    public void openPeople(ActionEvent event) throws IOException {
        SceneController.openScene(event, "people", stage, scene);
    }

    public void openProfile(ActionEvent event) throws IOException {
        SceneController.openScene(event, "profile", stage, scene);
    }

    public void openAdmin(ActionEvent event) throws IOException {
        SceneController.openScene(event, "admin", stage, scene);
    }

    public void openHelp(ActionEvent event) throws IOException {
        SceneController.openScene(event, "help", stage, scene);
    }

    public void toggleTheme() throws IOException {
        ThemeManager.toggleMode();
    }
}
