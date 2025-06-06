package ma.org.ormt.security.roles.controllers;

import org.keycloak.representations.idm.RoleRepresentation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
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
import ma.org.ormt.core.commun.base.controller.BaseController;
import ma.org.ormt.core.commun.rest.responses.RestResponse;
import ma.org.ormt.core.validators.groups.OnCreate;
import ma.org.ormt.core.validators.groups.OnUpdate;
import ma.org.ormt.security.keycloak.services.roles.client.KeycloakClientRoleService;
import ma.org.ormt.security.roles.dto.RoleDto;
import ma.org.ormt.security.roles.dto.RoleDtoMapper;
import ma.org.ormt.security.roles.dto.request.RoleRequestDto;

@RestController
@RequestMapping(value = "/api/v1/auth/roles")
@RequiredArgsConstructor
@Tag(name = "auth", description = "Auth API")
public class KeycloakRoleCrudController extends BaseController<RoleRepresentation> {

        private static final String ENTITY_NAME = "auth";

        private final KeycloakClientRoleService keycloakClientRoleService;

        private final RoleDtoMapper roleDtoMapper;

        @Operation(summary = "create " + ENTITY_NAME, responses = {
                        @ApiResponse(responseCode = "200", description = "Success", content = @Content(mediaType = "application/json", schema = @Schema(implementation = RoleDto.class))),
                        @ApiResponse(responseCode = "400", description = "Validation failed", content = @Content(mediaType = "ErrorResponse")),
                        @ApiResponse(responseCode = "403", description = "Permission denied", content = @Content(mediaType = "ErrorResponse"))
        })
        @PostMapping("")
        @PreAuthorize("hasAuthority('role:create')")
        public ResponseEntity<RestResponse<RoleDto>> createRole(
                        @Validated(OnCreate.class) @RequestBody RoleRequestDto requestDto) throws Exception {
                RoleRepresentation createdRoleRepresentation = keycloakClientRoleService.createClientRole(requestDto);
                return buildResponseEntity(createdRoleRepresentation, RoleDto.class, HttpStatus.CREATED);
        }

        @Operation(summary = "Update " + ENTITY_NAME, responses = {
                        @ApiResponse(responseCode = "200", description = "Success", content = @Content(mediaType = "application/json", schema = @Schema(implementation = RoleDto.class))),
                        @ApiResponse(responseCode = "400", description = "Validation failed", content = @Content(mediaType = "ErrorResponse")),
                        @ApiResponse(responseCode = "404", description = "Not found", content = @Content(mediaType = "ErrorResponse")),
                        @ApiResponse(responseCode = "403", description = "Permission denied", content = @Content(mediaType = "ErrorResponse"))
        })
        @PutMapping("{id}")
        @PreAuthorize("hasAuthority('role:edit')")
        public ResponseEntity<RestResponse<RoleDto>> updateRole(@PathVariable String id,
                        @Validated(OnUpdate.class) @RequestBody RoleRequestDto roleRequestDto) throws Exception {
                RoleRepresentation createdRoleRepresentation = keycloakClientRoleService
                                .updateClientRole(roleRequestDto);

                return buildResponseEntity(createdRoleRepresentation, RoleDto.class, HttpStatus.OK);
        }

        @Operation(summary = "Delete " + ENTITY_NAME, responses = {
                        @ApiResponse(responseCode = "204", description = "No content"),
                        @ApiResponse(responseCode = "404", description = "Not found", content = @Content(mediaType = "ErrorResponse")),
                        @ApiResponse(responseCode = "403", description = "Permission denied", content = @Content(mediaType = "ErrorResponse"))
        })
        @DeleteMapping("/{id}")
        @PreAuthorize("hasAuthority('role:delete')")
        public ResponseEntity<Void> deleteById(@PathVariable String id) {
                return handleDelete(() -> keycloakClientRoleService.deleteClientRole(id));
        }

        @Override
        protected <DTO> DTO mapToDto(RoleRepresentation entity, Class<DTO> dtoClass) {
                return dtoClass.cast(roleDtoMapper.mapToDto(entity));
        }

}
