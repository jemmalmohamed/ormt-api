package ma.org.ormt.security.users.users.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import ma.org.ormt.core.commun.base.controller.BaseController;
import ma.org.ormt.core.commun.rest.responses.RestResponse;
import ma.org.ormt.core.exceptions.handlers.KeycloakException;
import ma.org.ormt.core.validators.groups.OnCreate;
import ma.org.ormt.core.validators.groups.OnUpdate;
import ma.org.ormt.security.keycloak.services.KeycloakConnectService;
import ma.org.ormt.security.keycloak.services.realm.KeycloakRealmService;
import ma.org.ormt.security.users.users.dtos.UserDto;
import ma.org.ormt.security.users.users.dtos.UserDtoMapper;
import ma.org.ormt.security.users.users.dtos.request.UserRequestDto;
import ma.org.ormt.security.users.users.services.UserService;

@RestController
@RequestMapping(value = "/api/v1/utilisateurs")
@RequiredArgsConstructor
@Tag(name = "User", description = "User API")
public class UserCrudController extends BaseController<UserRepresentation> {

        private static final String ENTITY_NAME = "utilisateur";

        @Value("${keycloak.realm}")
        private String realmName;

        private final UserService userService;
        private final KeycloakConnectService keycloakConnectService;
        private final KeycloakRealmService keycloakRealmService;

        private final UserDtoMapper userDtoMapper;

