package ma.org.ancfcc.pva.core.commun.base.controller;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import ma.org.ancfcc.pva.core.commun.rest.responses.RestResponse;
import ma.org.ancfcc.pva.core.validators.groups.OnCreate;
import ma.org.ancfcc.pva.core.validators.groups.OnUpdate;

public interface EntityOperations<DTO, R> {

    @Operation(summary = "Create entity", responses = {
            @ApiResponse(responseCode = "200", description = "Success", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Object.class))),
            @ApiResponse(responseCode = "402", description = "Unprocessable entity", content = @Content(mediaType = "ErrorResponse")),
            @ApiResponse(responseCode = "403", description = "Permission denied", content = @Content(mediaType = "ErrorResponse"))
    })
    @PostMapping("")
    @PreAuthorize("hasAuthority(#createAuthority)")
    ResponseEntity<RestResponse<DTO>> create(@Validated(OnCreate.class) @RequestBody R requestDto,
            @RequestParam String createAuthority);

    @Operation(summary = "Update entity", responses = {
            @ApiResponse(responseCode = "200", description = "Success", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Object.class))),
            @ApiResponse(responseCode = "402", description = "Unprocessable entity", content = @Content(mediaType = "ErrorResponse")),
            @ApiResponse(responseCode = "404", description = "Not found", content = @Content(mediaType = "ErrorResponse")),
            @ApiResponse(responseCode = "403", description = "Permission denied", content = @Content(mediaType = "ErrorResponse"))
    })
    @PutMapping("{id}")
    @PreAuthorize("hasAuthority(#updateAuthority)")
    ResponseEntity<RestResponse<DTO>> update(@PathVariable UUID id,
            @Validated(OnUpdate.class) @RequestBody R requestDto, @RequestParam String updateAuthority);
}
