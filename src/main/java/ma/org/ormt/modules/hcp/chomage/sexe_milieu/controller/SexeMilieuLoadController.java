package ma.org.ormt.modules.hcp.chomage.sexe_milieu.controller;

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
import ma.org.ormt.modules.hcp.chomage.sexe_milieu.SexeMilieu;
import ma.org.ormt.modules.hcp.chomage.sexe_milieu.dto.SexeMilieuDto;
import ma.org.ormt.modules.hcp.chomage.sexe_milieu.dto.SexeMilieuDtoMapper;
import ma.org.ormt.modules.hcp.chomage.sexe_milieu.dto.detail.SexeMilieuDetailDto;
import ma.org.ormt.modules.hcp.chomage.sexe_milieu.dto.detail.SexeMilieuDetailMapper;
import ma.org.ormt.modules.hcp.chomage.sexe_milieu.service.SexeMilieuService;

@RestController
@RequestMapping("api/v1/hcp-chomage-sexe-milieu")
@RequiredArgsConstructor
public class SexeMilieuLoadController extends BaseController<SexeMilieu> {

        private static final String ENTITY_NAME = "sexeMilieu";

        private final SexeMilieuService sexeMilieuService;
        private final SexeMilieuDtoMapper sexeMilieuDtoMapper;
        private final SexeMilieuDetailMapper sexeMilieuDetailMapper;

        @Operation(summary = "Get all " + ENTITY_NAME + "s")
        @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Ok", content = {
                        @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = SexeMilieuDto.class))) }),
                        @ApiResponse(responseCode = "404", description = ENTITY_NAME
                                        + " not found", content = @Content(mediaType = "ErrorResponse")),
                        @ApiResponse(responseCode = "403", description = "Permission denied", content = @Content(mediaType = "ErrorResponse"))
        })
        @GetMapping("")
        @PreAuthorize("hasAuthority('hcp:list')")
        public ResponseEntity<RestResponse<List<SexeMilieuDto>>> getSexeMilieus(
                        @RequestParam(value = "pageIndex", defaultValue = "0") int pageIndex,
                        @RequestParam(value = "pageSize", defaultValue = "-1") int pageSize,
                        @RequestParam(value = "sortField", defaultValue = "createdDate") String sortField,
                        @RequestParam(value = "sortDirection", defaultValue = "DESC") Direction direction,
                        @RequestParam(value = "filters", defaultValue = "") List<String> filters,
                        @RequestParam(value = "globalFilter", defaultValue = "") String globalFilter) {

                QueryParams requestParams = createQueryParams(pageIndex, pageSize, sortField, direction, filters,
                                globalFilter);

                Page<SexeMilieu> sexeMilieuPage = sexeMilieuService.getEntityList(requestParams);

                List<SexeMilieuDto> dtos = sexeMilieuDtoMapper.mapToDto(sexeMilieuPage.getContent());

                QueryParams queryParams = adjustQueryParamsForAllRecords(requestParams, sexeMilieuPage);

                return buildResponseEntity(dtos, queryParams, HttpStatus.OK);

        }

        @Operation(summary = "Get " + ENTITY_NAME + " by id")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Ok", content = {
                                        @Content(mediaType = "application/json", schema = @Schema(implementation = SexeMilieuDto.class)) }),
                        @ApiResponse(responseCode = "404", description = ENTITY_NAME
                                        + " not found", content = @Content(mediaType = "ErrorResponse")),
                        @ApiResponse(responseCode = "403", description = "Permission denied", content = @Content(mediaType = "ErrorResponse"))
        })
        @GetMapping("/{id}")
        @PreAuthorize("hasAuthority('hcp:read')")
        public ResponseEntity<RestResponse<SexeMilieuDetailDto>> getSexeMilieu(@PathVariable("id") Long id) {
                SexeMilieu sexeMilieu = sexeMilieuService.findById(id).orElseThrow(EntityNotFoundException::new);
                return buildResponseEntity(sexeMilieu, SexeMilieuDetailDto.class, HttpStatus.OK);

        }

        @Override
        protected <DTO> DTO mapToDto(SexeMilieu entity, Class<DTO> dtoClass) {
                if (dtoClass == SexeMilieuDetailDto.class) {
                        return dtoClass.cast(sexeMilieuDetailMapper.mapToDto(entity));
                } else if (dtoClass == SexeMilieuDto.class) {
                        return dtoClass.cast(sexeMilieuDtoMapper.mapToDto(entity));
                }
                throw new IllegalArgumentException("Unsupported DTO type: " + dtoClass.getName());
        }

}
