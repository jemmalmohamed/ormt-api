package ma.org.ormt.modules.newsletters.campaign.dtos;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;
import ma.org.ormt.modules.newsletters.campaign.models.NewsletterCampaignStatus;

@Getter
@Setter
public class NewsletterCampaignDto {

    private Long id;

    private String title;

    private String subject;

    private String contentHtml;

    private String contentText;

    private NewsletterCampaignStatus status;

    private LocalDateTime sentAt;

    private LocalDateTime createdDate;
}