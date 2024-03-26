package application.email;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;

import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.Date;

import application.DatabaseController;
import application.payroll.DetailedPayroll;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.encryption.AccessPermission;
import org.apache.pdfbox.pdmodel.encryption.StandardProtectionPolicy;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

public class PDFBox {
    public static String createPDF(String employeeNumber, String niNo) throws IOException {

        String savePath = "src/main/resources/PDF/";

        // Create the path to save the PDF
        checkDirectory(savePath);

        // Check if employee directory exists
        String employeeDirectory = savePath + "/" + employeeNumber;
        checkDirectory(employeeDirectory);

        // Check if the employee's payroll directory exists for each month
        String[] monthAndYear = getMonthAndYear();
        String employeeMonthDirectory = employeeDirectory + "/" + monthAndYear[0] + "_" + monthAndYear[1];
        //checkDirectory(employeeMonthDirectory);

        // Create the PDF
        PDDocument doc = new PDDocument();
        PDPage firstPage = new PDPage();
        doc.addPage(firstPage);

        // Get page width and height
        int pageWidth = (int) firstPage.getTrimBox().getWidth();
        int pageHeight = (int) firstPage.getTrimBox().getHeight();

        // Create the content stream
        PDPageContentStream contentStream = new PDPageContentStream(doc, firstPage);

        // Create the text class
        textClass text = new textClass(doc, contentStream);

        // Set the font
        PDFont font = PDType1Font.HELVETICA;
        PDFont italicFont = PDType1Font.HELVETICA_OBLIQUE;

        // ****************Employee details***************
        DetailedPayroll employee = DatabaseController.getSpecificEmployeePayroll(employeeNumber); // Gets the employee info from the database using the employee ID
        String employeeID = employee.getEmployeeID();
        String name = employee.getFirstName() + " " + employee.getLastName();
        String hoursWorked = String.valueOf(employee.getHoursWorked());
        String hourlyRate = String.valueOf(employee.getsalary());
        String overTimeWorked = String.valueOf(employee.getOvertimeHours());
        String overTimeRate = String.valueOf(employee.getOvertimePay());
        String overAllPay = String.valueOf(employee.getBasePay());
        String tax = String.valueOf(employee.getTaxPaid());
        String payAfterSacrifice = String.valueOf(employee.getNetPay());
        // ****************Employee details***************


        // Add the date at the top right of the page
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        Date date = new Date();
        String dateString = formatter.format(date);
        textClass.addSingleLineText(dateString, pageWidth - 100, pageHeight - 25, font, 15);

        // Add the employee ID and name at the top left of the page
        String[] employeeDetails = {"Employee ID: " + employeeID, "Name: " + name};
        textClass.addSingleLineText(employeeDetails[0], 25, pageHeight - 25, font, 15);
        textClass.addSingleLineText(employeeDetails[1], 25, pageHeight - 50, font, 15);

        // Add table for employee pay details
        // Title for the table
        textClass.addSingleLineText("Pay Details", 25, pageHeight - 115, font, 15);
        // Set the table
        int[] columnWidths = {115, 115, 115, 115, 115};
        tableClass table = new tableClass(doc, contentStream, columnWidths, 25, pageHeight - 145, 20, 0, 20, 15, font);
        // Add the table headers
        String[] tableHeaders = {"Hours", "Hourly Rate", "Overtime", "Overtime Rate", "Overall Pay"};
        for (String header : tableHeaders) {
            table.addCell(header);
        }
        // Add the table data
        String[] tableData = {hoursWorked, hourlyRate, overTimeWorked, overTimeRate, overAllPay};
        for (String data : tableData) {
            table.addCell(data);
        }

        // Add table for employee salary sacrifice details - tax, pension, etc.
        // Title for the table
        textClass.addSingleLineText("Salary Sacrifice Details", 25, pageHeight - 215, font, 15);
        // Set the table
        int[] columnWidths2 = {115, 115, 115, 115, 115};
        tableClass table2 = new tableClass(doc, contentStream, columnWidths2, 25, pageHeight - 245, 20, 0, 20, 15, font);
        // Add the table headers
        String[] tableHeaders2 = {"Tax", "Pension", "Other", "Other", "Pay"};
        for (String header : tableHeaders2) {
            table2.addCell(header);
        }
        // Add the table data
        String[] tableData2 = {tax, "0", "0", "0", payAfterSacrifice};
        for (String data : tableData2) {
            table2.addCell(data);
        }

        // Close the content stream
        contentStream.close();
        String pdfFilePath = employeeMonthDirectory + ".pdf";
        //doc.save(pdfFilePath);

        // Encrypt the PDF
        String lastFourDigits = niNo.substring(niNo.length() - 4);
        encryptPDF(doc, pdfFilePath, lastFourDigits);

        System.out.println("PDF created: " + pdfFilePath);
        doc.close();

        return pdfFilePath;
    }

    private static class textClass {
        PDDocument doc;
        static PDPageContentStream contentStream;

        public textClass(PDDocument doc, PDPageContentStream contentStream) {
            this.doc = doc;
            this.contentStream = contentStream;
        }

