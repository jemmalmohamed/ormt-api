package ma.org.ormt.security.authentication.services;

import java.util.List;
import java.util.Map;

import ma.org.ormt.security.authorization.dto.AuthorisationDto;
import ma.org.ormt.security.authorization.dto.PermissionDto;
import ma.org.ormt.security.authorization.dto.RoleWithPermissionsDto;
import ma.org.ormt.security.roles.dto.CreateRoleRequestDto;

public interface AuthService {

    AuthorisationDto getCurrentUserAuth();

    AuthorisationDto getAppRoles();

    List<RoleWithPermissionsDto> getRolesWithPermissions();

    List<PermissionDto> getAllPermissions();

    RoleWithPermissionsDto createRole(CreateRoleRequestDto request);

    List<Map<String, Object>> getAllPolicies();

}