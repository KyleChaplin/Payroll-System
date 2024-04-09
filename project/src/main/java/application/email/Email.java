package application.email;

import application.DatabaseController;
import application.employees.Person;
import javafx.scene.chart.PieChart;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Email {
    private static MimeMessage[] mimeMessage = null;

    public static void sendEmailTask() {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

        // Schedule the task to run periodically every month
        scheduler.scheduleAtFixedRate(new EmailTask(), 0, 30, TimeUnit.DAYS);
    }

    static class EmailTask implements Runnable {
        private static Session newSession;

        @Override
        public void run() {
            try {
                // Fetch the next scheduled time from the database
                String currentEmailDate = DatabaseController.getEmailDateInfo();

                // Parse the retrieved date and time
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                Date scheduledDate = sdf.parse(currentEmailDate);

                // Check if the scheduled date has passed
                if (scheduledDate.before(new Date())) {
                    // Setup mail server properties
                    setupServerProperties();

                    // Draft an email
                    draftEmail();

                    // Send the email
                    sendEmail();

                    updateEmailDateInDatabase();
                }
            } catch (MessagingException | IOException | ParseException e) {
                throw new RuntimeException(e);
            }
        }

        private void updateEmailDateInDatabase() {
            System.out.println("Updating database...");

            try {
                // Update the email date in the database
                String newEmailDate = calculateNextExecutionDate();
                DatabaseController.updateEmailDate(newEmailDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        private String calculateNextExecutionDate() throws ParseException {
            // Fetch the next scheduled time from the database
            String currentEmailDate = DatabaseController.getEmailDateInfo();

            // Parse the retrieved date and time
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            Date scheduledDate = sdf.parse(currentEmailDate);

            // Calculate the next scheduled time (e.g., add one month to the retrieved date)
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(scheduledDate);
            calendar.add(Calendar.MONTH, 1);

            return sdf.format(calendar.getTime());
        }

        private static void sendEmail() throws MessagingException {
            System.out.println("Sending emails...");

            String fromUser = DatabaseController.getEmailInfo();
            String fromUserPassword = DatabaseController.getPasswordInfo();
            String emailHost = "smtp.gmail.com";
            Transport transport = newSession.getTransport("smtp");
            transport.connect(emailHost, fromUser, fromUserPassword);

            for (MimeMessage message : mimeMessage) {
                transport.sendMessage(message, message.getAllRecipients());
            }

            transport.close();

            // Output message to test
            System.out.println("Email sent successfully");
        }

        private static void draftEmail() throws MessagingException, IOException {
            System.out.println("Drafting emails...");

            String emailSubject;
            String emailBody;

            List<Person> employees = DatabaseController.getAllEmployeeInfo();
            mimeMessage = new MimeMessage[employees.size()];
            Calendar currentCalendar = Calendar.getInstance();

            for (int i = 0; i < employees.size(); i++) {
                Person person = employees.get(i);

                SimpleDateFormat monthFormat = new SimpleDateFormat("MMMM", java.util.Locale.getDefault());
                String currentMonth = monthFormat.format(currentCalendar.getTime());

                emailSubject = "Payslip for " + currentMonth;
                emailBody = "Dear, " + person.getFirstName() + " " + person.getLastName() +
                        "<br><br>Please find attached your payslip for the month of " + currentMonth + ".<br><br>" +
                        "Kind regards,<br>" +
                        "Payroll Team";

                mimeMessage[i] = new MimeMessage(newSession);
                mimeMessage[i].addRecipient(Message.RecipientType.TO, new InternetAddress(person.getEmail()));
                mimeMessage[i].setSubject(emailSubject);

                MimeBodyPart pdfAttachment = new MimeBodyPart();
                String source = PDFBox.createPDF(person.getEmployeeID(), person.getNiNumber());
                pdfAttachment.attachFile(source);

                MimeBodyPart bodyPart = new MimeBodyPart();
                bodyPart.setContent(emailBody, "text/html");
                MimeMultipart multipart = new MimeMultipart();
                multipart.addBodyPart(bodyPart);
                multipart.addBodyPart(pdfAttachment);

                mimeMessage[i].setContent(multipart);
            }
        }

        private static void setupServerProperties() {
            System.out.println("Setting up email server properties...");

            Properties properties = System.getProperties();
            properties.put("mail.smtp.host", "smtp.gmail.com");
            properties.put("mail.smtp.port", "587");
            properties.put("mail.smtp.auth", "true");
            properties.put("mail.smtp.starttls.enable", "true");

            newSession = Session.getDefaultInstance(properties, null);
        }
    }
}
