package ma.org.ormt.security.services.impl;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import ma.org.ormt.security.dtos.AuthorisationDto;
import ma.org.ormt.security.dtos.PermissionDto;
import ma.org.ormt.security.dtos.RoleDto;
import ma.org.ormt.security.services.AuthService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
public class AuthServiceImpl implements AuthService {

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
}