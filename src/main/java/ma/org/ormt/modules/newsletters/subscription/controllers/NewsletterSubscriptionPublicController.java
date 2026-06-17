package ma.org.ormt.modules.newsletters.subscription.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import ma.org.ormt.core.commun.base.controller.BaseController;
import ma.org.ormt.core.commun.rest.responses.RestResponse;
import ma.org.ormt.modules.newsletters.subscription.dtos.NewsletterSubscriptionDto;
import ma.org.ormt.modules.newsletters.subscription.dtos.NewsletterSubscriptionDtoMapper;
import ma.org.ormt.modules.newsletters.subscription.dtos.request.NewsletterSubscribeRequestDto;
import ma.org.ormt.modules.newsletters.subscription.models.NewsletterSubscription;
import ma.org.ormt.modules.newsletters.subscription.services.NewsletterSubscriptionService;

@Validated
@RestController
@RequestMapping("api/v1/public/newsletters")
@RequiredArgsConstructor
public class NewsletterSubscriptionPublicController extends BaseController<NewsletterSubscription> {

    private final NewsletterSubscriptionService newsletterSubscriptionService;
    private final NewsletterSubscriptionDtoMapper newsletterSubscriptionDtoMapper;

    @Operation(summary = "Subscribe to newsletter")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Subscribed", content = @Content(mediaType = "application/json", schema = @Schema(implementation = NewsletterSubscriptionDto.class))),
            @ApiResponse(responseCode = "400", description = "Validation failed", content = @Content(mediaType = "application/json"))
    })
    @PostMapping("/subscribe")
    public ResponseEntity<RestResponse<NewsletterSubscriptionDto>> subscribe(
            @RequestBody @Validated NewsletterSubscribeRequestDto requestDto) {
        NewsletterSubscription subscription = newsletterSubscriptionService.subscribe(
                requestDto.getEmail(),
                requestDto.isConsentGiven());

        return buildResponseEntity(subscription, NewsletterSubscriptionDto.class, HttpStatus.CREATED);
    }

    @Operation(summary = "Unsubscribe from newsletter")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Unsubscribed", content = @Content(mediaType = "application/json", schema = @Schema(implementation = NewsletterSubscriptionDto.class))),
            @ApiResponse(responseCode = "404", description = "Subscription not found", content = @Content(mediaType = "application/json"))
    })
    @PostMapping("/unsubscribe/{token}")
    public ResponseEntity<RestResponse<NewsletterSubscriptionDto>> unsubscribe(@PathVariable("token") String token) {
        NewsletterSubscription subscription = newsletterSubscriptionService.unsubscribe(token);
        return buildResponseEntity(subscription, NewsletterSubscriptionDto.class, HttpStatus.OK);
    }

    @Override
    protected <DTO> DTO mapToDto(NewsletterSubscription entity, Class<DTO> dtoClass) {
        if (dtoClass == NewsletterSubscriptionDto.class) {
            return dtoClass.cast(newsletterSubscriptionDtoMapper.mapToDto(entity));
        }

        throw new IllegalArgumentException("Unsupported DTO type: " + dtoClass.getName());
    }
}