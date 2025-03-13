package ma.org.ormt.security.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import ma.org.ormt.core.commun.rest.responses.RestResponse;
import ma.org.ormt.security.dtos.AuthorisationDto;
import ma.org.ormt.security.dtos.PermissionDto;
import ma.org.ormt.security.services.AuthService;

@RestController
@RequestMapping(value = "/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "auth", description = "Auth API")
public class AuthController {

        private static final String ENTITY_NAME = "auth";

        private final AuthService authService;

        @Operation(summary = "Get all " + ENTITY_NAME + "s")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Ok", content = {
                                        @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = PermissionDto.class))) }),
                        @ApiResponse(responseCode = "404", description = ENTITY_NAME
                                        + " not found", content = @Content(mediaType = "ErrorResponse")),
                        @ApiResponse(responseCode = "403", description = "Permission denied", content = @Content(mediaType = "ErrorResponse"))
        })
        @GetMapping("/autorities")
        @PreAuthorize("hasAuthority('auth:read')")
        public ResponseEntity<RestResponse<AuthorisationDto>> getPermissions() {

                AuthorisationDto authorisationDto = authService.getCurrentUserAuth();

                RestResponse<AuthorisationDto> restResponse = RestResponse.<AuthorisationDto>builder()
                                .status(HttpStatus.OK)
                                .data(authorisationDto)
                                .message(authorisationDto != null ? null : "no data found")
                                .build();

                return ResponseEntity.ok(restResponse);
        }

        @Operation(summary = "Get all " + ENTITY_NAME + "s")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Ok", content = {
                                        @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = PermissionDto.class))) }),
                        @ApiResponse(responseCode = "404", description = ENTITY_NAME
                                        + " not found", content = @Content(mediaType = "ErrorResponse")),
                        @ApiResponse(responseCode = "403", description = "Permission denied", content = @Content(mediaType = "ErrorResponse"))
        })
        @GetMapping("/autorities/roles")
        @PreAuthorize("hasAuthority('auth:read')")
        public ResponseEntity<RestResponse<AuthorisationDto>> getRoles() {

                AuthorisationDto authorisationDto = authService.getRoles();

                RestResponse<AuthorisationDto> restResponse = RestResponse.<AuthorisationDto>builder()
                                .status(HttpStatus.OK)
                                .data(authorisationDto)
                                .message(authorisationDto != null ? null : "no data found")
                                .build();

                return ResponseEntity.ok(restResponse);
        }

}
