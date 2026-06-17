package ma.org.ormt.modules.newsletters.subscription.services.impl;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import ma.org.ormt.core.commun.base.service.BaseServiceImpl;
import ma.org.ormt.core.commun.base.service.SpecificationService;
import ma.org.ormt.core.commun.rest.queries.QueryParams;
import ma.org.ormt.modules.newsletters.subscription.models.NewsletterSubscription;
import ma.org.ormt.modules.newsletters.subscription.models.NewsletterSubscriptionStatus;
import ma.org.ormt.modules.newsletters.subscription.repositories.NewsletterSubscriptionRepository;
import ma.org.ormt.modules.newsletters.subscription.services.NewsletterSubscriptionService;

@Slf4j
@Service
public class NewsletterSubscriptionServiceImpl extends BaseServiceImpl<NewsletterSubscription>
        implements NewsletterSubscriptionService {

    private static final String NOT_FOUND_STRING = "Newsletter subscription not found";

    private final NewsletterSubscriptionRepository newsletterSubscriptionRepository;

    public NewsletterSubscriptionServiceImpl(
            NewsletterSubscriptionRepository newsletterSubscriptionRepository,
            SpecificationService specificationService) {
        super(newsletterSubscriptionRepository, specificationService);
        this.newsletterSubscriptionRepository = newsletterSubscriptionRepository;
    }

    @Override
    public Optional<NewsletterSubscription> findByEmail(String email) {
        return newsletterSubscriptionRepository.findByEmail(normalizeEmail(email));
    }

    @Override
    public NewsletterSubscription subscribe(String email, boolean consentGiven) {
        String normalizedEmail = normalizeEmail(email);

        Optional<NewsletterSubscription> existingSubscription = newsletterSubscriptionRepository
                .findByEmail(normalizedEmail);
        if (existingSubscription.isPresent()) {
            NewsletterSubscription subscription = existingSubscription.get();
            subscription.setConsentGiven(consentGiven);
            subscription.setStatus(NewsletterSubscriptionStatus.ACTIVE);
            subscription.setSubscribedAt(LocalDateTime.now());
            subscription.setUnsubscribedAt(null);
            if (subscription.getUnsubscribeToken() == null || subscription.getUnsubscribeToken().isBlank()) {
                subscription.setUnsubscribeToken(UUID.randomUUID().toString());
            }
            return newsletterSubscriptionRepository.save(subscription);
        }

        NewsletterSubscription subscription = NewsletterSubscription.builder()
                .email(normalizedEmail)
                .consentGiven(consentGiven)
                .status(NewsletterSubscriptionStatus.ACTIVE)
                .unsubscribeToken(UUID.randomUUID().toString())
                .subscribedAt(LocalDateTime.now())
                .build();

        return newsletterSubscriptionRepository.save(subscription);
    }

    @Override
    public NewsletterSubscription unsubscribe(String unsubscribeToken) {
        NewsletterSubscription subscription = newsletterSubscriptionRepository.findByUnsubscribeToken(unsubscribeToken)
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException(NOT_FOUND_STRING));

        subscription.setStatus(NewsletterSubscriptionStatus.UNSUBSCRIBED);
        subscription.setUnsubscribedAt(LocalDateTime.now());
        NewsletterSubscription updatedSubscription = newsletterSubscriptionRepository.save(subscription);
        log.info("Newsletter subscription {} unsubscribed", subscription.getEmail());
        return updatedSubscription;
    }

    @Override
    public Page<NewsletterSubscription> getEntityList(QueryParams requestParams) {
        return super.getEntityList(requestParams, NewsletterSubscription.class);
    }

    private String normalizeEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email is required");
        }
        return email.trim().toLowerCase();
    }
}