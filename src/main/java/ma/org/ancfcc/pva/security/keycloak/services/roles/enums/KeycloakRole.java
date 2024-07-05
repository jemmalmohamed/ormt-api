package ma.org.ancfcc.pva.security.keycloak.services.roles.enums;

import java.util.ArrayList;
import java.util.List;

public enum KeycloakRole {

    ADMIN_PVA("admin_pva"),
    AGENT_PVA("agent_pva"),
    RESPONSABLE_PVA("responsable_pva"),
    RESPONSABLE_EXTERNE("responsable_externe");

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