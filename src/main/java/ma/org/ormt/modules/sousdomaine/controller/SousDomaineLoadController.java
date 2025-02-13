package ma.org.ormt.modules.sousdomaine.controller;

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
import ma.org.ormt.modules.sousdomaine.SousDomaine;
import ma.org.ormt.modules.sousdomaine.dto.SousDomaineDto;
import ma.org.ormt.modules.sousdomaine.dto.SousDomaineDtoMapper;
import ma.org.ormt.modules.sousdomaine.dto.detail.SousDomaineDetailDto;
import ma.org.ormt.modules.sousdomaine.dto.detail.SousDomaineDetailMapper;
import ma.org.ormt.modules.sousdomaine.service.SousDomaineService;

@RestController
@RequestMapping("api/v1/sousdomaines")
@RequiredArgsConstructor
public class SousDomaineLoadController extends BaseController<SousDomaine> {

        private static final String ENTITY_NAME = "sousdomaine";

        private final SousDomaineService sousDomaineService;
        private final SousDomaineDtoMapper sousDomaineDtoMapper;
        private final SousDomaineDetailMapper sousDomaineDetailMapper;

        @Operation(summary = "Get all " + ENTITY_NAME + "s")
        @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Ok", content = {
                        @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = SousDomaineDto.class))) }),
                        @ApiResponse(responseCode = "404", description = ENTITY_NAME
                                        + " not found", content = @Content(mediaType = "ErrorResponse")),
                        @ApiResponse(responseCode = "403", description = "Permission denied", content = @Content(mediaType = "ErrorResponse"))
        })
        @GetMapping("")
        @PreAuthorize("hasAuthority('sousdomaine:list')")
        public ResponseEntity<RestResponse<List<SousDomaineDto>>> getSousDomaines(
                        @RequestParam(value = "pageIndex", defaultValue = "0") int pageIndex,
                        @RequestParam(value = "pageSize", defaultValue = "-1") int pageSize,
                        @RequestParam(value = "sortField", defaultValue = "createdDate") String sortField,
                        @RequestParam(value = "sortDirection", defaultValue = "DESC") Direction direction,
                        @RequestParam(value = "filters", defaultValue = "") List<String> filters,
                        @RequestParam(value = "globalFilter", defaultValue = "") String globalFilter) {

                QueryParams requestParams = createQueryParams(pageIndex, pageSize, sortField, direction, filters,
                                globalFilter);

                Page<SousDomaine> sousDomainePage = sousDomaineService.getEntityList(requestParams);

                List<SousDomaineDto> dtos = sousDomaineDtoMapper.mapToDto(sousDomainePage.getContent());

                QueryParams queryParams = adjustQueryParamsForAllRecords(requestParams, sousDomainePage);

                return buildResponseEntity(dtos, queryParams, HttpStatus.OK);

        }

        @Operation(summary = "Get " + ENTITY_NAME + " by id")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Ok", content = {
                                        @Content(mediaType = "application/json", schema = @Schema(implementation = SousDomaineDto.class)) }),
                        @ApiResponse(responseCode = "404", description = ENTITY_NAME
                                        + " not found", content = @Content(mediaType = "ErrorResponse")),
                        @ApiResponse(responseCode = "403", description = "Permission denied", content = @Content(mediaType = "ErrorResponse"))
        })
        @GetMapping("/{id}")
        @PreAuthorize("hasAuthority('sousdomaine:read')")
        public ResponseEntity<RestResponse<SousDomaineDetailDto>> getSousDomaine(@PathVariable("id") Long id) {
                SousDomaine sousDomaine = sousDomaineService.findById(id).orElseThrow(EntityNotFoundException::new);
                return buildResponseEntity(sousDomaine, SousDomaineDetailDto.class, HttpStatus.OK);

        }

        @Override
        protected <DTO> DTO mapToDto(SousDomaine entity, Class<DTO> dtoClass) {
                if (dtoClass == SousDomaineDetailDto.class) {
                        return dtoClass.cast(sousDomaineDetailMapper.mapToDto(entity));
                } else if (dtoClass == SousDomaineDto.class) {
                        return dtoClass.cast(sousDomaineDtoMapper.mapToDto(entity));
                }
                throw new IllegalArgumentException("Unsupported DTO type: " + dtoClass.getName());
        }

}
