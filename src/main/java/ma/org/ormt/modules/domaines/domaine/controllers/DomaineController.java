package ma.org.ormt.modules.domaines.domaine.controllers;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import ma.org.ormt.core.commun.base.controller.BaseController;
import ma.org.ormt.core.commun.rest.queries.QueryParams;
import ma.org.ormt.core.commun.rest.responses.RestResponse;
import ma.org.ormt.modules.domaines.domaine.dtos.DomaineDto;
import ma.org.ormt.modules.domaines.domaine.dtos.DomaineDtoMapper;
import ma.org.ormt.modules.domaines.domaine.dtos.details.DomaineDetailDto;
import ma.org.ormt.modules.domaines.domaine.dtos.details.DomaineDetailDtoMapper;
import ma.org.ormt.modules.domaines.domaine.models.Domaine;
import ma.org.ormt.modules.domaines.domaine.services.DomaineService;

@RestController
@RequestMapping("api/v1/public/espaces/{espaceId}/domaines") // Context-aware public controller
@RequiredArgsConstructor
public class DomaineController extends BaseController<Domaine> {

        private static final String ENTITY_NAME = "domaine";

        private final DomaineService domaineService;
        private final DomaineDtoMapper domaineDtoMapper;
        private final DomaineDetailDtoMapper domaineDetailMapper;

        @Operation(summary = "Get all " + ENTITY_NAME + "s in espace (context-aware)")
        @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Ok", content = {
                        @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = DomaineDto.class))) }),
                        @ApiResponse(responseCode = "404", description = ENTITY_NAME
                                        + " not found", content = @Content(mediaType = "ErrorResponse")),
                        @ApiResponse(responseCode = "403", description = "Permission denied", content = @Content(mediaType = "ErrorResponse"))
        })
        @GetMapping("")
        @PreAuthorize("hasAuthority('domaine:list')")
        public ResponseEntity<RestResponse<List<DomaineDto>>> getDomaines(
                        @PathVariable("espaceId") Long espaceId,
                        @RequestParam(value = "pageIndex", defaultValue = "0") int pageIndex,
                        @RequestParam(value = "pageSize", defaultValue = "-1") int pageSize,
                        @RequestParam(value = "sortField", defaultValue = "createdDate") String sortField,
                        @RequestParam(value = "sortDirection", defaultValue = "DESC") Direction direction,
                        @RequestParam(value = "filters", defaultValue = "") List<String> filters,
                        @RequestParam(value = "globalFilter", defaultValue = "") String globalFilter) {

                if (!hasResourceAccess(espaceId, "espace", "lecture")) {
                        return createForbiddenResponse();
                }

                List<String> effectiveFilters = (filters == null) ? new java.util.ArrayList<>()
                                : new java.util.ArrayList<>(filters);
                effectiveFilters.add("actif:like:true");
                effectiveFilters.add("espaceDomaines.espace.id:=:" + espaceId);

                QueryParams requestParams = buildQueryParams(pageIndex, pageSize, sortField, direction,
                                effectiveFilters,
                                globalFilter);

                Page<Domaine> domainePage = domaineService.getEntityList(requestParams);

                return buildResponseEntity(
                                domainePage.getContent(), DomaineDto.class,
                                adjustQueryParamsToGetAllRecords(requestParams, domainePage),
                                HttpStatus.OK);
        }

        @Operation(summary = "Get " + ENTITY_NAME + " by id in espace (context-aware)")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Ok", content = {
                                        @Content(mediaType = "application/json", schema = @Schema(implementation = DomaineDetailDto.class)) }),
                        @ApiResponse(responseCode = "404", description = ENTITY_NAME
                                        + " not found", content = @Content(mediaType = "ErrorResponse")),
                        @ApiResponse(responseCode = "403", description = "Permission denied", content = @Content(mediaType = "ErrorResponse"))
        })
        @GetMapping("/{id}")
        @PreAuthorize("hasAuthority('domaine:read')")
        public ResponseEntity<RestResponse<DomaineDetailDto>> getDomaine(
                        @PathVariable("espaceId") Long espaceId,
                        @PathVariable("id") Long id) {

                // Check if the user has access to the specified espace
                boolean hasAccess = hasResourceAccess(espaceId, "espace", "lecture");
                boolean existsInEspace = domaineService.existsInEspace(id, espaceId);

                if (!hasAccess || !existsInEspace) {
                        return createForbiddenResponse();
                }
                Domaine domaine = domaineService.findById(id).orElseThrow(EntityNotFoundException::new);

                return buildResponseEntity(domaine, DomaineDetailDto.class, HttpStatus.OK);
        }

        @Override
        protected <DTO> DTO mapToDto(Domaine entity, Class<DTO> dtoClass) {
                if (dtoClass == DomaineDetailDto.class) {
                        return dtoClass.cast(domaineDetailMapper.mapToDto(entity));
                } else if (dtoClass == DomaineDto.class) {
                        return dtoClass.cast(domaineDtoMapper.mapToDto(entity));
                }
                throw new IllegalArgumentException("Unsupported DTO type: " + dtoClass.getName());
        }
}
