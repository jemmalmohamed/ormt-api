package ma.org.ormt.security.controllers;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
import ma.org.ormt.modules.users.auth.AuthService;
import ma.org.ormt.security.dtos.AuthorisationDto;
import ma.org.ormt.security.dtos.PermissionDto;
import ma.org.ormt.security.dtos.ResourceDto;
import ma.org.ormt.security.dtos.CreateRoleRequestDto;
import ma.org.ormt.security.dtos.RoleWithPermissionsDto;

@RestController
@RequestMapping(value = "/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "auth", description = "Auth API")
public class AuthController {

        private static final String ENTITY_NAME = "auth";

        private final AuthService authService;

        @Operation(summary = "Get current user permissions and roles")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Ok", content = {
                                        @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = PermissionDto.class))) }),
                        @ApiResponse(responseCode = "404", description = ENTITY_NAME
                                        + " not found", content = @Content(mediaType = "ErrorResponse")),
                        @ApiResponse(responseCode = "403", description = "Permission denied", content = @Content(mediaType = "ErrorResponse"))
        })
        @GetMapping("/autorities/user")
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

        @Operation(summary = "Get all application roles")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Ok", content = {
                                        @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = PermissionDto.class))) }),
                        @ApiResponse(responseCode = "404", description = ENTITY_NAME
                                        + " not found", content = @Content(mediaType = "ErrorResponse")),
                        @ApiResponse(responseCode = "403", description = "Permission denied", content = @Content(mediaType = "ErrorResponse"))
        })
        @GetMapping("/autorities/roles")
        @PreAuthorize("hasAuthority('auth:read')")
        public ResponseEntity<RestResponse<AuthorisationDto>> getAppRoles() {

                AuthorisationDto authorisationDto = authService.getAppRoles();

                RestResponse<AuthorisationDto> restResponse = RestResponse.<AuthorisationDto>builder()
                                .status(HttpStatus.OK)
                                .data(authorisationDto)
                                .message(authorisationDto != null ? null : "no data found")
                                .build();

                return ResponseEntity.ok(restResponse);
        }

        @Operation(summary = "Get all resources and their permissions")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Ok", content = {
                                        @Content(mediaType = "application/json")
                        }),
                        @ApiResponse(responseCode = "403", description = "Permission denied", content = @Content(mediaType = "ErrorResponse"))
        })
        @GetMapping("/autorities/resources")
        @PreAuthorize("hasAuthority('auth:read')")
        public ResponseEntity<RestResponse<List<ResourceDto>>> getResourcesWithPermissions() {
                List<ResourceDto> resources = authService.getResourcesWithPermissions();
                RestResponse<List<ResourceDto>> restResponse = RestResponse.<List<ResourceDto>>builder()
                                .status(HttpStatus.OK)
                                .data(resources)
                                .message(resources != null ? null : "no data found")
                                .build();
                return ResponseEntity.ok(restResponse);
        }

        @Operation(summary = "Get all roles with their permissions")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Ok", content = {
                                        @Content(mediaType = "application/json")
                        }),
                        @ApiResponse(responseCode = "403", description = "Permission denied", content = @Content(mediaType = "ErrorResponse"))
        })
        @GetMapping("/autorities/roles/with-permissions")
        @PreAuthorize("hasAuthority('auth:read')")
        public ResponseEntity<RestResponse<List<RoleWithPermissionsDto>>> getRolesWithPermissions() {
                List<RoleWithPermissionsDto> roles = authService.getRolesWithPermissions();
                RestResponse<List<RoleWithPermissionsDto>> restResponse = RestResponse
                                .<List<RoleWithPermissionsDto>>builder()
                                .status(HttpStatus.OK)
                                .data(roles)
                                .message(roles != null ? null : "no data found")
                                .build();
                return ResponseEntity.ok(restResponse);
        }

        @Operation(summary = "Get all available permissions/scopes")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Ok", content = {
                                        @Content(mediaType = "application/json")
                        }),
                        @ApiResponse(responseCode = "403", description = "Permission denied", content = @Content(mediaType = "ErrorResponse"))
        })
        @GetMapping("/autorities/permissions")
        @PreAuthorize("hasAuthority('auth:read')")
        public ResponseEntity<RestResponse<List<PermissionDto>>> getAllPermissions() {
                List<PermissionDto> permissions = authService.getAllPermissions();
                RestResponse<List<PermissionDto>> restResponse = RestResponse.<List<PermissionDto>>builder()
                                .status(HttpStatus.OK)
                                .data(permissions)
                                .message(permissions != null ? null : "no data found")
                                .build();
                return ResponseEntity.ok(restResponse);
        }

        @Operation(summary = "Create a new role and assign permissions")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "201", description = "Created", content = {
                                        @Content(mediaType = "application/json")
                        }),
                        @ApiResponse(responseCode = "400", description = "Bad request", content = @Content(mediaType = "ErrorResponse")),
                        @ApiResponse(responseCode = "403", description = "Permission denied", content = @Content(mediaType = "ErrorResponse"))
        })
        @PostMapping("/autorities/roles")
        @PreAuthorize("hasAuthority('auth:create')")
        public ResponseEntity<RestResponse<RoleWithPermissionsDto>> createRoleWithPermissions(
                        @RequestBody CreateRoleRequestDto request) {
                RoleWithPermissionsDto createdRole = authService.createRole(request);
                RestResponse<RoleWithPermissionsDto> restResponse = RestResponse.<RoleWithPermissionsDto>builder()
                                .status(HttpStatus.CREATED)
                                .data(createdRole)
                                .message("Role created successfully")
                                .build();
                return ResponseEntity.status(HttpStatus.CREATED).body(restResponse);
        }

        @Operation(summary = "Get all authorization policies")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Ok", content = {
                                        @Content(mediaType = "application/json")
                        }),
                        @ApiResponse(responseCode = "403", description = "Permission denied", content = @Content(mediaType = "ErrorResponse"))
        })
        @GetMapping("/autorities/policies")
        @PreAuthorize("hasAuthority('auth:read')")
        public ResponseEntity<RestResponse<List<Map<String, Object>>>> getAllPolicies() {
                List<Map<String, Object>> policies = authService.getAllPolicies();
                RestResponse<List<Map<String, Object>>> restResponse = RestResponse.<List<Map<String, Object>>>builder()
                                .status(HttpStatus.OK)
                                .data(policies)
                                .message(policies != null ? null : "no data found")
                                .build();
                return ResponseEntity.ok(restResponse);
        }

        @Operation(summary = "Assign a role to a policy (role policy type)")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Role assigned to policy", content = {
                                        @Content(mediaType = "application/json")
                        }),
                        @ApiResponse(responseCode = "400", description = "Bad request", content = @Content(mediaType = "ErrorResponse")),
                        @ApiResponse(responseCode = "403", description = "Permission denied", content = @Content(mediaType = "ErrorResponse"))
        })
        @PostMapping("/autorities/policies/assign-role")
        @PreAuthorize("hasAuthority('auth:create')")
        public ResponseEntity<RestResponse<Boolean>> assignRoleToPolicy(@RequestBody Map<String, String> request) {
                String policyId = request.get("policyId");
                String roleName = request.get("roleName");
                if (policyId == null || roleName == null) {
                        return ResponseEntity.badRequest().body(RestResponse.<Boolean>builder()
                                        .status(HttpStatus.BAD_REQUEST)
                                        .data(false)
                                        .message("policyId and roleName are required")
                                        .build());
                }
                boolean result = authService.assignRoleToPolicy(policyId, roleName);
                return ResponseEntity.ok(RestResponse.<Boolean>builder()
                                .status(result ? HttpStatus.OK : HttpStatus.BAD_REQUEST)
                                .data(result)
                                .message(result ? "Role assigned to policy" : "Failed to assign role to policy")
                                .build());
        }

        @Operation(summary = "Assign a role to multiple policies (role policy type)")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Role assigned to policies", content = {
                                        @Content(mediaType = "application/json")
                        }),
                        @ApiResponse(responseCode = "400", description = "Bad request", content = @Content(mediaType = "ErrorResponse")),
                        @ApiResponse(responseCode = "403", description = "Permission denied", content = @Content(mediaType = "ErrorResponse"))
        })
        @PostMapping("/autorities/policies/assign-role-multiple")
        @PreAuthorize("hasAuthority('auth:create')")
        public ResponseEntity<RestResponse<Boolean>> assignRoleToMultiplePolicies(
                        @RequestBody Map<String, Object> request) {
                Object policyIdsObj = request.get("policyIds");
                String roleName = (String) request.get("roleName");
                if (!(policyIdsObj instanceof List) || roleName == null) {
                        return ResponseEntity.badRequest().body(RestResponse.<Boolean>builder()
                                        .status(HttpStatus.BAD_REQUEST)
                                        .data(false)
                                        .message("policyIds (array) and roleName are required")
                                        .build());
                }
                @SuppressWarnings("unchecked")
                List<String> policyIds = (List<String>) policyIdsObj;
                boolean result = authService.assignRoleToPolicies(policyIds, roleName);
                return ResponseEntity.ok(RestResponse.<Boolean>builder()
                                .status(result ? HttpStatus.OK : HttpStatus.BAD_REQUEST)
                                .data(result)
                                .message(result ? "Role assigned to all policies"
                                                : "Failed to assign role to one or more policies")
                                .build());
        }

}
