package ma.org.ancfcc.pva.modules.planaction.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import ma.org.ancfcc.pva.core.commun.rest.responses.RestResponse;
import ma.org.ancfcc.pva.core.validators.groups.OnCreate;
import ma.org.ancfcc.pva.core.validators.groups.OnUpdate;
import ma.org.ancfcc.pva.modules.planaction.PlanAction;
import ma.org.ancfcc.pva.modules.planaction.dto.PlanActionDto;
import ma.org.ancfcc.pva.modules.planaction.dto.PlanActionDtoMapper;
import ma.org.ancfcc.pva.modules.planaction.dto.request.PlanActionRequestDto;
import ma.org.ancfcc.pva.modules.planaction.service.PlanActionService;

@RestController
@RequestMapping(value = "/api/v1/planactions")
@RequiredArgsConstructor
@Tag(name = "PlanAction", description = "PlanAction API")
public class PlanActionCreateUpdateController {

        private static final String ENTITY_NAME = "PlanAction";

        private final PlanActionService planactionService;
        private final PlanActionDtoMapper planactionDtoMapper;

        @Operation(summary = "create " + ENTITY_NAME, responses = {
                        @ApiResponse(responseCode = "200", description = "Success", content = @Content(mediaType = "application/json", schema = @Schema(implementation = PlanActionDto.class))),
                        @ApiResponse(responseCode = "402", description = "Unprocessable entity", content = @Content(mediaType = "ErrorResponse")),
                        @ApiResponse(responseCode = "403", description = "Permission denied", content = @Content(mediaType = "ErrorResponse"))
        })
        @PostMapping("")
        @PreAuthorize("hasAuthority('planaction:create')")
        public ResponseEntity<RestResponse<PlanActionDto>> createPlanAction(
                        @Validated(OnCreate.class) @RequestBody PlanActionRequestDto requestDto) {

                PlanAction planaction = planactionService.create(requestDto);
                return buildResponseEntity(planaction, HttpStatus.CREATED);

        }

        // *********** UPDATE OPERATIONS ***********

        @Operation(summary = "Update " + ENTITY_NAME, responses = {
                        @ApiResponse(responseCode = "200", description = "Success", content = @Content(mediaType = "application/json", schema = @Schema(implementation = PlanActionDto.class))),
                        @ApiResponse(responseCode = "402", description = "Unprocessable entity", content = @Content(mediaType = "ErrorResponse")),
                        @ApiResponse(responseCode = "404", description = "Not found", content = @Content(mediaType = "ErrorResponse")),
                        @ApiResponse(responseCode = "403", description = "Permission denied", content = @Content(mediaType = "ErrorResponse"))
        })
        @PutMapping("{id}")
        @PreAuthorize("hasAuthority('planaction:update')")
        public ResponseEntity<RestResponse<PlanActionDto>> updatePlanAction(@PathVariable Long id,
                        @Validated(OnUpdate.class) @RequestBody PlanActionRequestDto planactionRequestDto) {
                PlanAction planaction = planactionService.update(id, planactionRequestDto);
                return buildResponseEntity(planaction, HttpStatus.OK);
        }

        private ResponseEntity<RestResponse<PlanActionDto>> buildResponseEntity(PlanAction planaction,
                        HttpStatus status) {
                PlanActionDto dto = planactionDtoMapper.mapToDto(planaction);
                RestResponse<PlanActionDto> restResponse = RestResponse.<PlanActionDto>builder()
                                .status(status)
                                .data(dto)
                                .build();
                return ResponseEntity.status(status).body(restResponse);
        }

}