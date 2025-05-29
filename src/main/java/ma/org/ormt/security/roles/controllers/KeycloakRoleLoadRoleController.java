package ma.org.ormt.security.roles.controllers;

import java.util.List;
import java.util.Optional;

import org.keycloak.representations.idm.RoleRepresentation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
import ma.org.ormt.core.commun.base.controller.BaseController;
import ma.org.ormt.core.commun.rest.responses.RestResponse;
import ma.org.ormt.security.authorization.dto.PermissionDto;
import ma.org.ormt.security.keycloak.services.roles.client.KeycloakClientRoleService;
import ma.org.ormt.security.roles.dto.RoleDto;
import ma.org.ormt.security.roles.dto.RoleDtoMapper;
import ma.org.ormt.security.roles.dto.detail.RoleDetailsDto;
import ma.org.ormt.security.roles.dto.detail.RoleDetailsDtoMapper;

@RestController
@RequestMapping(value = "/api/v1/auth/roles")
@RequiredArgsConstructor
@Tag(name = "auth", description = "Auth API")
public class KeycloakRoleLoadRoleController extends BaseController<RoleRepresentation> {

        private static final String ENTITY_NAME = "auth";

        private final KeycloakClientRoleService keycloakClientRoleService;

        private final RoleDtoMapper roleDtoMapper;
        private final RoleDetailsDtoMapper roleDetailsDtoMapper;

        @Operation(summary = "Get all application roles")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Ok", content = {
                                        @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = PermissionDto.class))) }),
                        @ApiResponse(responseCode = "404", description = ENTITY_NAME
                                        + " not found", content = @Content(mediaType = "ErrorResponse")),
                        @ApiResponse(responseCode = "403", description = "Permission denied", content = @Content(mediaType = "ErrorResponse"))
        })
        @GetMapping("")
        @PreAuthorize("hasAuthority('role:list') and (hasAuthority('ROLE_MASTER') or hasAuthority('ROLE_ADMIN'))")
        public ResponseEntity<RestResponse<List<RoleDto>>> getRoles() {

                List<RoleDto> roleList = keycloakClientRoleService.getClientRoles()
                                .stream()
                                .filter(role -> !"uma_protection".equals(role.getName()))
                                .filter(role -> !"master".equals(role.getName()))
                                .collect(java.util.stream.Collectors.toList());

                RestResponse<List<RoleDto>> restResponse = RestResponse.<List<RoleDto>>builder()
                                .status(HttpStatus.OK)
                                .data(roleList)
                                .message(roleList != null ? null : "no data found")
                                .build();

                return ResponseEntity.ok(restResponse);
        }

        /**
         * Get role details by ID.
         */
        @Operation(summary = "Get " + ENTITY_NAME + " by id")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Ok", content = {
                                        @Content(mediaType = "application/json", schema = @Schema(implementation = RoleDetailsDto.class)) }),
                        @ApiResponse(responseCode = "404", description = ENTITY_NAME
                                        + " not found", content = @Content(mediaType = "application/json")),
                        @ApiResponse(responseCode = "403", description = "Permission denied", content = @Content(mediaType = "application/json"))
        })
        @GetMapping("/{id}")
        @PreAuthorize("hasAuthority('role:list') and (hasAuthority('ROLE_MASTER') or hasAuthority('ROLE_ADMIN'))")
        public ResponseEntity<RestResponse<RoleDetailsDto>> getRole(@PathVariable("id") final String id) {

                try {
                        Optional<RoleRepresentation> role = keycloakClientRoleService.findRoleClientById(id);

                        if (role.isEmpty()) {
                                return createNotFoundResponse("Role with name " + id + " not found");
                        }

                        return buildResponseEntity(role.get(), RoleDetailsDto.class, HttpStatus.OK);

                } catch (Exception e) {
                        return createNotFoundResponse("Role with name " + id + " not found");
                }
        }

        @Override
        protected <DTO> DTO mapToDto(RoleRepresentation entity, Class<DTO> dtoClass) {
                if (dtoClass == RoleDetailsDto.class) {
                        return dtoClass.cast(roleDetailsDtoMapper.mapToDto(entity));
                } else if (dtoClass == RoleDto.class) {
                        return dtoClass.cast(roleDtoMapper.mapToDto(entity));
                }
                throw new IllegalArgumentException("Unsupported DTO type: " + dtoClass.getName());
        }
}
