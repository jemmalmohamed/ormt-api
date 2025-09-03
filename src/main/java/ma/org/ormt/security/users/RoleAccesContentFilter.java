package ma.org.ormt.security.users;

import java.util.Collection;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import ma.org.ormt.modules.roleacces.dtos.summary.RoleAccesSummaryDto;

/**
 * Jackson content filter used with @JsonInclude(content = Include.CUSTOM,
 * contentFilter = RoleAccesContentFilter.class)
 *
 * Behavior:
 * - For ADMIN/MASTER users: keep all list elements (never filter).
 * - For others: keep only the RoleAccesSummaryDto whose roleCode matches the
 * current authenticated user's role (e.g., "role_decideur").
 * - For anonymous users: defaults to "role_public".
 *
 * Note: equals() must return true to have Jackson suppress (exclude) a value.
 */
public class RoleAccesContentFilter {

    @Override
    public boolean equals(Object obj) {
        // Null elements are not expected in lists; if present, drop them
        if (obj == null) {
            return true; // suppress null element
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            // Keep only public role when unauthenticated
            if (obj instanceof RoleAccesSummaryDto dto) {
                return !"role_public".equalsIgnoreCase(dto.getRoleCode());
            }
            return false; // not a RoleAccesSummaryDto, don't suppress
        }

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

        boolean isAdmin = authorities.contains(new SimpleGrantedAuthority("ROLE_ADMIN"));
        boolean isMaster = authorities.contains(new SimpleGrantedAuthority("ROLE_MASTER"));
        if (isAdmin || isMaster) {
            return false; // never suppress for admin/master
        }

        // Derive current user's role code: ROLE_X -> role_x
        String currentRoleCode = authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .filter(auth -> auth != null && auth.startsWith("ROLE_"))
                .findFirst()
                .map(auth -> auth.replace("ROLE_", "role_").toLowerCase())
                .orElse("role_public");

        if (obj instanceof RoleAccesSummaryDto dto) {
            // Suppress any element whose roleCode does NOT match current role
            return !currentRoleCode.equalsIgnoreCase(dto.getRoleCode());
        }

        // If not the expected type, don't suppress
        return false;
    }
}
