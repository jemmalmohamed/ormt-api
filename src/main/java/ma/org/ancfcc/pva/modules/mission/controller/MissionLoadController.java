package ma.org.ancfcc.pva.modules.mission.controller;

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
import ma.org.ancfcc.pva.modules.mission.dto.MissionDto;
import ma.org.ancfcc.pva.modules.mission.dto.MissionDtoMapper;
import ma.org.ancfcc.pva.modules.mission.dto.detail.MissionDetailDto;
import ma.org.ancfcc.pva.modules.mission.dto.detail.MissionDetailDtoMapper;
import ma.org.ancfcc.pva.modules.mission.models.Mission;
import ma.org.ancfcc.pva.modules.mission.repository.MissionRepository;
import ma.org.ancfcc.pva.modules.mission.service.MissionService;

@RestController
@RequestMapping("api/v1/missions")
@RequiredArgsConstructor
public class MissionLoadController extends BaseController<Mission> {

        private static final String ENTITY_NAME = "mission";

        private final MissionService missionService;
        private final MissionDtoMapper missionDtoMapper;
        private final MissionDetailDtoMapper missionDetailMapper;

        private final MissionRepository missionRepository;

        @Operation(summary = "Get all " + ENTITY_NAME + "s")
        @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Ok", content = {
                        @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = MissionDto.class))) }),
                        @ApiResponse(responseCode = "404", description = ENTITY_NAME
                                        + " not found", content = @Content(mediaType = "ErrorResponse")),
                        @ApiResponse(responseCode = "403", description = "Permission denied", content = @Content(mediaType = "ErrorResponse"))
        })
        @GetMapping("")
        @PreAuthorize("hasAuthority('mission:list')")
        public ResponseEntity<RestResponse<List<MissionDto>>> getMissions(
                        @RequestParam(value = "pageIndex", defaultValue = "0") int pageIndex,
                        @RequestParam(value = "pageSize", defaultValue = "-1") int pageSize,
                        @RequestParam(value = "sortField", defaultValue = "createdDate") String sortField,
                        @RequestParam(value = "sortDirection", defaultValue = "DESC") Direction direction,
                        @RequestParam(value = "filters", defaultValue = "") List<String> filters,
                        @RequestParam(value = "globalFilter", defaultValue = "") String globalFilter) {

                QueryParams requestParams = createQueryParams(pageIndex, pageSize, sortField, direction, filters,
                                globalFilter);

                Page<Mission> missionPage = missionService.getEntityList(requestParams);

                List<MissionDto> dtos = missionDtoMapper.mapToDto(missionPage.getContent());

                QueryParams queryParams = adjustQueryParamsForAllRecords(requestParams, missionPage);

                return buildResponseEntity(dtos, queryParams, HttpStatus.OK);

        }

        @Operation(summary = "Get " + ENTITY_NAME + " by id")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Ok", content = {
                                        @Content(mediaType = "application/json", schema = @Schema(implementation = MissionDto.class)) }),
                        @ApiResponse(responseCode = "404", description = ENTITY_NAME
                                        + " not found", content = @Content(mediaType = "ErrorResponse")),
                        @ApiResponse(responseCode = "403", description = "Permission denied", content = @Content(mediaType = "ErrorResponse"))
        })
        @GetMapping("/{id}")
        @PreAuthorize("hasAuthority('mission:read')")
        public ResponseEntity<RestResponse<MissionDetailDto>> getMission(@PathVariable("id") Long id) {
                Mission mission = missionRepository.findById(id).orElseThrow(EntityNotFoundException::new);

                return buildResponseEntity(mission, MissionDetailDto.class, HttpStatus.OK);
        }

        @Override
        protected <DTO> DTO mapToDto(Mission entity, Class<DTO> dtoClass) {
                if (dtoClass == MissionDetailDto.class) {
                        return dtoClass.cast(missionDetailMapper.mapToDto(entity));
                } else if (dtoClass == MissionDto.class) {
                        return dtoClass.cast(missionDtoMapper.mapToDto(entity));
                }
                throw new IllegalArgumentException("Unsupported DTO type: " + dtoClass.getName());
        }

}
