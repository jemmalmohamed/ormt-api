package ma.org.ormt.modules.newsletters.subscription.services;

import java.util.Optional;

import org.springframework.data.domain.Page;

import ma.org.ormt.core.commun.base.service.BaseService;
import ma.org.ormt.core.commun.rest.queries.QueryParams;
import ma.org.ormt.modules.newsletters.subscription.models.NewsletterSubscription;

public interface NewsletterSubscriptionService extends BaseService<NewsletterSubscription> {

    Optional<NewsletterSubscription> findByEmail(String email);

    NewsletterSubscription subscribe(String email, boolean consentGiven);

    NewsletterSubscription unsubscribe(String unsubscribeToken);

    Page<NewsletterSubscription> getEntityList(QueryParams requestParams);
}