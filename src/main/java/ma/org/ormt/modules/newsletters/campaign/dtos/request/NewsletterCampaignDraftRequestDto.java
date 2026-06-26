package ma.org.ormt.modules.newsletters.campaign.dtos.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(name = "NewsletterCampaignDraftRequest")
public class NewsletterCampaignDraftRequestDto {

    @NotBlank(message = "Ce champ est requis.")
    private String title;

    @NotBlank(message = "Ce champ est requis.")
    private String subject;

    private String contentHtml;

    private String contentText;
}