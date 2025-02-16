package ma.org.ormt.modules.indicateur.controller;

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
import ma.org.ormt.modules.indicateur.dto.IndicateurDto;
import ma.org.ormt.modules.indicateur.dto.IndicateurDtoMapper;
import ma.org.ormt.modules.indicateur.dto.detail.IndicateurDetailDto;
import ma.org.ormt.modules.indicateur.dto.detail.IndicateurDetailDtoMapper;
import ma.org.ormt.modules.indicateur.models.Indicateur;
import ma.org.ormt.modules.indicateur.service.IndicateurService;

@RestController
@RequestMapping("api/v1/indicateurs")
@RequiredArgsConstructor
public class IndicateurLoadController extends BaseController<Indicateur> {

        private static final String ENTITY_NAME = "indicateur";

        private final IndicateurService indicateurService;
        private final IndicateurDtoMapper indicateurDtoMapper;
        private final IndicateurDetailDtoMapper indicateurDetailMapper;

        @Operation(summary = "Get all " + ENTITY_NAME + "s")
        @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Ok", content = {
                        @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = IndicateurDto.class))) }),
                        @ApiResponse(responseCode = "404", description = ENTITY_NAME
                                        + " not found", content = @Content(mediaType = "ErrorResponse")),
                        @ApiResponse(responseCode = "403", description = "Permission denied", content = @Content(mediaType = "ErrorResponse"))
        })
        @GetMapping("")
        @PreAuthorize("hasAuthority('indicateur:list')")
        public ResponseEntity<RestResponse<List<IndicateurDto>>> getIndicateurs(
                        @RequestParam(value = "pageIndex", defaultValue = "0") int pageIndex,
                        @RequestParam(value = "pageSize", defaultValue = "-1") int pageSize,
                        @RequestParam(value = "sortField", defaultValue = "createdDate") String sortField,
                        @RequestParam(value = "sortDirection", defaultValue = "DESC") Direction direction,
                        @RequestParam(value = "filters", defaultValue = "") List<String> filters,
                        @RequestParam(value = "globalFilter", defaultValue = "") String globalFilter) {

                QueryParams requestParams = createQueryParams(pageIndex, pageSize, sortField, direction, filters,
                                globalFilter);

                Page<Indicateur> indicateurPage = indicateurService.getEntityList(requestParams);

                List<IndicateurDto> dtos = indicateurDtoMapper.mapToDto(indicateurPage.getContent());

                QueryParams queryParams = adjustQueryParamsForAllRecords(requestParams, indicateurPage);

                return buildResponseEntity(dtos, queryParams, HttpStatus.OK);
        }

        @Operation(summary = "Get " + ENTITY_NAME + " by id")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Ok", content = {
                                        @Content(mediaType = "application/json", schema = @Schema(implementation = IndicateurDto.class)) }),
                        @ApiResponse(responseCode = "404", description = ENTITY_NAME
                                        + " not found", content = @Content(mediaType = "ErrorResponse")),
                        @ApiResponse(responseCode = "403", description = "Permission denied", content = @Content(mediaType = "ErrorResponse"))
        })
        @GetMapping("/{id}")
        @PreAuthorize("hasAuthority('indicateur:read')")
        public ResponseEntity<RestResponse<IndicateurDetailDto>> getIndicateur(@PathVariable("id") Long id) {
                Indicateur indicateur = indicateurService.findById(id).orElseThrow(EntityNotFoundException::new);
                return buildResponseEntity(indicateur, IndicateurDetailDto.class, HttpStatus.OK);
        }

        @Override
        protected <DTO> DTO mapToDto(Indicateur entity, Class<DTO> dtoClass) {
                if (dtoClass == IndicateurDetailDto.class) {
                        return dtoClass.cast(indicateurDetailMapper.mapToDto(entity));
                } else if (dtoClass == IndicateurDto.class) {
                        return dtoClass.cast(indicateurDtoMapper.mapToDto(entity));
                }
                throw new IllegalArgumentException("Unsupported DTO type: " + dtoClass.getName());
        }
}