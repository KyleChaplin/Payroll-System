package application.email;

import application.DatabaseController;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.IOException;
import java.util.*;

public class SingleEmail {
    static Session newSession = null;
    static MimeMessage msg = null;

    //this method for sending account creation emails
    public static void sendAccountCreationEmail(String recipientEmail, String firstName) {
        try {
            setupServerProperties();
            draftAccountCreationEmail(recipientEmail, firstName);
            sendEmail();
        } catch (MessagingException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void draftAccountCreationEmail(String recipientEmail, String firstName) throws MessagingException, IOException {
        String emailSubject = "Account Created";
        String emailBody = "Dear " + firstName + ",<br><br>" +
                "Your account has been successfully created.<br>" +
                "Your password is your first name an underscore and the last " +
                "four characters of your national insurance number<br><br>" +
                "Example: If your name is Bob and the last 4 characters of your " +
                "NI number are 1234, your password is: Bob_1234<br><br>" +
                "Please do not share your password with anyone and store " +
                "it in a secure place i.e. a password manager" +
                "Kind regards,<br>" +
                "Your Application Team";

        msg = new MimeMessage(newSession);
        msg.addRecipient(Message.RecipientType.TO, new InternetAddress(recipientEmail));
        msg.setSubject(emailSubject);

        MimeBodyPart bodyPart = new MimeBodyPart();
        bodyPart.setContent(emailBody, "text/html");
        MimeMultipart multipart = new MimeMultipart();
        multipart.addBodyPart(bodyPart);

        msg.setContent(multipart);
    }

    private static void sendEmail() throws MessagingException {
        String fromUser = DatabaseController.GetTableData.getEmailInfo();
        String fromUserPassword = DatabaseController.GetTableData.getPasswordInfo();
        String emailHost = "smtp.gmail.com";
        Transport transport = newSession.getTransport("smtp");
        transport.connect(emailHost, fromUser, fromUserPassword);
        transport.sendMessage(msg, msg.getAllRecipients());
        //transport.sendMessage(mimeMessage, mimeMessage.getAllRecipients());
        transport.close();
    }

    private static void setupServerProperties() {
        Properties properties = System.getProperties();
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "587");
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");

        newSession = Session.getDefaultInstance(properties, null);
    }

}
