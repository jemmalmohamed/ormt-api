package ma.org.ormt.modules.newsletters.subscription.dtos;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;
import ma.org.ormt.modules.newsletters.subscription.models.NewsletterSubscriptionStatus;

@Getter
@Setter
public class NewsletterSubscriptionDto {

    private Long id;

    private String email;

    private Boolean consentGiven;

    private NewsletterSubscriptionStatus status;

    private LocalDateTime subscribedAt;

    private LocalDateTime unsubscribedAt;
}