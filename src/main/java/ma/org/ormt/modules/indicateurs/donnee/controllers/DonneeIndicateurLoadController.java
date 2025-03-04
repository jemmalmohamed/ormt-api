package ma.org.ormt.modules.indicateurs.donnee.controllers;

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
import ma.org.ormt.modules.indicateurs.donnee.dtos.DonneeIndicateurDto;
import ma.org.ormt.modules.indicateurs.donnee.dtos.DonneeIndicateurDtoMapper;
import ma.org.ormt.modules.indicateurs.donnee.dtos.details.DonneeIndicateurDetailsDto;
import ma.org.ormt.modules.indicateurs.donnee.dtos.details.DonneeIndicateurDetailsDtoMapper;
import ma.org.ormt.modules.indicateurs.donnee.models.DonneeIndicateur;
import ma.org.ormt.modules.indicateurs.donnee.services.DonneeIndicateurService;

@RestController
@RequestMapping("api/v1/indicateurs")
@RequiredArgsConstructor
public class DonneeIndicateurLoadController extends BaseController<DonneeIndicateur> {

        private static final String ENTITY_NAME = "donneeIndicateur";

        private final DonneeIndicateurService donneeIndicateurService;
        private final DonneeIndicateurDtoMapper donneeIndicateurDtoMapper;
        private final DonneeIndicateurDetailsDtoMapper donneeIndicateurDetailMapper;

        @Operation(summary = "Get all " + ENTITY_NAME + "s")
        @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Ok", content = {
                        @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = DonneeIndicateurDto.class))) }),
                        @ApiResponse(responseCode = "404", description = ENTITY_NAME
                                        + " not found", content = @Content(mediaType = "ErrorResponse")),
                        @ApiResponse(responseCode = "403", description = "Permission denied", content = @Content(mediaType = "ErrorResponse"))
        })
        @GetMapping("/{indicateurId}/donnees")
        @PreAuthorize("hasAuthority('indicateur:list')")
        public ResponseEntity<RestResponse<List<DonneeIndicateurDto>>> getDonneeIndicateur(
                        @PathVariable("indicateurId") Long indicateurId,
                        @RequestParam(value = "pageIndex", defaultValue = "0") int pageIndex,
                        @RequestParam(value = "pageSize", defaultValue = "-1") int pageSize,
                        @RequestParam(value = "sortField", defaultValue = "createdDate") String sortField,
                        @RequestParam(value = "sortDirection", defaultValue = "DESC") Direction direction,
                        @RequestParam(value = "filters", defaultValue = "") List<String> filters,
                        @RequestParam(value = "globalFilter", defaultValue = "") String globalFilter) {

                QueryParams requestParams = createQueryParams(pageIndex, pageSize, sortField, direction, filters,
                                globalFilter);

                Page<DonneeIndicateur> donneeIndicateurPage = donneeIndicateurService.getEntityListByIndicateurId(
                                indicateurId,
                                requestParams);

                List<DonneeIndicateurDto> dtos = donneeIndicateurDtoMapper.mapToDto(donneeIndicateurPage.getContent());

                QueryParams queryParams = adjustQueryParamsForAllRecords(requestParams, donneeIndicateurPage);

                return buildResponseEntity(dtos, queryParams, HttpStatus.OK);
        }

        @Operation(summary = "Get " + ENTITY_NAME + " by id")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Ok", content = {
                                        @Content(mediaType = "application/json", schema = @Schema(implementation = DonneeIndicateurDto.class)) }),
                        @ApiResponse(responseCode = "404", description = ENTITY_NAME
                                        + " not found", content = @Content(mediaType = "ErrorResponse")),
                        @ApiResponse(responseCode = "403", description = "Permission denied", content = @Content(mediaType = "ErrorResponse"))
        })
        @GetMapping("/{indicateurId}/donnees/{id}")
        @PreAuthorize("hasAuthority('indicateur:read')")
        public ResponseEntity<RestResponse<DonneeIndicateurDetailsDto>> getDonneeIndicateur(
                        @PathVariable("indicateurId") Long indicateurId, @PathVariable("id") Long id) {
                DonneeIndicateur donneeIndicateur = donneeIndicateurService.findById(id)
                                .orElseThrow(EntityNotFoundException::new);
                return buildResponseEntity(donneeIndicateur, DonneeIndicateurDetailsDto.class, HttpStatus.OK);
        }

        @Override
        protected <DTO> DTO mapToDto(DonneeIndicateur entity, Class<DTO> dtoClass) {
                if (dtoClass == DonneeIndicateurDetailsDto.class) {
                        return dtoClass.cast(donneeIndicateurDetailMapper.mapToDto(entity));
                } else if (dtoClass == DonneeIndicateurDto.class) {
                        return dtoClass.cast(donneeIndicateurDtoMapper.mapToDto(entity));
                }
                throw new IllegalArgumentException("Unsupported DTO type: " + dtoClass.getName());
        }

}
