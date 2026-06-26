package ma.org.ormt.modules.newsletters.subscription.controllers.admin;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import ma.org.ormt.core.commun.base.controller.BaseController;
import ma.org.ormt.core.commun.rest.queries.QueryParams;
import ma.org.ormt.core.commun.rest.responses.RestResponse;
import ma.org.ormt.modules.newsletters.subscription.dtos.NewsletterSubscriptionDto;
import ma.org.ormt.modules.newsletters.subscription.dtos.NewsletterSubscriptionDtoMapper;
import ma.org.ormt.modules.newsletters.subscription.models.NewsletterSubscription;
import ma.org.ormt.modules.newsletters.subscription.services.NewsletterSubscriptionService;

@Validated
@RestController
@RequestMapping("api/v1/admin/newsletters/subscriptions")
@RequiredArgsConstructor
public class NewsletterSubscriptionAdminLoadController extends BaseController<NewsletterSubscription> {

    private final NewsletterSubscriptionService newsletterSubscriptionService;
    private final NewsletterSubscriptionDtoMapper newsletterSubscriptionDtoMapper;

    @Operation(summary = "Get newsletter subscriptions")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = NewsletterSubscriptionDto.class)))),
            @ApiResponse(responseCode = "403", description = "Permission denied", content = @Content(mediaType = "application/json"))
    })
    @GetMapping
    @PreAuthorize("hasAuthority('newsletter:list')")
    public ResponseEntity<RestResponse<List<NewsletterSubscriptionDto>>> getSubscriptions(
            @Parameter(description = "Page index (0-based)") @RequestParam(value = "pageIndex", defaultValue = "0") int pageIndex,
            @Parameter(description = "Page size (-1 for all)") @RequestParam(value = "pageSize", defaultValue = "-1") int pageSize,
            @Parameter(description = "Sort field") @RequestParam(value = "sortField", defaultValue = "subscribedAt") String sortField,
            @Parameter(description = "Sort direction") @RequestParam(value = "sortDirection", defaultValue = "DESC") Direction direction,
            @Parameter(description = "Filters") @RequestParam(value = "filters", required = false) List<String> filters,
            @Parameter(description = "Global filter") @RequestParam(value = "globalFilter", defaultValue = "") String globalFilter) {

        QueryParams requestParams = buildQueryParams(pageIndex, pageSize, sortField, direction, filters, globalFilter);
        Page<NewsletterSubscription> subscriptionPage = newsletterSubscriptionService.getEntityList(requestParams);

        return buildResponseEntity(
                subscriptionPage.getContent(),
                NewsletterSubscriptionDto.class,
                adjustQueryParamsToGetAllRecords(requestParams, subscriptionPage),
                HttpStatus.OK,
                true);
    }

    @Override
    protected <DTO> DTO mapToDto(NewsletterSubscription entity, Class<DTO> dtoClass) {
        if (dtoClass == NewsletterSubscriptionDto.class) {
            return dtoClass.cast(newsletterSubscriptionDtoMapper.mapToDto(entity));
        }

        throw new IllegalArgumentException("Unsupported DTO type: " + dtoClass.getName());
    }
}