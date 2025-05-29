package ma.org.ormt.security.authentication.services.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import ma.org.ormt.security.authentication.services.AuthService;
import ma.org.ormt.security.authorization.dto.AuthorisationDto;
import ma.org.ormt.security.authorization.dto.PermissionDto;
import ma.org.ormt.security.keycloak.services.roles.client.KeycloakClientRoleService;
import ma.org.ormt.security.roles.dto.RoleDto;

@Service
@RequiredArgsConstructor
@Log4j2
public class AuthServiceImpl implements AuthService {

    @Value("${keycloak.realm}")
    private String realmName;

    @Value("${keycloak.clients.backend.id}")
    private String backendClientName;

    public final KeycloakClientRoleService keycloakClientRoleService;

    private static final String ROLE_PREFIX = "ROLE_";
    private static final String SCOPE_PREFIX = "SCOPE_";

    public AuthorisationDto getCurrentUserAuthorities() {
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

    @Override
    public boolean isAdmin() {
        return SecurityContextHolder.getContext().getAuthentication().getAuthorities()
                .stream()
                .anyMatch(auth -> auth.getAuthority().equals(ROLE_PREFIX + "ADMIN"));
    }

    @Override
    public boolean isMaster() {
        return SecurityContextHolder.getContext().getAuthentication().getAuthorities()
                .stream()
                .anyMatch(auth -> auth.getAuthority().equals(ROLE_PREFIX + "MASTER"));
    }

    @Override
    public String getCurrentUserRole() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return "role_public";
        }

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        for (GrantedAuthority authority : authorities) {
            String authorityName = authority.getAuthority();
            if (authorityName.startsWith("ROLE_")) {
                return authorityName.replace("ROLE_", "role_").toLowerCase();
            }
        }

        return "role_public";
    }

    private RoleDto createRoleDto(String authority) {
        String roleName = authority.substring(ROLE_PREFIX.length());
        RoleDto roleDto = new RoleDto();
        roleDto.setName(roleName.toLowerCase());
        return roleDto;
    }

    private PermissionDto createResourcePermission(String authority) {
        String[] parts = authority.split(":");
        return new PermissionDto(
                parts[0],
                parts[1]);
    }

}