package ma.org.ormt.modules.newsletters.email.services.impl;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import ma.org.ormt.modules.newsletters.email.models.EmailMessage;
import ma.org.ormt.modules.newsletters.email.services.EmailProvider;

@Service
@RequiredArgsConstructor
public class SmtpEmailProvider implements EmailProvider {

    private final JavaMailSender javaMailSender;

    @Override
    public void send(EmailMessage emailMessage) {
        if (emailMessage.htmlBody() != null && !emailMessage.htmlBody().isBlank()) {
            sendHtmlEmail(emailMessage);
            return;
        }

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(emailMessage.to());
        message.setSubject(emailMessage.subject());
        message.setText(emailMessage.textBody());
        message.setFrom(formatFrom(emailMessage));
        javaMailSender.send(message);
    }

    private void sendHtmlEmail(EmailMessage emailMessage) {
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, false, "UTF-8");
            helper.setTo(emailMessage.to());
            helper.setSubject(emailMessage.subject());
            helper.setText(
                    emailMessage.textBody() != null ? emailMessage.textBody() : emailMessage.htmlBody(),
                    emailMessage.htmlBody());
            helper.setFrom(formatFrom(emailMessage));
            javaMailSender.send(mimeMessage);
        } catch (MessagingException exception) {
            throw new IllegalStateException("Unable to send newsletter email", exception);
        }
    }

    private String formatFrom(EmailMessage emailMessage) {
        if (emailMessage.fromName() == null || emailMessage.fromName().isBlank()) {
            return emailMessage.fromEmail();
        }
        return "%s <%s>".formatted(emailMessage.fromName(), emailMessage.fromEmail());
    }
}