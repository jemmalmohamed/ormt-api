package ma.org.ormt.modules.newsletters.event.models;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import ma.org.ormt.core.commun.base.entity.BaseEntity;
import ma.org.ormt.modules.newsletters.campaign.models.NewsletterCampaign;
import ma.org.ormt.modules.newsletters.subscription.models.NewsletterSubscription;

@Setter
@Getter
@SuperBuilder
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "newsletter_event")
public class NewsletterEvent extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "campaign_id")
    private NewsletterCampaign campaign;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subscription_id")
    private NewsletterSubscription subscription;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NewsletterEventType eventType;

    private String eventMessage;

    private String providerMessageId;

    @Column(nullable = false)
    private LocalDateTime occurredAt;
}