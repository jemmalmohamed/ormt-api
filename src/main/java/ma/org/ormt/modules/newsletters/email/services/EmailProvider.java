package ma.org.ormt.modules.newsletters.email.services;

import ma.org.ormt.modules.newsletters.email.models.EmailMessage;

public interface EmailProvider {

    void send(EmailMessage emailMessage);
}