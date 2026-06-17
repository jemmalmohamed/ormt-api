package ma.org.ormt.modules.newsletters.email.models;

public record EmailMessage(
                String to,
                String subject,
                String htmlBody,
                String textBody,
                String fromEmail,
                String fromName) {
}