package com.danieldigiovanni.email.emailer;

import com.danieldigiovanni.email.emailer.exception.MailtrapEmailerException;
import jakarta.mail.Address;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;

/**
 * Sends an email to Mailtrap for testing instead of sending an acutal email.
 */
public class MailtrapEmailer implements Emailer {

    private final String fromAddress;
    private final String emailHtmlTemplate;
    private final JavaMailSender mailSender;

    public MailtrapEmailer(String fromAddress, String emailHtmlTemplate, JavaMailSender mailSender) {
        this.fromAddress = fromAddress;
        this.emailHtmlTemplate = emailHtmlTemplate;
        this.mailSender = mailSender;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Sends an email <strong>to Mailtrap</strong> for testing purposes (and
     * to prevent my email inbox from being full of test email).
     */
    @Override
    public void sendEmail(String toAddress, String subject, String code, Integer duration) {
        try {
            String emailHtmlContent = this.emailHtmlTemplate
                .replace("{{code}}", code)
                .replace("{{duration}}", duration.toString());

            Address from = new InternetAddress(this.fromAddress);
            Address to = new InternetAddress(toAddress);

            MimeMessage message = mailSender.createMimeMessage();

            message.setFrom(from);
            message.setRecipient(Message.RecipientType.TO, to);
            message.setSubject(subject);
            message.setContent(emailHtmlContent, "text/html; charset=utf-8");

            mailSender.send(message);
        } catch (MessagingException exception) {
            throw new MailtrapEmailerException(exception);
        }
    }

}
