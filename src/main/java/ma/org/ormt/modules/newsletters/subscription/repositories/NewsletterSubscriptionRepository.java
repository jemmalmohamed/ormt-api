package ma.org.ormt.modules.newsletters.subscription.repositories;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import ma.org.ormt.core.commun.base.repository.BaseRepository;
import ma.org.ormt.modules.newsletters.subscription.models.NewsletterSubscription;

@Repository
public interface NewsletterSubscriptionRepository extends BaseRepository<NewsletterSubscription> {

    Optional<NewsletterSubscription> findByEmail(String email);

    Optional<NewsletterSubscription> findByUnsubscribeToken(String unsubscribeToken);
}