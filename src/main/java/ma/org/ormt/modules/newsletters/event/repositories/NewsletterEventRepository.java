package ma.org.ormt.modules.newsletters.event.repositories;

import org.springframework.stereotype.Repository;

import ma.org.ormt.core.commun.base.repository.BaseRepository;
import ma.org.ormt.modules.newsletters.event.models.NewsletterEvent;

@Repository
public interface NewsletterEventRepository extends BaseRepository<NewsletterEvent> {
}