package ma.org.ancfcc.pva.modules.mission.controller;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import ma.org.ancfcc.pva.core.commun.base.controller.BaseController;
import ma.org.ancfcc.pva.modules.mission.Mission;
import ma.org.ancfcc.pva.modules.mission.dto.MissionDto;
import ma.org.ancfcc.pva.modules.mission.dto.MissionDtoMapper;
import ma.org.ancfcc.pva.modules.mission.dto.detail.MissionDetailDto;
import ma.org.ancfcc.pva.modules.mission.dto.detail.MissionDetailDtoMapper;
import ma.org.ancfcc.pva.modules.mission.dto.export.ExportMissionRequestDto;
import ma.org.ancfcc.pva.modules.mission.service.exports.xls.MissionXlsExportService;

@RestController
@RequestMapping("api/v1/missions")
@RequiredArgsConstructor
public class MissionExportController extends BaseController<Mission> {

        private static final String ENTITY_NAME = "mission";

        private final MissionXlsExportService missionXlsExportService;
        private final MissionDtoMapper missionDtoMapper;
        private final MissionDetailDtoMapper missionDetailMapper;

        @Operation(summary = "export excel " + ENTITY_NAME, responses = {
                        @ApiResponse(responseCode = "200", description = "Success", content = @Content(mediaType = "application/vnd.ms-excel", schema = @Schema(implementation = MissionDto.class))),
                        @ApiResponse(responseCode = "402", description = "Unprocessable entity", content = @Content(mediaType = "ErrorResponse")),
                        @ApiResponse(responseCode = "404", description = "Not found", content = @Content(mediaType = "ErrorResponse")),
                        @ApiResponse(responseCode = "403", description = "Permission denied", content = @Content(mediaType = "ErrorResponse"))
        })
        @PostMapping("/export")
        @PreAuthorize("hasAuthority('mission:read')")
        public ResponseEntity<byte[]> exportMissions(
                        @RequestBody ExportMissionRequestDto requestDto) throws IOException {

                switch (requestDto.getFormat()) {
                        case "xlsx":
                                return missionXlsExportService.exportMissionList(requestDto);
                        case "shp":
                                // return exportExcelFormat(missionDtos);
                        default:
                                return new ResponseEntity<>(HttpStatus.UNSUPPORTED_MEDIA_TYPE);
                }
        }

        // private ResponseEntity<byte[]> exportExcelFormat(ExportMissionRequestDto
        // requestDto) {
        // try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
        // missionExportService.exportMission(requestDto, outputStream);
        // HttpHeaders headers = new HttpHeaders();
        // headers.setContentType(MediaType.parseMediaType("application/vnd.ms-excel"));
        // headers.setContentDispositionFormData("attachment", "missions.xlsx");
        // headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

        // return new ResponseEntity<>(outputStream.toByteArray(), headers,
        // HttpStatus.OK);
        // } catch (IOException e) {
        // e.printStackTrace();
        // return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        // }
        // }

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
