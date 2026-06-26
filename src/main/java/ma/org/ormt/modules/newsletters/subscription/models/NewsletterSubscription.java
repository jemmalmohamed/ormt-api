package ma.org.ormt.modules.newsletters.subscription.models;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import ma.org.ormt.core.commun.base.entity.BaseEntity;

@Setter
@Getter
@SuperBuilder
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "newsletter_subscription")
public class NewsletterSubscription extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private Boolean consentGiven;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NewsletterSubscriptionStatus status;

    @Column(nullable = false, unique = true)
    private String unsubscribeToken;

    @Column(nullable = false)
    private LocalDateTime subscribedAt;

    private LocalDateTime unsubscribedAt;
}