package ma.org.ormt.modules.newsletters.campaign.repositories;

import org.springframework.stereotype.Repository;

import ma.org.ormt.core.commun.base.repository.BaseRepository;
import ma.org.ormt.modules.newsletters.campaign.models.NewsletterCampaign;

@Repository
public interface NewsletterCampaignRepository extends BaseRepository<NewsletterCampaign> {
}