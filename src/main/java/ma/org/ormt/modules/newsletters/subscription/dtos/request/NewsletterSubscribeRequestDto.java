package ma.org.ormt.modules.newsletters.subscription.dtos.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(name = "NewsletterSubscribeRequest")
public class NewsletterSubscribeRequestDto {

    @NotBlank(message = "Ce champ est requis.")
    @Email(message = "Veuillez saisir une adresse email valide.")
    private String email;

    @AssertTrue(message = "Le consentement est requis.")
    private boolean consentGiven;
}