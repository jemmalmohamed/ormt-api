package ma.org.ormt.modules.users.auth;

import ma.org.ormt.security.dtos.AuthorisationDto;
import ma.org.ormt.security.dtos.CreateRoleRequestDto;
import ma.org.ormt.security.dtos.PermissionDto;
import ma.org.ormt.security.dtos.ResourceDto;
import ma.org.ormt.security.dtos.RoleWithPermissionsDto;

import java.util.List;
import java.util.Map;

public interface AuthService {

    AuthorisationDto getCurrentUserAuth();

    AuthorisationDto getAppRoles();

    List<ResourceDto> getResourcesWithPermissions();

    List<RoleWithPermissionsDto> getRolesWithPermissions();

    List<PermissionDto> getAllPermissions();

    RoleWithPermissionsDto createRole(CreateRoleRequestDto request);

    List<Map<String, Object>> getAllPolicies();

    boolean assignRoleToPolicy(String policyId, String roleName);

    /**
     * Assign a role to multiple policies (role policy type) in Keycloak.
     * 
     * @param policyIds the list of policy IDs
     * @param roleName  the name of the role to assign
     * @return true if all assignments succeed, false otherwise
     */
    boolean assignRoleToPolicies(List<String> policyIds, String roleName);
}