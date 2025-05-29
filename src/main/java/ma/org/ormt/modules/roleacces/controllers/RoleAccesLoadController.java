package ma.org.ormt.modules.roleacces.controllers;

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
import ma.org.ormt.modules.roleacces.dtos.RoleAccesDto;
import ma.org.ormt.modules.roleacces.dtos.RoleAccesDtoMapper;
import ma.org.ormt.modules.roleacces.models.RoleAcces;
import ma.org.ormt.modules.roleacces.services.RoleAccesService;

@RestController
@RequestMapping("api/v1/acces")
@RequiredArgsConstructor
public class RoleAccesLoadController extends BaseController<RoleAcces> {

        private static final String ENTITY_NAME = "roleAcces";

        private final RoleAccesService roleAccesService;
        private final RoleAccesDtoMapper roleAccesDtoMapper;

        @Operation(summary = "Get all " + ENTITY_NAME + "s")
        @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Ok", content = {
                        @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = RoleAccesDto.class))) }),
                        @ApiResponse(responseCode = "404", description = ENTITY_NAME
                                        + " not found", content = @Content(mediaType = "ErrorResponse")),
                        @ApiResponse(responseCode = "403", description = "Permission denied", content = @Content(mediaType = "ErrorResponse"))
        })
        @GetMapping("")
        @PreAuthorize("hasAuthority('role:list')")
        public ResponseEntity<RestResponse<List<RoleAccesDto>>> getRoleAccess(
                        @RequestParam(value = "pageIndex", defaultValue = "0") int pageIndex,
                        @RequestParam(value = "pageSize", defaultValue = "-1") int pageSize,
                        @RequestParam(value = "sortField", defaultValue = "createdDate") String sortField,
                        @RequestParam(value = "sortDirection", defaultValue = "DESC") Direction direction,
                        @RequestParam(value = "filters", defaultValue = "") List<String> filters,
                        @RequestParam(value = "globalFilter", defaultValue = "") String globalFilter) {

                QueryParams requestParams = buildQueryParams(pageIndex, pageSize, sortField, direction, filters,
                                globalFilter);

                Page<RoleAcces> roleAccesPage = roleAccesService.getEntityList(requestParams);

                QueryParams queryParams = adjustQueryParamsToGetAllRecords(requestParams, roleAccesPage);

                return buildResponseEntity(roleAccesPage.getContent(), RoleAccesDto.class, queryParams, HttpStatus.OK);
        }

        @Operation(summary = "Get " + ENTITY_NAME + " by id")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Ok", content = {
                                        @Content(mediaType = "application/json", schema = @Schema(implementation = RoleAccesDto.class)) }),
                        @ApiResponse(responseCode = "404", description = ENTITY_NAME
                                        + " not found", content = @Content(mediaType = "ErrorResponse")),
                        @ApiResponse(responseCode = "403", description = "Permission denied", content = @Content(mediaType = "ErrorResponse"))
        })
        @GetMapping("/{id}")
        @PreAuthorize("hasAuthority('role:read')")
        public ResponseEntity<RestResponse<RoleAccesDto>> getRoleAcces(@PathVariable("id") Long id) {
                RoleAcces roleAcces = roleAccesService.findById(id).orElseThrow(EntityNotFoundException::new);
                return buildResponseEntity(roleAcces, RoleAccesDto.class, HttpStatus.OK);
        }

        @Override
        protected <DTO> DTO mapToDto(RoleAcces entity, Class<DTO> dtoClass) {
                if (dtoClass == RoleAccesDto.class) {
                        return dtoClass.cast(roleAccesDtoMapper.mapToDto(entity));
                }
                throw new IllegalArgumentException("Unsupported DTO type: " + dtoClass.getName());
        }
}