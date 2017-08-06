package ir.company.app.domain.util;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

/**
 * Created by farzad on 7/26/14.
 */
public class MailUtils {
    public static void sendEmail(String userEmailAddress, String content, String subject) {
        Properties props = System.getProperties();
        String host = "smtp.gmail.com";
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", host);
//        props.put("mail.smtp.user", "anijuu.ir@gmail.com");
//        props.put("mail.smtp.password", "anijuu@123");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        Session session = Session.getInstance(props,
            new javax.mail.Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication("anijuu.ir@gmail.com",  "anijuu@123");
                }
            });
        MimeMessage message = new MimeMessage(session);


        try {
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(userEmailAddress));
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        try {
            message.setSubject(subject);
            message.setText(content);
            Transport transport;
            transport = session.getTransport("smtp");
            transport.connect(host, "anijuu.ir@gmail.com", "anijuu@123");
            transport.sendMessage(message, message.getAllRecipients());
            transport.close();
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }



}
