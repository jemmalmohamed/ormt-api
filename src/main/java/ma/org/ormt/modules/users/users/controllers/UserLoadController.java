package ma.org.ormt.modules.users.users.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
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
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import ma.org.ormt.core.commun.base.controller.BaseController;
import ma.org.ormt.core.commun.rest.responses.RestResponse;
import ma.org.ormt.modules.users.users.dtos.UserDto;
import ma.org.ormt.modules.users.users.dtos.UserDtoMapper;
import ma.org.ormt.modules.users.users.dtos.details.UserDetailsDto;
import ma.org.ormt.modules.users.users.dtos.details.UserDetailsDtoMapper;
import ma.org.ormt.security.keycloak.config.KeycloakConnectService;
import ma.org.ormt.security.keycloak.services.realm.KeycloakRealmService;
import ma.org.ormt.security.keycloak.services.users.KeycloakUserService;

@RestController
@RequestMapping("api/v1/utilisateurs")
@RequiredArgsConstructor
public class UserLoadController extends BaseController<UserRepresentation> {

        private static final String ENTITY_NAME = "utilisateurs";

        private final KeycloakUserService keycloakUserService;
        private final KeycloakConnectService keycloakConnectService;
        private final KeycloakRealmService keycloakRealmService;

        private final UserDtoMapper userDtoMapper;
        private final UserDetailsDtoMapper userDetailDtoMapper;

        @Value("${keycloak.realm}")
        private String realmName;

        @Value("${keycloak.clients.backend.id}")
        private String backendClientName;

        @Operation(summary = "Get all " + ENTITY_NAME)
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Ok", content = {
                                        @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = UserDto.class)))
                        }),
                        @ApiResponse(responseCode = "404", description = ENTITY_NAME
                                        + " not found", content = @Content(mediaType = "ErrorResponse")),
                        @ApiResponse(responseCode = "403", description = "Permission denied", content = @Content(mediaType = "ErrorResponse"))
        })
        @GetMapping("")
        @PreAuthorize("hasAuthority('user:list')")
        public ResponseEntity<RestResponse<List<UserDto>>> getUsers() {

                // Get Keycloak admin connection
                Keycloak keycloak = keycloakConnectService.getKeyCloakAdminCli();
                // Get realm resource
                RealmResource realmResource = keycloakRealmService.getRealmResource(keycloak, realmName);

                // Get client UUID
                String clientUuid = realmResource.clients()
                                .findByClientId(backendClientName)
                                .stream()
                                .findFirst()
                                .orElseThrow(() -> new EntityNotFoundException("Client not found"))
                                .getId();

                // Get users from Keycloak
                List<UserRepresentation> users = realmResource.users().list();

                // For each user, fetch and set client roles
                for (UserRepresentation user : users) {
                        List<String> clientRoles = realmResource.users()
                                        .get(user.getId())
                                        .roles()
                                        .clientLevel(clientUuid)
                                        .listAll()
                                        .stream()
                                        .map(role -> role.getName())
                                        .toList();

                        Map<String, List<String>> clientRolesMap = new HashMap<>();
                        clientRolesMap.put(backendClientName, clientRoles);
                        user.setClientRoles(clientRolesMap);
                }

                return buildResponseEntity(users, UserDto.class, HttpStatus.OK);
        }

        @Operation(summary = "Get " + ENTITY_NAME + " by id")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Ok", content = {
                                        @Content(mediaType = "application/json", schema = @Schema(implementation = UserDto.class))
                        }),
                        @ApiResponse(responseCode = "404", description = ENTITY_NAME
                                        + " not found", content = @Content(mediaType = "ErrorResponse")),
                        @ApiResponse(responseCode = "403", description = "Permission denied", content = @Content(mediaType = "ErrorResponse"))
        })
        @GetMapping("/{id}")
        @PreAuthorize("hasAuthority('user:read')")
        public ResponseEntity<RestResponse<UserDetailsDto>> getUser(@PathVariable("id") String id) {
                try {
                        // Get Keycloak admin connection
                        Keycloak keycloak = keycloakConnectService.getKeyCloakAdminCli();
                        // Get realm resource
                        RealmResource realmResource = keycloakRealmService.getRealmResource(keycloak,
                                        realmName);

                        // Find user by id
                        UserRepresentation user = keycloakUserService.findUserById(realmResource, id)
                                        .orElseThrow(() -> new EntityNotFoundException(
                                                        "User with id " + id + " not found"));

                        String clientUuid = realmResource.clients()
                                        .findByClientId(backendClientName)
                                        .stream()
                                        .findFirst()
                                        .orElseThrow(() -> new EntityNotFoundException("Client not found"))
                                        .getId();

                        List<String> clientRoles = realmResource.users()
                                        .get(user.getId())
                                        .roles()
                                        .clientLevel(clientUuid)
                                        .listAll()
                                        .stream()
                                        .map(role -> role.getName())
                                        .toList();

                        System.out.println("User roles: " + clientRoles);
                        Map<String, List<String>> clientRolesMap = new HashMap<>();
                        clientRolesMap.put(backendClientName, clientRoles);
                        user.setClientRoles(clientRolesMap);

                        return buildResponseEntity(user, UserDetailsDto.class, HttpStatus.OK);

                } catch (EntityNotFoundException e) {
                        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                        .body(new RestResponse<>(HttpStatus.NOT_FOUND, e.getMessage(), false, null,
                                                        null));
                } catch (Exception e) {
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                        .body(new RestResponse<>(HttpStatus.INTERNAL_SERVER_ERROR,
                                                        "Error retrieving user: " + e.getMessage(), false, null, null));
                }
        }

        @Override
        protected <DTO> DTO mapToDto(UserRepresentation entity, Class<DTO> dtoClass) {
                if (dtoClass == UserDetailsDto.class) {
                        return dtoClass.cast(userDetailDtoMapper.mapToDto(entity));
                } else if (dtoClass == UserDto.class) {
                        return dtoClass.cast(userDtoMapper.mapToDto(entity));
                }
                throw new IllegalArgumentException("Unsupported DTO type: " + dtoClass.getName());
        }

}