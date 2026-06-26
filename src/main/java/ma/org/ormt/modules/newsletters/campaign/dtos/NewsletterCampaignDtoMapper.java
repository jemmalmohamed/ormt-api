package ma.org.ormt.modules.newsletters.campaign.dtos;

import org.mapstruct.Mapper;

import ma.org.ormt.core.commun.base.mapper.BaseDtoMapper;
import ma.org.ormt.modules.newsletters.campaign.models.NewsletterCampaign;

@Mapper
public interface NewsletterCampaignDtoMapper extends BaseDtoMapper<NewsletterCampaign, NewsletterCampaignDto> {
}