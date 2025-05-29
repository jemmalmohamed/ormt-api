package ma.org.ormt.security.policy.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import ma.org.ormt.core.commun.rest.responses.RestResponse;
import ma.org.ormt.security.keycloak.services.authorization.policy.KeycloakPolicyService;
import ma.org.ormt.security.policy.dto.PolicyDto;
import ma.org.ormt.security.policy.dto.RolePoliciesRequestDto;

@RestController
@RequestMapping(value = "/api/v1/auth/policies")
@RequiredArgsConstructor
@Tag(name = "auth", description = "Auth API")
public class KeycloakPoliciesController {

        private final KeycloakPolicyService keycloakPolicyService;

        @Operation(summary = "Get all authorization policies")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Ok", content = {
                                        @Content(mediaType = "application/json")
                        }),
                        @ApiResponse(responseCode = "403", description = "Permission denied", content = @Content(mediaType = "ErrorResponse"))
        })
        @GetMapping("")
        @PreAuthorize("hasAuthority('role:read')")
        public ResponseEntity<RestResponse<List<PolicyDto>>> getPolicies() {

                List<PolicyDto> policies = keycloakPolicyService.getAllPolicies();

                RestResponse<List<PolicyDto>> restResponse = RestResponse.<List<PolicyDto>>builder()
                                .status(HttpStatus.OK)
                                .data(policies)
                                .message(policies != null ? null : "no data found")
                                .build();
                return ResponseEntity.ok(restResponse);
        }

        @Operation(summary = "Assign a role to multiple policies (role policy type)")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Role assigned to policies", content = {
                                        @Content(mediaType = "application/json")
                        }),
                        @ApiResponse(responseCode = "400", description = "Bad request", content = @Content(mediaType = "ErrorResponse")),
                        @ApiResponse(responseCode = "403", description = "Permission denied", content = @Content(mediaType = "ErrorResponse"))
        })
        @PostMapping("/assign-role-multiple")
        @PreAuthorize("hasAuthority('role:edit')")
        public ResponseEntity<RestResponse<Boolean>> assignRoleToMultiplePolicies(
                        @RequestBody RolePoliciesRequestDto request) {

                String roleName = request.getRoleName();

                if (roleName == null || roleName.trim().isEmpty()) {
                        return ResponseEntity.badRequest().body(RestResponse.<Boolean>builder()
                                        .status(HttpStatus.BAD_REQUEST)
                                        .data(false)
                                        .message("roleName is required")
                                        .build());
                }
                boolean result = keycloakPolicyService.assignRoleToPolicies(request);
                return ResponseEntity.ok(RestResponse.<Boolean>builder()
                                .status(result ? HttpStatus.OK : HttpStatus.BAD_REQUEST)
                                .data(result)
                                .message(result ? "Role assignment operation completed"
                                                : "Failed to assign/detach role for one or more policies")
                                .build());
        }

}
