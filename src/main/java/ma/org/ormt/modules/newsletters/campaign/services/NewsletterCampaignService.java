package ma.org.ormt.modules.newsletters.campaign.services;

import org.springframework.data.domain.Page;

import ma.org.ormt.core.commun.base.service.BaseService;
import ma.org.ormt.core.commun.rest.queries.QueryParams;
import ma.org.ormt.modules.newsletters.campaign.models.NewsletterCampaign;

public interface NewsletterCampaignService extends BaseService<NewsletterCampaign> {

    Page<NewsletterCampaign> getEntityList(QueryParams requestParams);

    NewsletterCampaign createDraft(String title, String subject, String contentHtml, String contentText);
}