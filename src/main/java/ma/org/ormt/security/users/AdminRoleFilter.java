package ma.org.ormt.security.users;

import java.util.Collection;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

public class AdminRoleFilter {

    @Override
    public boolean equals(Object obj) {

        if (obj == null) {

            return true;
        }

        // Get current authentication
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null) {
            return true;
        }

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

        // Check if user has ROLE_ADMIN authority - try different forms of admin role
        boolean isAdmin = authorities.contains(new SimpleGrantedAuthority("ROLE_ADMIN"));
        boolean isMaster = authorities.contains(new SimpleGrantedAuthority("ROLE_MASTER"));

        if (isAdmin || isMaster) {
            return false; // Return false to include the field for admins and masters
        }

        return true; // Return true to exclude the field for non-admins and non-masters
    }
}
