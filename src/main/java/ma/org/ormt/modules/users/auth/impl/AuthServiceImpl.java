package ma.org.ormt.modules.users.auth.impl;

import org.keycloak.admin.client.Keycloak;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import ma.org.ormt.modules.users.auth.AuthService;
import ma.org.ormt.security.dtos.AuthorisationDto;
import ma.org.ormt.security.dtos.PermissionDto;
import ma.org.ormt.security.dtos.RoleDto;
import ma.org.ormt.security.keycloak.config.KeycloakConnectService;
import ma.org.ormt.security.keycloak.services.realm.KeycloakRealmService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
@Log4j2
public class AuthServiceImpl implements AuthService {

    private final KeycloakRealmService keycloakRealmService;
    private final KeycloakConnectService keycloakService;

    private static final String ROLE_PREFIX = "ROLE_";
    private static final String SCOPE_PREFIX = "SCOPE_";

    public AuthorisationDto getCurrentUserAuth() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        List<RoleDto> roles = new ArrayList<>();
        List<PermissionDto> permissions = new ArrayList<>();
        Collection<? extends GrantedAuthority> Authorities = authentication.getAuthorities();
        Authorities.forEach(authority -> {
            String auth = authority.getAuthority();
            if (auth.startsWith(ROLE_PREFIX)) {
                roles.add(createRoleDto(auth));
            } else if (!auth.startsWith(SCOPE_PREFIX)) {
                permissions.add(createResourcePermission(auth));
            }
        });

        return new AuthorisationDto(roles, permissions);
    }

    private RoleDto createRoleDto(String authority) {
        String roleName = authority.substring(ROLE_PREFIX.length());
        return new RoleDto(roleName);
    }

    private PermissionDto createResourcePermission(String authority) {
        String[] parts = authority.split(":");
        return new PermissionDto(
                parts[0],
                parts[1]);
    }

    public AuthorisationDto getAppRoles() {
        List<RoleDto> roles = new ArrayList<>();
        Keycloak keycloak = keycloakService.getKeyCloakAdminCli();
        keycloakRealmService.getClientResource(keycloak, "ormt", "ormt-api")
                .ifPresent(clientResource -> {
                    clientResource.roles().list().forEach(roleRepresentation -> {
                        if (roleRepresentation.getName().startsWith("role_")) {
                            roles.add(new RoleDto(roleRepresentation.getName()));
                        }
                    });
                });
        keycloak.tokenManager().logout();
        keycloak.close();

        return new AuthorisationDto(roles, null);
    }
}