        static void addSingleLineText(String text, int x, int y, PDFont font, float fontSize) throws IOException {
            contentStream.beginText();
            contentStream.setFont(font, fontSize);
            contentStream.setNonStrokingColor(0,0,0);
            contentStream.newLineAtOffset(x, y);
            contentStream.showText(text);
            contentStream.endText();
            contentStream.moveTo(x, y);
        }

//        static void addMultipleLineText(String[] textArray, float leading, int x, int y, PDFont font, float fontSize) throws IOException {
//            contentStream.beginText();
//            contentStream.setFont(font, fontSize);
//            contentStream.setNonStrokingColor(0,0,0);
//            contentStream.newLineAtOffset(x, y);
//            for (String text : textArray) {
//                contentStream.showText(text);
//                contentStream.newLine();
//            }
//            contentStream.endText();
//            contentStream.moveTo(x, y);
//        }

        float getTextWidth(String text, PDFont font, float fontSize) throws IOException {
            return font.getStringWidth(text) / 1000 * fontSize;
        }
    }

    private static class tableClass {
        PDDocument doc;
        PDPageContentStream contentStream;
        private int[] columnWidths;
        private int cellHeights;
        private int yPos;
        private int xPos;
        private int colPos = 0;
        private int xInitialPos;
        private float fontSize;
        private PDFont font;

        public tableClass(PDDocument doc, PDPageContentStream contentStream, int[] columnWidths, int cellHeights, int yPos, int xPos, int colPos, int xInitialPos, float fontSize, PDFont font) {
            this.doc = doc;
            this.contentStream = contentStream;
            this.columnWidths = columnWidths;
            this.cellHeights = cellHeights;
            this.yPos = yPos;
            this.xPos = xPos;
            this.colPos = colPos;
            this.xInitialPos = xInitialPos;
            this.fontSize = fontSize;
            this.font = font;
        }

        void setTable(int[] columnWidths, int cellHeights, int xPos, int yPos) {
            this.columnWidths = columnWidths;
            this.cellHeights = cellHeights;
            this.xPos = xPos;
            this.yPos = yPos;
            xInitialPos = xPos;
        }

        void setTableFont(float fontSize, PDFont font) {
            this.fontSize = fontSize;
            this.font = font;
        }

        void addCell(String text) throws IOException {
            contentStream.setStrokingColor(1f);

            // Set the colour of the cell
            contentStream.setNonStrokingColor(0,0,0);
            contentStream.setStrokingColor(0,0,0);

            // Draw the cell
            contentStream.addRect(xPos, yPos, columnWidths[colPos], cellHeights);

            // Fill the cell
            contentStream.stroke();


            // Add the text to the cell
            contentStream.beginText();
            contentStream.setNonStrokingColor(0,0,0);

            // Set the position of the text - if the column is the last or second last, align the text to the right
//            if (colPos == 4 || colPos == 2) {
//                float fontWidth = font.getStringWidth(text) / 1000 * fontSize;
//                contentStream.newLineAtOffset(xPos + columnWidths[colPos]-20-fontWidth, yPos + 10);
//            } else {
//                contentStream.newLineAtOffset(xPos + 20, yPos + 10);
//            }

            contentStream.newLineAtOffset(xPos + 5, yPos + 8);

            // Add the text
            contentStream.showText(text);
            contentStream.endText();

            xPos = xPos + columnWidths[colPos];
            colPos++;

            // If the column position is the same as the number of columns, reset the column position and move to the next row
            if (colPos == columnWidths.length) {
                colPos = 0;
                xPos = xInitialPos;
                yPos = yPos - cellHeights;
            }
        }
    }

    // Method to encrypt the PDF with a password
    private static void encryptPDF(PDDocument doc, String filePath, String password) throws IOException {
        // Set up encryption options
        AccessPermission ap = new AccessPermission();
        ap.setCanPrint(true);

        StandardProtectionPolicy spp = new StandardProtectionPolicy(password, password, ap);
        spp.setEncryptionKeyLength(128);

        // Apply encryption to the document
        doc.protect(spp);

        // Set additional document information
        PDDocumentInformation info = doc.getDocumentInformation();
        info.setAuthor("Payroll System");
        info.setTitle("Payslip");

        // Save the encrypted document
        doc.save(filePath);
    }

    // Method to check if the directories exist to save the PDF
    // directories are unique to each employee by using their email
    // and the current month and year
    private static void checkDirectory(String directory) {
        // Check if the directory exists
        Path path = Paths.get(directory);
        // If the directory doesn't exist, create it
        if (Files.exists(path) && Files.isDirectory(path))
        {
            System.out.println("Directory already exists");
        }
        else {
            try {
                Files.createDirectories(path);
            } catch (FileAlreadyExistsException e) {
                System.out.println("Directory already exists");
            } catch (IOException e) {
                System.out.println("An error occurred");
                e.printStackTrace();
            }
        }
    }

    // Method to get the current month and year
    private static String[] getMonthAndYear() {
        // Get the current month and year
        // Get the current date
        LocalDate currentDate = LocalDate.now();

        // Extract the current month and year
        String currentMonth = currentDate.getMonth().toString();
        int currentYear = currentDate.getYear();

        String[] monthAndYear = new String[2];
        monthAndYear[0] = currentMonth.toLowerCase();
        monthAndYear[1] = Integer.toString(currentYear);

        return monthAndYear;
    }
}
