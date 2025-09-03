package ma.org.ormt.modules.partenaires.partenaire.controllers;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
import ma.org.ormt.modules.partenaires.partenaire.dtos.PartenaireDto;
import ma.org.ormt.modules.partenaires.partenaire.dtos.PartenaireDtoMapper;
import ma.org.ormt.modules.partenaires.partenaire.dtos.details.PartenaireDetailDto;
import ma.org.ormt.modules.partenaires.partenaire.dtos.details.PartenaireDetailDtoMapper;
import ma.org.ormt.modules.partenaires.partenaire.models.Partenaire;
import ma.org.ormt.modules.partenaires.partenaire.services.PartenaireService;

@RestController
@RequestMapping("api/v1/public/partenaires")
@RequiredArgsConstructor
public class PartenaireLoadController extends BaseController<Partenaire> {

        private static final String ENTITY_NAME = "partenaire";

        private final PartenaireService partenaireService;
        private final PartenaireDtoMapper partenaireDtoMapper;
        private final PartenaireDetailDtoMapper partenaireDetailMapper;

        @Operation(summary = "Get all " + ENTITY_NAME + "s")
        @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Ok", content = {
                        @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = PartenaireDto.class))) }),
                        @ApiResponse(responseCode = "404", description = ENTITY_NAME
                                        + " not found", content = @Content(mediaType = "ErrorResponse")),
                        @ApiResponse(responseCode = "403", description = "Permission denied", content = @Content(mediaType = "ErrorResponse"))
        })
        @GetMapping("")
        public ResponseEntity<RestResponse<List<PartenaireDto>>> getPartenaires(
                        @RequestParam(value = "pageIndex", defaultValue = "0") int pageIndex,
                        @RequestParam(value = "pageSize", defaultValue = "-1") int pageSize,
                        @RequestParam(value = "sortField", defaultValue = "createdDate") String sortField,
                        @RequestParam(value = "sortDirection", defaultValue = "DESC") Direction direction,
                        @RequestParam(value = "filters", defaultValue = "") List<String> filters,
                        @RequestParam(value = "globalFilter", defaultValue = "") String globalFilter) {

                QueryParams requestParams = buildQueryParams(pageIndex, pageSize, sortField, direction, filters,
                                globalFilter);

                Page<Partenaire> partenairePage = partenaireService.getEntityList(requestParams);

                QueryParams queryParams = adjustQueryParamsToGetAllRecords(requestParams, partenairePage);

                return buildResponseEntity(partenairePage.getContent(), PartenaireDto.class, queryParams,
                                HttpStatus.OK, true);

        }

        @Operation(summary = "Get " + ENTITY_NAME + " by id")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Ok", content = {
                                        @Content(mediaType = "application/json", schema = @Schema(implementation = PartenaireDto.class)) }),
                        @ApiResponse(responseCode = "404", description = ENTITY_NAME
                                        + " not found", content = @Content(mediaType = "ErrorResponse")),
                        @ApiResponse(responseCode = "403", description = "Permission denied", content = @Content(mediaType = "ErrorResponse"))
        })
        @GetMapping("/{id}")
        public ResponseEntity<RestResponse<PartenaireDetailDto>> getPartenaire(@PathVariable("id") Long id) {
                Partenaire partenaire = partenaireService.findById(id).orElseThrow(EntityNotFoundException::new);
                return buildResponseEntity(partenaire, PartenaireDetailDto.class, HttpStatus.OK);

        }

        @Override
        protected <DTO> DTO mapToDto(Partenaire entity, Class<DTO> dtoClass) {
                if (dtoClass == PartenaireDetailDto.class) {
                        return dtoClass.cast(partenaireDetailMapper.mapToDto(entity));
                } else if (dtoClass == PartenaireDto.class) {
                        return dtoClass.cast(partenaireDtoMapper.mapToDto(entity));
                }
                throw new IllegalArgumentException("Unsupported DTO type: " + dtoClass.getName());
        }

}
