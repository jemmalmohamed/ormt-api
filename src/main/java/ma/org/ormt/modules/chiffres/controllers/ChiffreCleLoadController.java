package ma.org.ormt.modules.chiffres.controllers;

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
import ma.org.ormt.modules.chiffres.dtos.ChiffreCleDto;
import ma.org.ormt.modules.chiffres.dtos.ChiffreCleDtoMapper;
import ma.org.ormt.modules.chiffres.dtos.details.ChiffreCleDetailsDto;
import ma.org.ormt.modules.chiffres.dtos.details.ChiffreCleDetailsDtoMapper;
import ma.org.ormt.modules.chiffres.models.ChiffreCle;
import ma.org.ormt.modules.chiffres.services.ChiffreCleService;
import ma.org.ormt.modules.roleacces.services.RoleAccesService;

@RestController
@RequestMapping("api/v1/chiffrecles")
@RequiredArgsConstructor
public class ChiffreCleLoadController extends BaseController<ChiffreCle> {

        private static final String ENTITY_NAME = "chiffrecle";
        private static final String RESOURCE_TYPE = "chiffrecle";

        private final ChiffreCleService chiffrecleService;
        private final RoleAccesService roleAccesService;
        private final ChiffreCleDtoMapper chiffrecleDtoMapper;
        private final ChiffreCleDetailsDtoMapper chiffrecleDetailMapper;

        @Operation(summary = "Get all " + ENTITY_NAME + "s")
        @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Ok", content = {
                        @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = ChiffreCleDto.class))) }),
                        @ApiResponse(responseCode = "404", description = ENTITY_NAME
                                        + " not found", content = @Content(mediaType = "ErrorResponse")),
                        @ApiResponse(responseCode = "403", description = "Permission denied", content = @Content(mediaType = "ErrorResponse"))
        })
        @GetMapping("")
        @PreAuthorize("hasAuthority('chiffrecle:list')")
        public ResponseEntity<RestResponse<List<ChiffreCleDto>>> getChiffreCles(
                        @RequestParam(value = "pageIndex", defaultValue = "0") int pageIndex,
                        @RequestParam(value = "pageSize", defaultValue = "-1") int pageSize,
                        @RequestParam(value = "sortField", defaultValue = "createdDate") String sortField,
                        @RequestParam(value = "sortDirection", defaultValue = "DESC") Direction direction,
                        @RequestParam(value = "filters", defaultValue = "") List<String> filters,
                        @RequestParam(value = "globalFilter", defaultValue = "") String globalFilter) {

                // Create query params
                QueryParams requestParams = buildQueryParams(pageIndex, pageSize, sortField, direction, filters,
                                globalFilter);

                // Check if user has admin role
                Page<ChiffreCle> chiffreclePage = getEntitiesWithAccessControl(
                                RESOURCE_TYPE,
                                "lecture",
                                requestParams,
                                chiffrecleService::getEntityList, // Function<QueryParams, Page<T>>
                                chiffrecleService::getEntitiesByIds // BiFunction<List<Long>, QueryParams, Page<T>>
                );

                return buildResponseEntity(
                                chiffreclePage.getContent(), ChiffreCleDto.class,
                                adjustQueryParamsToGetAllRecords(requestParams, chiffreclePage),
                                HttpStatus.OK, true);
        }

        @Operation(summary = "Get " + ENTITY_NAME + " by id")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Ok", content = {
                                        @Content(mediaType = "application/json", schema = @Schema(implementation = ChiffreCleDetailsDto.class)) }),
                        @ApiResponse(responseCode = "404", description = ENTITY_NAME
                                        + " not found", content = @Content(mediaType = "ErrorResponse")),
                        @ApiResponse(responseCode = "403", description = "Permission denied", content = @Content(mediaType = "ErrorResponse"))
        })
        @GetMapping("/{id}")
        @PreAuthorize("hasAuthority('chiffrecle:read')")
        public ResponseEntity<RestResponse<ChiffreCleDetailsDto>> getChiffreCle(@PathVariable("id") Long id) {
                if (!hasResourceAccess(id, RESOURCE_TYPE, "lecture")) {
                        return createForbiddenResponse();
                }

                try {

                        ChiffreCle chiffrecle = chiffrecleService.findById(id)
                                        .orElseThrow(() -> new EntityNotFoundException(
                                                        "ChiffreCle with id " + id + " not found"));
                        return buildResponseEntity(chiffrecle, ChiffreCleDetailsDto.class, HttpStatus.OK);
                } catch (EntityNotFoundException e) {
                        return createNotFoundResponse(e.getMessage());
                }
        }

        @Override
        protected <DTO> DTO mapToDto(ChiffreCle entity, Class<DTO> dtoClass) {
                if (dtoClass == ChiffreCleDetailsDto.class) {
                        return dtoClass.cast(chiffrecleDetailMapper.mapToDto(entity, roleAccesService));
                } else if (dtoClass == ChiffreCleDto.class) {
                        return dtoClass.cast(chiffrecleDtoMapper.mapToDto(entity, roleAccesService));
                }
                throw new IllegalArgumentException("Unsupported DTO type: " + dtoClass.getName());
        }

}