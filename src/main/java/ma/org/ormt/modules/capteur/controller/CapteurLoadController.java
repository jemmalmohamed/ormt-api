package ma.org.ormt.modules.capteur.controller;

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
import ma.org.ormt.modules.capteur.Capteur;
import ma.org.ormt.modules.capteur.dto.CapteurDto;
import ma.org.ormt.modules.capteur.dto.CapteurDtoMapper;
import ma.org.ormt.modules.capteur.dto.detail.CapteurDetailDto;
import ma.org.ormt.modules.capteur.dto.detail.CapteurDetailDtoMapper;
import ma.org.ormt.modules.capteur.service.CapteurService;

@RestController
@RequestMapping("api/v1/capteurs")
@RequiredArgsConstructor
public class CapteurLoadController extends BaseController<Capteur> {

        private static final String ENTITY_NAME = "capteur";

        private final CapteurService capteurService;
        private final CapteurDtoMapper capteurDtoMapper;
        private final CapteurDetailDtoMapper capteurDetailMapper;

        @Operation(summary = "Get all " + ENTITY_NAME + "s")
        @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Ok", content = {
                        @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = CapteurDto.class))) }),
                        @ApiResponse(responseCode = "404", description = ENTITY_NAME
                                        + " not found", content = @Content(mediaType = "ErrorResponse")),
                        @ApiResponse(responseCode = "403", description = "Permission denied", content = @Content(mediaType = "ErrorResponse"))
        })
        @GetMapping("")
        @PreAuthorize("hasAuthority('capteur:list')")
        public ResponseEntity<RestResponse<List<CapteurDto>>> getCapteurs(
                        @RequestParam(value = "pageIndex", defaultValue = "0") int pageIndex,
                        @RequestParam(value = "pageSize", defaultValue = "-1") int pageSize,
                        @RequestParam(value = "sortField", defaultValue = "createdDate") String sortField,
                        @RequestParam(value = "sortDirection", defaultValue = "DESC") Direction direction,
                        @RequestParam(value = "filters", defaultValue = "") List<String> filters,
                        @RequestParam(value = "globalFilter", defaultValue = "") String globalFilter) {

                QueryParams requestParams = createQueryParams(pageIndex, pageSize, sortField, direction, filters,
                                globalFilter);

                Page<Capteur> capteurPage = capteurService.getEntityList(requestParams);

                List<CapteurDto> dtos = capteurDtoMapper.mapToDto(capteurPage.getContent());

                QueryParams queryParams = adjustQueryParamsForAllRecords(requestParams, capteurPage);

                return buildResponseEntity(dtos, queryParams, HttpStatus.OK);

        }

        @Operation(summary = "Get " + ENTITY_NAME + " by id")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Ok", content = {
                                        @Content(mediaType = "application/json", schema = @Schema(implementation = CapteurDto.class)) }),
                        @ApiResponse(responseCode = "404", description = ENTITY_NAME
                                        + " not found", content = @Content(mediaType = "ErrorResponse")),
                        @ApiResponse(responseCode = "403", description = "Permission denied", content = @Content(mediaType = "ErrorResponse"))
        })
        @GetMapping("/{id}")
        @PreAuthorize("hasAuthority('capteur:read')")
        public ResponseEntity<RestResponse<CapteurDetailDto>> getCapteur(@PathVariable("id") Long id) {
                Capteur capteur = capteurService.findById(id).orElseThrow(EntityNotFoundException::new);
                return buildResponseEntity(capteur, CapteurDetailDto.class, HttpStatus.OK);

        }

        @Override
        protected <DTO> DTO mapToDto(Capteur entity, Class<DTO> dtoClass) {
                if (dtoClass == CapteurDetailDto.class) {
                        return dtoClass.cast(capteurDetailMapper.mapToDto(entity));
                } else if (dtoClass == CapteurDto.class) {
                        return dtoClass.cast(capteurDtoMapper.mapToDto(entity));
                }
                throw new IllegalArgumentException("Unsupported DTO type: " + dtoClass.getName());
        }

}
