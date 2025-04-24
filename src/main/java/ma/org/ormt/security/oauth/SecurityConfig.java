package ma.org.ormt.security.oauth;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
@Log4j2
public class SecurityConfig {

        @Value("${keycloak.clients.frontend.root-url}")
        private String originFrontendUrl;

        @Value("${spring.security.enabled}")
        private boolean securityEnabled;

        private final JwtAuthResourceConverter jwtAuthResourceConverter;
        private final PublicRoleProvider publicRoleProvider;
        private final PublicRoleAuthenticationFilter publicRoleAuthenticationFilter;

        private static final String[] AUTH_SWAGGER_WHITELIST = {
                        "/v2/api-docs",
                        "/v3/api-docs",
                        "/v3/api-docs/**",
                        "/swagger-resources",
                        "/swagger-resources/**",
                        "/configuration/ui",
                        "/configuration/security",
                        "/webjars/**",
                        "/swagger-ui/**",
                        "/swagger-ui.html" };

        private static final String[] PUBLIC_ENDPOINTS = {
                        "/api/v1/public/**",
                        "/api/v1/**" // Allow anonymous access to all API endpoints, method security will handle
                                     // authorization
        };

        /**
         * Custom AuthenticationEntryPoint to handle unauthenticated requests
         * This ensures anonymous users still get proper access while logging the
         * authentication failure
         */
        @Bean
        public AuthenticationEntryPoint customAuthenticationEntryPoint() {
                return new AuthenticationEntryPoint() {
                        @Override
                        public void commence(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException authException) throws IOException, ServletException {
                                log.debug("Authentication failed: {}", authException.getMessage());
                                // For API endpoints that require authentication, return 401
                                // But let Spring Security's anonymous authentication handle permitted endpoints
                                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                        }
                };
        }

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
                if (securityEnabled) {
                        http
                                        .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                                        .csrf(csrf -> csrf.disable())
                                        // Configure anonymous authentication with public role permissions
                                        .anonymous(anonymous -> {
                                                // Get all public authorities from our provider
                                                String[] authorities = publicRoleProvider.getPublicAuthorities()
                                                                .stream()
                                                                .map(auth -> auth.getAuthority())
                                                                .toArray(String[]::new);

                                                anonymous.authorities(authorities)
                                                                .principal("anonymousUser");
                                        })
                                        // Add our public role filter to handle anonymous requests correctly
                                        .addFilterBefore(publicRoleAuthenticationFilter,
                                                        AnonymousAuthenticationFilter.class)
                                        .authorizeHttpRequests(authorizeHttpRequests -> authorizeHttpRequests
                                                        .requestMatchers(AUTH_SWAGGER_WHITELIST).permitAll()
                                                        .requestMatchers(PUBLIC_ENDPOINTS).permitAll()
                                                        .anyRequest()
                                                        .authenticated())
                                        .oauth2ResourceServer(oauth2ResourceServer -> oauth2ResourceServer
                                                        .jwt(jwt -> jwt.jwtAuthenticationConverter(
                                                                        jwtAuthResourceConverter))
                                                        .authenticationEntryPoint(customAuthenticationEntryPoint()))
                                        .sessionManagement(sessionManagement -> sessionManagement
                                                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS));
                } else {
                        http
                                        .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                                        .csrf(csrf -> csrf.disable())
                                        .authorizeHttpRequests(authorizeHttpRequests -> authorizeHttpRequests
                                                        .anyRequest().permitAll());
                }
                return http.build();
        }

        CorsConfigurationSource corsConfigurationSource() {
                CorsConfiguration configuration = new CorsConfiguration();
                configuration.setAllowedOrigins(Arrays.asList(originFrontendUrl));
                configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
                configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));
                configuration.setAllowCredentials(true);

                UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
                source.registerCorsConfiguration("/**", configuration);
                return source;
        }

}