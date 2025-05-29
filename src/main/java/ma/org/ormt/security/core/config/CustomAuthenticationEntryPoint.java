package ma.org.ormt.security.core.config;

import java.io.IOException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;

@Component
@Log4j2
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException authException) throws IOException {

        String requestUri = request.getRequestURI();
        String method = request.getMethod();

        log.warn("Échec d'authentification pour {} {}: {}", method, requestUri,
                authException.getMessage());

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        String errorResponse = String.format(
                "{\"error\":\"unauthorized\",\"message\":\"Authentification requise pour %s %s\",\"timestamp\":\"%s\"}",
                method, requestUri, java.time.Instant.now());

        response.getWriter().write(errorResponse);
    }
}