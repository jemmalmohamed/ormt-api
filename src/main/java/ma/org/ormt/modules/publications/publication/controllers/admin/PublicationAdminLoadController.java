package ma.org.ormt.modules.publications.publication.controllers.admin;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
import lombok.extern.slf4j.Slf4j;
import ma.org.ormt.core.commun.base.controller.BaseController;
import ma.org.ormt.core.commun.rest.queries.QueryParams;
import ma.org.ormt.core.commun.rest.responses.RestResponse;
import ma.org.ormt.modules.publications.publication.dtos.PublicationDto;
import ma.org.ormt.modules.publications.publication.dtos.PublicationDtoMapper;
import ma.org.ormt.modules.publications.publication.dtos.details.PublicationDetailDto;
import ma.org.ormt.modules.publications.publication.dtos.details.PublicationDetailDtoMapper;
import ma.org.ormt.modules.publications.publication.models.Publication;
import ma.org.ormt.modules.publications.publication.services.PublicationService;

@Slf4j
@Validated
@RestController
@RequestMapping("api/v1/admin/publications")
@RequiredArgsConstructor
public class PublicationAdminLoadController extends BaseController<Publication> {

        private static final String ENTITY_NAME = "publication";

        private final PublicationService publicationService;
        private final PublicationDtoMapper publicationDtoMapper;
        private final PublicationDetailDtoMapper publicationDetailMapper;

        /**
         * Get all publications with optional pagination, sorting, and filtering
         * (Admin).
         */
        @Operation(summary = "Get all " + ENTITY_NAME + "s (Admin)")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Ok", content = {
                                        @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = PublicationDto.class))) }),
                        @ApiResponse(responseCode = "404", description = ENTITY_NAME
                                        + " not found", content = @Content(mediaType = "application/json")),
                        @ApiResponse(responseCode = "403", description = "Permission denied", content = @Content(mediaType = "application/json"))
        })
        @GetMapping
        @PreAuthorize("hasAuthority('publication:list')")
        public ResponseEntity<RestResponse<List<PublicationDto>>> getPublications(
                        @Parameter(description = "Page index (0-based)") @RequestParam(value = "pageIndex", defaultValue = "0") final int pageIndex,
                        @Parameter(description = "Page size (-1 for all)") @RequestParam(value = "pageSize", defaultValue = "-1") final int pageSize,
                        @Parameter(description = "Sort field") @RequestParam(value = "sortField", defaultValue = "createdDate") final String sortField,
                        @Parameter(description = "Sort direction") @RequestParam(value = "sortDirection", defaultValue = "DESC") final Direction direction,
                        @Parameter(description = "Filters") @RequestParam(value = "filters", required = false) final List<String> filters,
                        @Parameter(description = "Global filter") @RequestParam(value = "globalFilter", defaultValue = "") final String globalFilter) {

                QueryParams requestParams = buildQueryParams(pageIndex, pageSize, sortField, direction,
                                filters,
                                globalFilter);

                Page<Publication> publicationPage = publicationService.getEntityList(requestParams);

                return buildResponseEntity(
                                publicationPage.getContent(), PublicationDto.class,
                                adjustQueryParamsToGetAllRecords(requestParams, publicationPage),
                                HttpStatus.OK, true);
        }

        /**
         * Get publication details by ID (Admin).
         */
        @Operation(summary = "Get " + ENTITY_NAME + " by id (Admin)")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Ok", content = {
                                        @Content(mediaType = "application/json", schema = @Schema(implementation = PublicationDetailDto.class)) }),
                        @ApiResponse(responseCode = "404", description = ENTITY_NAME
                                        + " not found", content = @Content(mediaType = "application/json")),
                        @ApiResponse(responseCode = "403", description = "Permission denied", content = @Content(mediaType = "application/json"))
        })
        @GetMapping("/{id}")
        @PreAuthorize("hasAuthority('publication:read')")
        public ResponseEntity<RestResponse<PublicationDetailDto>> getPublication(@PathVariable("id") final Long id) {
                try {
                        Optional<Publication> publicationOpt = publicationService.findById(id);
                        if (publicationOpt.isEmpty()) {
                                return ResponseEntity.notFound()
                                                .build();
                        }

                        Publication publication = publicationOpt.get();

                        return buildResponseEntity(publication, PublicationDetailDto.class, HttpStatus.OK);
                } catch (Exception e) {
                        return createNotFoundResponse("Publication with id " + id + " not found");
                }
        }

        // ==================== Abstract Method Implementation ====================

        /**
         * Maps Publication entity to the specified DTO class.
         * Supports PublicationDto and PublicationDetailDto mappings.
         * 
         * @param entity   The Publication entity to map
         * @param dtoClass The target DTO class
         * @return Mapped DTO instance
         * @throws IllegalArgumentException if unsupported DTO type is provided
         */
        @Override
        protected <DTO> DTO mapToDto(final Publication entity, final Class<DTO> dtoClass) {
                if (dtoClass == PublicationDetailDto.class) {
                        return dtoClass.cast(publicationDetailMapper.mapToDto(entity, roleAccesService));
                } else if (dtoClass == PublicationDto.class) {
                        return dtoClass.cast(publicationDtoMapper.mapToDto(entity, roleAccesService));
                }

                throw new IllegalArgumentException("Unsupported DTO type: " + dtoClass.getName());
        }
}
