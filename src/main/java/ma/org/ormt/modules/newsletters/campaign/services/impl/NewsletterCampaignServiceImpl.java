package ma.org.ormt.modules.newsletters.campaign.services.impl;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import ma.org.ormt.core.commun.base.service.BaseServiceImpl;
import ma.org.ormt.core.commun.base.service.SpecificationService;
import ma.org.ormt.core.commun.rest.queries.QueryParams;
import ma.org.ormt.modules.newsletters.campaign.models.NewsletterCampaign;
import ma.org.ormt.modules.newsletters.campaign.models.NewsletterCampaignStatus;
import ma.org.ormt.modules.newsletters.campaign.repositories.NewsletterCampaignRepository;
import ma.org.ormt.modules.newsletters.campaign.services.NewsletterCampaignService;

@Service
public class NewsletterCampaignServiceImpl extends BaseServiceImpl<NewsletterCampaign>
        implements NewsletterCampaignService {

    private final NewsletterCampaignRepository newsletterCampaignRepository;

    public NewsletterCampaignServiceImpl(
            NewsletterCampaignRepository newsletterCampaignRepository,
            SpecificationService specificationService) {
        super(newsletterCampaignRepository, specificationService);
        this.newsletterCampaignRepository = newsletterCampaignRepository;
    }

    @Override
    public Page<NewsletterCampaign> getEntityList(QueryParams requestParams) {
        return super.getEntityList(requestParams, NewsletterCampaign.class);
    }

    @Override
    public NewsletterCampaign createDraft(String title, String subject, String contentHtml, String contentText) {
        NewsletterCampaign campaign = NewsletterCampaign.builder()
                .title(title)
                .subject(subject)
                .contentHtml(contentHtml)
                .contentText(contentText)
                .status(NewsletterCampaignStatus.DRAFT)
                .build();

        return newsletterCampaignRepository.save(campaign);
    }
}