package ma.org.ancfcc.pva.modules.objet.controller;

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
import ma.org.ancfcc.pva.core.commun.base.controller.BaseController;
import ma.org.ancfcc.pva.core.commun.rest.queries.QueryParams;
import ma.org.ancfcc.pva.core.commun.rest.responses.RestResponse;
import ma.org.ancfcc.pva.modules.objet.Objet;
import ma.org.ancfcc.pva.modules.objet.dto.ObjetDto;
import ma.org.ancfcc.pva.modules.objet.dto.ObjetDtoMapper;
import ma.org.ancfcc.pva.modules.objet.dto.detail.ObjetDetailDto;
import ma.org.ancfcc.pva.modules.objet.dto.detail.ObjetDetailDtoMapper;
import ma.org.ancfcc.pva.modules.objet.service.ObjetService;

@RestController
@RequestMapping("api/v1/objets")
@RequiredArgsConstructor
public class ObjetLoadController extends BaseController<Objet> {

        private static final String ENTITY_NAME = "objet";

        private final ObjetService objetService;
        private final ObjetDtoMapper objetDtoMapper;
        private final ObjetDetailDtoMapper objetDetailMapper;

        @Operation(summary = "Get all " + ENTITY_NAME + "s")
        @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Ok", content = {
                        @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = ObjetDto.class))) }),
                        @ApiResponse(responseCode = "404", description = ENTITY_NAME
                                        + " not found", content = @Content(mediaType = "ErrorResponse")),
                        @ApiResponse(responseCode = "403", description = "Permission denied", content = @Content(mediaType = "ErrorResponse"))
        })
        @GetMapping("")
        @PreAuthorize("hasAuthority('objet:list')")
        public ResponseEntity<RestResponse<List<ObjetDto>>> getObjets(
                        @RequestParam(value = "pageIndex", defaultValue = "0") int pageIndex,
                        @RequestParam(value = "pageSize", defaultValue = "-1") int pageSize,
                        @RequestParam(value = "sortField", defaultValue = "createdDate") String sortField,
                        @RequestParam(value = "sortDirection", defaultValue = "DESC") Direction direction,
                        @RequestParam(value = "filters", defaultValue = "") List<String> filters,
                        @RequestParam(value = "globalFilter", defaultValue = "") String globalFilter) {

                QueryParams requestParams = createQueryParams(pageIndex, pageSize, sortField, direction, filters,
                                globalFilter);

                Page<Objet> objetPage = objetService.getEntityList(requestParams);

                List<ObjetDto> dtos = objetDtoMapper.mapToDto(objetPage.getContent());

                QueryParams queryParams = adjustQueryParamsForAllRecords(requestParams, objetPage);

                return buildResponseEntity(dtos, queryParams, HttpStatus.OK);

        }

        @Operation(summary = "Get " + ENTITY_NAME + " by id")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Ok", content = {
                                        @Content(mediaType = "application/json", schema = @Schema(implementation = ObjetDto.class)) }),
                        @ApiResponse(responseCode = "404", description = ENTITY_NAME
                                        + " not found", content = @Content(mediaType = "ErrorResponse")),
                        @ApiResponse(responseCode = "403", description = "Permission denied", content = @Content(mediaType = "ErrorResponse"))
        })
        @GetMapping("/{id}")
        @PreAuthorize("hasAuthority('objet:read')")
        public ResponseEntity<RestResponse<ObjetDetailDto>> getObjet(@PathVariable("id") Long id) {
                Objet objet = objetService.findById(id).orElseThrow(EntityNotFoundException::new);
                return buildResponseEntity(objet, ObjetDetailDto.class, HttpStatus.OK);

        }

        @Override
        protected <DTO> DTO mapToDto(Objet entity, Class<DTO> dtoClass) {
                if (dtoClass == ObjetDetailDto.class) {
                        return dtoClass.cast(objetDetailMapper.mapToDto(entity));
                } else if (dtoClass == ObjetDto.class) {
                        return dtoClass.cast(objetDtoMapper.mapToDto(entity));
                }
                throw new IllegalArgumentException("Unsupported DTO type: " + dtoClass.getName());
        }

}
