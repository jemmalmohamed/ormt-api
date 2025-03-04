package ma.org.ormt.security.keycloak.services.roles.enums;

import java.util.ArrayList;
import java.util.List;

public enum KeycloakRole {

    admin_ormt("admin_ormt"),
    decideur_ormt("decideur_ormt"),
    public_ormt("public_ormt");

    private final String roleName;

    KeycloakRole(String roleName) {
        this.roleName = roleName;
    }

    public String getRoleName() {
        return roleName;
    }

    public static List<String> getRoleNames() {
        List<String> roleNames = new ArrayList<>();
        for (KeycloakRole role : KeycloakRole.values()) {
            roleNames.add(role.getRoleName());
        }
        return roleNames;
    }

}