package ma.org.ormt.security.oauth;

import java.io.IOException;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

/**
 * Filter to ensure anonymous requests get proper public role permissions
 * when no JWT token is present.
 */
@Component
@RequiredArgsConstructor
@Log4j2
public class PublicRoleAuthenticationFilter extends OncePerRequestFilter {

    private final PublicRoleProvider publicRoleProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // Check if there's no authentication or it's anonymous
        if (SecurityContextHolder.getContext().getAuthentication() == null ||
                SecurityContextHolder.getContext().getAuthentication().getPrincipal().equals("anonymousUser")) {

            log.debug("Setting public role authentication for anonymous request to: {}", request.getRequestURI());

            // Set our custom anonymous authentication with public role permissions
            SecurityContextHolder.getContext().setAuthentication(
                    publicRoleProvider.createPublicAuthentication());
        }

        filterChain.doFilter(request, response);
    }
}