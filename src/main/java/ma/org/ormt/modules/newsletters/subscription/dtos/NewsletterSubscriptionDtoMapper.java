package ma.org.ormt.modules.newsletters.subscription.dtos;

import org.mapstruct.Mapper;

import ma.org.ormt.core.commun.base.mapper.BaseDtoMapper;
import ma.org.ormt.modules.newsletters.subscription.models.NewsletterSubscription;

@Mapper
public interface NewsletterSubscriptionDtoMapper extends BaseDtoMapper<NewsletterSubscription, NewsletterSubscriptionDto> {
}