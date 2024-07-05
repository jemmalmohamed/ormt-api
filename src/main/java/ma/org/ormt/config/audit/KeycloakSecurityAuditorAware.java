package ma.org.ormt.config.audit;

import org.springframework.data.domain.AuditorAware;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

public class KeycloakSecurityAuditorAware implements AuditorAware<String> {

    static final String DEFAULT_AUDITOR = "system"; // Set your desired default auditor value here

    @Override
    public @NonNull Optional<String> getCurrentAuditor() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();
            return Optional.of(username);
        }
        return Optional.of(DEFAULT_AUDITOR);

    }
}