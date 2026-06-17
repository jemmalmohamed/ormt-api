package ma.org.ormt.modules.newsletters.campaign.controllers.admin;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
import ma.org.ormt.modules.newsletters.campaign.dtos.NewsletterCampaignDto;
import ma.org.ormt.modules.newsletters.campaign.dtos.NewsletterCampaignDtoMapper;
import ma.org.ormt.modules.newsletters.campaign.dtos.request.NewsletterCampaignDraftRequestDto;
import ma.org.ormt.modules.newsletters.campaign.models.NewsletterCampaign;
import ma.org.ormt.modules.newsletters.campaign.services.NewsletterCampaignService;

@Validated
@RestController
@RequestMapping("api/v1/admin/newsletters/campaigns")
@RequiredArgsConstructor
public class NewsletterCampaignAdminController extends BaseController<NewsletterCampaign> {

    private final NewsletterCampaignService newsletterCampaignService;
    private final NewsletterCampaignDtoMapper newsletterCampaignDtoMapper;

    @Operation(summary = "Get newsletter campaigns")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = NewsletterCampaignDto.class)))),
            @ApiResponse(responseCode = "403", description = "Permission denied", content = @Content(mediaType = "application/json"))
    })
    @GetMapping
    @PreAuthorize("hasAuthority('newsletter:list')")
    public ResponseEntity<RestResponse<List<NewsletterCampaignDto>>> getCampaigns(
            @Parameter(description = "Page index (0-based)") @RequestParam(value = "pageIndex", defaultValue = "0") int pageIndex,
            @Parameter(description = "Page size (-1 for all)") @RequestParam(value = "pageSize", defaultValue = "-1") int pageSize,
            @Parameter(description = "Sort field") @RequestParam(value = "sortField", defaultValue = "createdDate") String sortField,
            @Parameter(description = "Sort direction") @RequestParam(value = "sortDirection", defaultValue = "DESC") Direction direction,
            @Parameter(description = "Filters") @RequestParam(value = "filters", required = false) List<String> filters,
            @Parameter(description = "Global filter") @RequestParam(value = "globalFilter", defaultValue = "") String globalFilter) {

        QueryParams requestParams = buildQueryParams(pageIndex, pageSize, sortField, direction, filters, globalFilter);
        Page<NewsletterCampaign> campaignPage = newsletterCampaignService.getEntityList(requestParams);

        return buildResponseEntity(
                campaignPage.getContent(),
                NewsletterCampaignDto.class,
                adjustQueryParamsToGetAllRecords(requestParams, campaignPage),
                HttpStatus.OK,
                true);
    }

    @Operation(summary = "Create newsletter draft")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Created", content = @Content(mediaType = "application/json", schema = @Schema(implementation = NewsletterCampaignDto.class))),
            @ApiResponse(responseCode = "400", description = "Validation failed", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "403", description = "Permission denied", content = @Content(mediaType = "application/json"))
    })
    @PostMapping
    @PreAuthorize("hasAuthority('newsletter:create')")
    public ResponseEntity<RestResponse<NewsletterCampaignDto>> createDraft(
            @RequestBody @Validated NewsletterCampaignDraftRequestDto requestDto) {
        NewsletterCampaign campaign = newsletterCampaignService.createDraft(
                requestDto.getTitle(),
                requestDto.getSubject(),
                requestDto.getContentHtml(),
                requestDto.getContentText());

        return buildResponseEntity(campaign, NewsletterCampaignDto.class, HttpStatus.CREATED);
    }

    @Override
    protected <DTO> DTO mapToDto(NewsletterCampaign entity, Class<DTO> dtoClass) {
        if (dtoClass == NewsletterCampaignDto.class) {
            return dtoClass.cast(newsletterCampaignDtoMapper.mapToDto(entity));
        }

        throw new IllegalArgumentException("Unsupported DTO type: " + dtoClass.getName());
    }
}