        @Operation(summary = "create " + ENTITY_NAME, responses = {
                        @ApiResponse(responseCode = "200", description = "Success", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserDto.class))),
                        @ApiResponse(responseCode = "400", description = "Validation failed", content = @Content(mediaType = "ErrorResponse")),
                        @ApiResponse(responseCode = "403", description = "Permission denied", content = @Content(mediaType = "ErrorResponse"))
        })
        @PostMapping("")
        @PreAuthorize("hasAuthority('user:create')")
        public ResponseEntity<RestResponse<UserDto>> createUser(
                        @Validated(OnCreate.class) @RequestBody UserRequestDto requestDto) throws Exception {

                UserRepresentation userRepresentation = userService.create(requestDto);

                return buildResponseEntity(userRepresentation, UserDto.class, HttpStatus.CREATED);
        }

        @Operation(summary = "Update " + ENTITY_NAME, responses = {
                        @ApiResponse(responseCode = "200", description = "Success", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserDto.class))),
                        @ApiResponse(responseCode = "400", description = "Validation failed", content = @Content(mediaType = "ErrorResponse")),
                        @ApiResponse(responseCode = "404", description = "Not found", content = @Content(mediaType = "ErrorResponse")),
                        @ApiResponse(responseCode = "403", description = "Permission denied", content = @Content(mediaType = "ErrorResponse"))
        })
        @PutMapping("{id}")
        @PreAuthorize("hasAuthority('user:edit')")
        public ResponseEntity<RestResponse<UserDto>> updateUser(@PathVariable String id,
                        @Validated(OnUpdate.class) @RequestBody UserRequestDto userRequestDto) throws Exception {
                RealmResource realm = getRealmResource();

                UserRepresentation userRepresentation;
                try {
                        userRepresentation = realm.users().get(id).toRepresentation();
                } catch (Exception e) {
                        throw new KeycloakException("User not found");
                }

                userRepresentation.setFirstName(userRequestDto.getFirstName());
                userRepresentation.setLastName(userRequestDto.getLastName());
                userRepresentation.setEmail(userRequestDto.getEmail());
                userRepresentation.setEnabled(userRequestDto.getEnabled());
                userRepresentation.setEmailVerified(true);

                realm.users().get(id).update(userRepresentation);

                return buildResponseEntity(userRepresentation, UserDto.class, HttpStatus.OK);
        }

        @Operation(summary = "Delete " + ENTITY_NAME, responses = {
                        @ApiResponse(responseCode = "204", description = "No content"),
                        @ApiResponse(responseCode = "404", description = "Not found", content = @Content(mediaType = "ErrorResponse")),
                        @ApiResponse(responseCode = "403", description = "Permission denied", content = @Content(mediaType = "ErrorResponse"))
        })
        @DeleteMapping("/{id}")
        @PreAuthorize("hasAuthority('user:delete')")
        public ResponseEntity<Void> deleteById(@PathVariable String id) {
                RealmResource realm = getRealmResource();
                try {
                        // Check if the user exists first
                        try {
                                realm.users().get(id).toRepresentation();
                        } catch (Exception e) {
                                return ResponseEntity.notFound().build();
                        }

                        realm.users().get(id).remove();
                        return ResponseEntity.noContent().build();
                } catch (DataIntegrityViolationException e) {
                        return ResponseEntity.status(HttpStatus.CONFLICT).build();
                } catch (Exception e) {
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
                }
        }

        @Operation(summary = "Delete multiple " + ENTITY_NAME + "s", responses = {
                        @ApiResponse(responseCode = "204", description = "No content"),
                        @ApiResponse(responseCode = "404", description = "Not found", content = @Content(mediaType = "ErrorResponse")),
                        @ApiResponse(responseCode = "403", description = "Permission denied", content = @Content(mediaType = "ErrorResponse"))
        })
        @DeleteMapping("/bulk")
        @PreAuthorize("hasAuthority('user:delete')")
        public ResponseEntity<Map<String, String>> deleteMultiple(@RequestBody List<String> ids) {
                RealmResource realm = getRealmResource();
                try {
                        // Verify all users exist first
                        List<String> notFoundIds = new ArrayList<>();
                        for (String id : ids) {
                                try {
                                        realm.users().get(id).toRepresentation();
                                } catch (Exception e) {
                                        notFoundIds.add(id);
                                }
                        }

                        if (!notFoundIds.isEmpty()) {
                                Map<String, String> response = new HashMap<>();
                                response.put("message", "Les utilisateurs suivants n'ont pas été trouvés: "
                                                + String.join(", ", notFoundIds));
                                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
                        }

                        // Delete all users
                        for (String id : ids) {
                                realm.users().delete(id);
                        }
                        return ResponseEntity.noContent().build();
                } catch (DataIntegrityViolationException e) {
                        Map<String, String> response = new HashMap<>();
                        response.put("message", "Suppression impossible, les données sont utilisées ailleurs");
                        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
                } catch (Exception e) {
                        Map<String, String> response = new HashMap<>();
                        response.put("message", "Une erreur est survenue lors de la suppression: " + e.getMessage());
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
                }
        }

        @Operation(summary = "Delete all " + ENTITY_NAME + "s", responses = {
                        @ApiResponse(responseCode = "204", description = "No content"),
                        @ApiResponse(responseCode = "404", description = "Not found", content = @Content(mediaType = "ErrorResponse")),
                        @ApiResponse(responseCode = "403", description = "Permission denied", content = @Content(mediaType = "ErrorResponse"))
        })
        @DeleteMapping("/all")
        @PreAuthorize("hasAuthority('user:delete')")
        public ResponseEntity<Map<String, String>> deleteAll() {
                RealmResource realm = getRealmResource();
                try {
                        List<UserRepresentation> users = realm.users().list();

                        if (users.isEmpty()) {
                                Map<String, String> response = new HashMap<>();
                                response.put("message", "Aucun utilisateur à supprimer");
                                return ResponseEntity.ok(response);
                        }

                        for (UserRepresentation user : users) {
                                realm.users().delete(user.getId());
                        }

                        Map<String, String> response = new HashMap<>();
                        response.put("message", users.size() + " utilisateur(s) supprimé(s)");
                        return ResponseEntity.ok(response);
                } catch (DataIntegrityViolationException e) {
                        Map<String, String> response = new HashMap<>();
                        response.put("message", "Suppression impossible, les données sont utilisées ailleurs");
                        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
                } catch (Exception e) {
                        Map<String, String> response = new HashMap<>();
                        response.put("message", "Une erreur est survenue lors de la suppression: " + e.getMessage());
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
                }
        }

        @Operation(summary = "Delete all except specified IDs " + ENTITY_NAME + "s", responses = {
                        @ApiResponse(responseCode = "204", description = "No content"),
                        @ApiResponse(responseCode = "404", description = "Not found", content = @Content(mediaType = "ErrorResponse")),
                        @ApiResponse(responseCode = "403", description = "Permission denied", content = @Content(mediaType = "ErrorResponse"))
        })
        @DeleteMapping("/exclude")
        @PreAuthorize("hasAuthority('user:delete')")
        public ResponseEntity<Map<String, String>> deleteAllExcept(@RequestBody List<String> ids) {
                RealmResource realm = getRealmResource();
                try {
                        List<UserRepresentation> users = realm.users().list();
                        int deletedCount = 0;

                        for (UserRepresentation user : users) {
                                if (!ids.contains(user.getId())) {
                                        realm.users().delete(user.getId());
                                        deletedCount++;
                                }
                        }

                        Map<String, String> response = new HashMap<>();
                        response.put("message", deletedCount + " utilisateur(s) supprimé(s)");
                        return ResponseEntity.ok(response);
                } catch (DataIntegrityViolationException e) {
                        Map<String, String> response = new HashMap<>();
                        response.put("message", "Suppression impossible, les données sont utilisées ailleurs");
                        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
                } catch (Exception e) {
                        Map<String, String> response = new HashMap<>();
                        response.put("message", "Une erreur est survenue lors de la suppression: " + e.getMessage());
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
                }
        }

        @Operation(summary = "Delete by query parameters " + ENTITY_NAME + "s", responses = {
                        @ApiResponse(responseCode = "204", description = "No content"),
                        @ApiResponse(responseCode = "404", description = "Not found", content = @Content(mediaType = "ErrorResponse")),
                        @ApiResponse(responseCode = "403", description = "Permission denied", content = @Content(mediaType = "ErrorResponse"))
        })
        @DeleteMapping("/query")
        @PreAuthorize("hasAuthority('user:delete')")
        public ResponseEntity<Map<String, Object>> deleteByQueryParams(
                        @RequestParam(value = "filters", defaultValue = "") List<String> filters,
                        @RequestParam(value = "globalFilter", defaultValue = "") String globalFilter) {
                RealmResource realm = getRealmResource();
                List<String> deletedIds = new ArrayList<>();

                try {
                        List<UserRepresentation> users = realm.users().list();

                        List<UserRepresentation> filteredUsers;
                        if (!globalFilter.isEmpty()) {
                                filteredUsers = users.stream()
                                                .filter(user -> user.getUsername().contains(globalFilter) ||
                                                                (user.getFirstName() != null && user
                                                                                .getFirstName().contains(globalFilter))
                                                                ||
                                                                (user.getLastName() != null && user
                                                                                .getLastName().contains(globalFilter))
                                                                ||
                                                                (user.getEmail() != null && user.getEmail()
                                                                                .contains(globalFilter)))
                                                .collect(Collectors.toList());
                        } else {
                                filteredUsers = users;
                        }

                        if (filteredUsers.isEmpty()) {
                                Map<String, Object> response = new HashMap<>();
                                response.put("message", "Aucun utilisateur trouvé correspondant aux critères");
                                return ResponseEntity.ok(response);
                        }

                        for (UserRepresentation user : filteredUsers) {
                                realm.users().delete(user.getId());
                                deletedIds.add(user.getId());
                        }

                        Map<String, Object> response = new HashMap<>();
                        response.put("message", filteredUsers.size() + " utilisateur(s) supprimé(s)");
                        response.put("deletedIds", deletedIds);
                        return ResponseEntity.ok(response);
                } catch (DataIntegrityViolationException e) {
                        Map<String, Object> response = new HashMap<>();
                        response.put("message", "Suppression impossible, les données sont utilisées ailleurs");
                        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
                } catch (Exception e) {
                        Map<String, Object> response = new HashMap<>();
                        response.put("message", "Une erreur est survenue lors de la suppression: " + e.getMessage());
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
                }
        }

        @Operation(summary = "Delete by query parameters except ids " + ENTITY_NAME + "s", responses = {
                        @ApiResponse(responseCode = "204", description = "No content"),
                        @ApiResponse(responseCode = "404", description = "Not found", content = @Content(mediaType = "ErrorResponse")),
                        @ApiResponse(responseCode = "403", description = "Permission denied", content = @Content(mediaType = "ErrorResponse"))
        })
        @DeleteMapping("/query-exclude")
        @PreAuthorize("hasAuthority('user:delete')")
        public ResponseEntity<Map<String, Object>> deleteByQueryParamsExceptIds(
                        @RequestBody List<String> ids,
                        @RequestParam(value = "filters", defaultValue = "") List<String> filters,
                        @RequestParam(value = "globalFilter", defaultValue = "") String globalFilter) {
                RealmResource realm = getRealmResource();
                List<String> deletedIds = new ArrayList<>();

                try {
                        List<UserRepresentation> users = realm.users().list();

                        List<UserRepresentation> filteredUsers;
                        if (!globalFilter.isEmpty()) {
                                filteredUsers = users.stream()
                                                .filter(user -> user.getUsername().contains(globalFilter) ||
                                                                (user.getFirstName() != null && user.getFirstName()
                                                                                .contains(globalFilter))
                                                                ||
                                                                (user.getLastName() != null && user.getLastName()
                                                                                .contains(globalFilter))
                                                                ||
                                                                (user.getEmail() != null
                                                                                && user.getEmail().contains(
                                                                                                globalFilter)))
                                                .filter(user -> !ids.contains(user.getId()))
                                                .collect(Collectors.toList());
                        } else {
                                filteredUsers = users.stream()
                                                .filter(user -> !ids.contains(user.getId()))
                                                .collect(Collectors.toList());
                        }

                        if (filteredUsers.isEmpty()) {
                                Map<String, Object> response = new HashMap<>();
                                response.put("message", "Aucun utilisateur trouvé correspondant aux critères");
                                return ResponseEntity.ok(response);
                        }

                        for (UserRepresentation user : filteredUsers) {
                                realm.users().delete(user.getId());
                                deletedIds.add(user.getId());
                        }

                        Map<String, Object> response = new HashMap<>();
                        response.put("message", filteredUsers.size() + " utilisateur(s) supprimé(s)");
                        response.put("deletedIds", deletedIds);
                        return ResponseEntity.ok(response);
                } catch (DataIntegrityViolationException e) {
                        Map<String, Object> response = new HashMap<>();
                        response.put("message", "Suppression impossible, les données sont utilisées ailleurs");
                        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
                } catch (Exception e) {
                        Map<String, Object> response = new HashMap<>();
                        response.put("message", "Une erreur est survenue lors de la suppression: " + e.getMessage());
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
                }
        }

        private RealmResource getRealmResource() {
                Keycloak keycloak = keycloakConnectService.getKeyCloakAdminCli();
                if (keycloak == null) {
                        throw new KeycloakException("Keycloak connection is null");
                }
                RealmResource realmResource = keycloakRealmService.getRealmResource(keycloak,
                                realmName);
                return realmResource;
        }

        @Override
        protected <DTO> DTO mapToDto(UserRepresentation entity, Class<DTO> dtoClass) {
                return dtoClass.cast(userDtoMapper.mapToDto(entity));
        }
}