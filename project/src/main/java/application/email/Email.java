package application.email;

import application.DatabaseController;
import application.employees.Person;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class Email {
    static Session newSession = null;
    static MimeMessage[] mimeMessage = null;

    public static void sendEmailTask() {
        Timer timer = new Timer();
        timer.schedule(new EmailTask(), getNextExecutionTime());
    }

    private static Date getNextExecutionTime() {
        // Set the desired time of the month for sending the email
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        String scheduledDateStr = DatabaseController.getEmailDateInfo();// "26-01-2024 15:11:00";
        try {
            Date scheduledDate = sdf.parse(scheduledDateStr);
            Date currentDate = new Date();

            if (scheduledDate.before(currentDate)) {
                // Increment month if the scheduled date has passed
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(scheduledDate);
                calendar.add(Calendar.MONTH, 1);
                scheduledDate = calendar.getTime();
            }

            return scheduledDate;
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    static class EmailTask extends TimerTask {
        @Override
        public void run() {
            // Setup mail server properties
            // Draft an email
            // Send the email
            try {
                setupServerProperties();
                draftEmail();
                sendEmail();
            } catch (MessagingException | IOException e) {
                throw new RuntimeException(e);
            }
        }

        private void sendEmail() throws MessagingException {
            String fromUser = DatabaseController.getEmailInfo();   // TODO: get the from user from the database
            String fromUserPassword = DatabaseController.getPasswordInfo();   // TODO: get the from user password from the database
            String emailHost = "smtp.gmail.com";
            Transport transport = newSession.getTransport("smtp");
            transport.connect(emailHost, fromUser, fromUserPassword);

            for (int i = 0; i < mimeMessage.length; i++)
            {
                transport.sendMessage(mimeMessage[i], mimeMessage[i].getAllRecipients());
            }
            //transport.sendMessage(mimeMessage, mimeMessage.getAllRecipients());
            transport.close();

            // Output message to test
            System.out.println("Email sent successfully");
        }

        private MimeMessage[] draftEmail() throws MessagingException, IOException {
            String emailSubject;    // TODO: Allow setting and get the email subject from the database - Maybe
            String emailBody; // TODO: Allow setting and get the email body from the database - Maybe

            Person[] person = new Person[]{DatabaseController.getEmployeeInfo()};
            for (int i = 0; i < person.length; i++) {
                emailSubject = "Payslip for " + Calendar.MONTH;
                emailBody = "Dear, " + person[i].getFirstName() + " " + person[i].getLastName() + "\n\n" +
                        "Please find attached your payslip for the month of " + Calendar.MONTH + ".\n\n" +
                        "Kind regards,\n" +
                        "Payroll Team";

                mimeMessage = new MimeMessage[]{new MimeMessage(newSession)};

//                for (int i = 0; i < emailRecipients.length; i++) {
//                    mimeMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(emailRecipients[i]));
//                }

                mimeMessage[i].addRecipient(Message.RecipientType.TO, new InternetAddress(person[i].getEmail()));
                mimeMessage[i].setSubject(emailSubject);

                MimeBodyPart pdfAttachment = new MimeBodyPart();
                String source = PDFBox.createPDF(person[i].getEmployeeID(), person[i].getNiNumber());

                pdfAttachment.attachFile(source);

                MimeBodyPart bodyPart = new MimeBodyPart();
                bodyPart.setContent(emailBody, "text/html");
                MimeMultipart multipart = new MimeMultipart();
                multipart.addBodyPart(bodyPart);
                multipart.addBodyPart(pdfAttachment);

                mimeMessage[i].setContent(multipart);
            }
            return mimeMessage;
        }

        public static void sendAccountCreationEmail(String toEmail, String employeeName) throws MessagingException {
            String fromUser = DatabaseController.getEmailInfo();   // TODO: get the from user from the database
            String fromUserPassword = DatabaseController.getPasswordInfo();   // TODO: get the from user password from the database
            String emailHost = "smtp.gmail.com";
            Transport transport = newSession.getTransport("smtp");
            transport.connect(emailHost, fromUser, fromUserPassword);

            try {
                Message message = new MimeMessage(newSession);
                message.setFrom(new InternetAddress(fromUser));
                message.setRecipient(Message.RecipientType.TO, new InternetAddress(toEmail));
                message.setSubject("Account Created");
                message.setText("Dear " + employeeName +
                        ",\n\nYour account has been successfully created." +
                        "\n\n Please use password: {your first}_{last 4 characters of NIN}");

                Transport.send(message);

                System.out.println("Email sent successfully!");

            } catch (MessagingException e) {
                e.printStackTrace();
                System.err.println("Failed to send email: " + e.getMessage());
            }
        }

        private void setupServerProperties() {
            Properties properties = System.getProperties();
            properties.put("mail.smtp.host", "smtp.gmail.com");     // TODO: get the host port from the database
            properties.put("mail.smtp.port", "587");
            properties.put("mail.smtp.auth", "true");
            properties.put("mail.smtp.starttls.enable", "true");

            newSession = Session.getDefaultInstance(properties, null);
        }
    }
}
