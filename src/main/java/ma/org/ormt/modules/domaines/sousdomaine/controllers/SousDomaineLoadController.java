package ma.org.ormt.modules.domaines.sousdomaine.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import ma.org.ormt.core.commun.base.controller.BaseController;
import ma.org.ormt.modules.domaines.sousdomaine.dtos.publicdto.SousDomainePublicDto;
import ma.org.ormt.modules.domaines.sousdomaine.dtos.publicdto.SousDomainePublicDtoMapper;
import ma.org.ormt.modules.domaines.sousdomaine.models.SousDomaine;

@RestController
@RequestMapping("api/v1/public/domaines")
@RequiredArgsConstructor
public class SousDomaineLoadController extends BaseController<SousDomaine> {

        // private static final String ENTITY_NAME = "sousdomaine";

        // private final SousDomaineService sousDomaineService;
        private final SousDomainePublicDtoMapper sousDomainePublicDtoMapper;
        // // private final SousDomaineDetailsDtoMapper sousDomaineDetailMapper;
        // // private final SousDomaineDicDtoMapper sousDomaineDicDtoMapper;

        // @Operation(summary = "Get all " + ENTITY_NAME + "s")
        // @ApiResponses(value = { @ApiResponse(responseCode = "200", description =
        // "Ok", content = {
        // @Content(mediaType = "application/json", array = @ArraySchema(schema =
        // @Schema(implementation = SousDomaineDto.class))) }),
        // @ApiResponse(responseCode = "404", description = ENTITY_NAME
        // + " not found", content = @Content(mediaType = "ErrorResponse")),
        // @ApiResponse(responseCode = "403", description = "Permission denied", content
        // = @Content(mediaType = "ErrorResponse"))
        // })
        // @GetMapping("/{domaineId}/sous-domaines")
        // @PreAuthorize("hasAuthority('domaine:list')")
        // public ResponseEntity<RestResponse<List<SousDomainePublicDto>>>
        // getSousDomaines(
        // @PathVariable("domaineId") Long domaineId,
        // @RequestParam(value = "pageIndex", defaultValue = "0") int pageIndex,
        // @RequestParam(value = "pageSize", defaultValue = "-1") int pageSize,
        // @RequestParam(value = "sortField", defaultValue = "createdDate") String
        // sortField,
        // @RequestParam(value = "sortDirection", defaultValue = "DESC") Direction
        // direction,
        // @RequestParam(value = "filters", defaultValue = "") List<String> filters,
        // @RequestParam(value = "globalFilter", defaultValue = "") String globalFilter)
        // {

        // QueryParams requestParams = buildQueryParams(pageIndex, pageSize, sortField,
        // direction, filters,
        // globalFilter);

        // Page<SousDomaine> sousDomainePage =
        // sousDomaineService.getEntityListByDomaineId(domaineId,
        // requestParams);

        // QueryParams queryParams = adjustQueryParamsToGetAllRecords(requestParams,
        // sousDomainePage);

        // return buildResponseEntity(sousDomainePage.getContent(),
        // SousDomainePublicDto.class, queryParams,
        // HttpStatus.OK);
        // }

        // @Operation(summary = "Get " + ENTITY_NAME + " by id")
        // @ApiResponses(value = {
        // @ApiResponse(responseCode = "200", description = "Ok", content = {
        // @Content(mediaType = "application/json", schema = @Schema(implementation =
        // SousDomaineDto.class)) }),
        // @ApiResponse(responseCode = "404", description = ENTITY_NAME
        // + " not found", content = @Content(mediaType = "ErrorResponse")),
        // @ApiResponse(responseCode = "403", description = "Permission denied", content
        // = @Content(mediaType = "ErrorResponse"))
        // })
        // @GetMapping("/{domaineId}/sous-domaines/{id}")
        // @PreAuthorize("hasAuthority('domaine:read')")
        // public ResponseEntity<RestResponse<SousDomaineDetailsDto>> getSousDomaine(
        // @PathVariable("domaineId") Long domaineId, @PathVariable("id") Long id) {
        // SousDomaine sousDomaine =
        // sousDomaineService.findById(id).orElseThrow(EntityNotFoundException::new);
        // return buildResponseEntity(sousDomaine, SousDomaineDetailsDto.class,
        // HttpStatus.OK);

        // }

        // @Operation(summary = "Get " + ENTITY_NAME + " by id")
        // @ApiResponses(value = {
        // @ApiResponse(responseCode = "200", description = "Ok", content = {
        // @Content(mediaType = "application/json", schema = @Schema(implementation =
        // SousDomaineDto.class)) }),
        // @ApiResponse(responseCode = "404", description = ENTITY_NAME
        // + " not found", content = @Content(mediaType = "ErrorResponse")),
        // @ApiResponse(responseCode = "403", description = "Permission denied", content
        // = @Content(mediaType = "ErrorResponse"))
        // })
        // @GetMapping("/{domaineId}/sous-domaines/{id}/dic")
        // @PreAuthorize("hasAuthority('domaine:read')")
        // public ResponseEntity<RestResponse<SousDomaineDicDto>> getSousDomaineDic(
        // @PathVariable("domaineId") Long domaineId, @PathVariable("id") Long id) {
        // SousDomaine sousDomaine =
        // sousDomaineService.findById(id).orElseThrow(EntityNotFoundException::new);
        // return buildResponseEntity(sousDomaine, SousDomaineDicDto.class,
        // HttpStatus.OK);

        // }

        @Override
        protected <DTO> DTO mapToDto(SousDomaine entity, Class<DTO> dtoClass) {
                if (dtoClass == SousDomainePublicDto.class) {
                        return dtoClass.cast(sousDomainePublicDtoMapper.mapToDto(entity));
                }
                throw new IllegalArgumentException("Unsupported DTO type: " + dtoClass.getName());
        }

}
