package ma.org.ormt.security.core.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import ma.org.ormt.security.authentication.filters.PublicRoleAuthenticationFilter;
import ma.org.ormt.security.oauth.converters.JwtAuthResourceConverter;

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
        private final CorsConfigurationSource corsConfigurationSource;
        private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
        private final PublicRoleAuthenticationFilter publicRoleAuthenticationFilter;

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
                if (securityEnabled) {
                        return buildSecureFilterChain(http);
                } else {
                        return buildDevelopmentFilterChain(http);
                }
        }

        private SecurityFilterChain buildSecureFilterChain(HttpSecurity http) throws Exception {
                return http
                                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                                .csrf(csrf -> csrf.disable())
                                .sessionManagement(session -> session
                                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                                .addFilterBefore(publicRoleAuthenticationFilter, AnonymousAuthenticationFilter.class)
                                .oauth2ResourceServer(oauth2 -> oauth2
                                                .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthResourceConverter))
                                                .authenticationEntryPoint(customAuthenticationEntryPoint))
                                .authorizeHttpRequests(auth -> auth
                                                .requestMatchers(SecurityEndpoints.DOCUMENTATION_ENDPOINTS).permitAll()
                                                .requestMatchers(SecurityEndpoints.TRULY_PUBLIC_ENDPOINTS).permitAll()
                                                .requestMatchers("/api/v1/files/**").authenticated()
                                                .requestMatchers("/api/v1/**").authenticated()
                                                .anyRequest().authenticated())
                                .build();
        }

        private SecurityFilterChain buildDevelopmentFilterChain(HttpSecurity http) throws Exception {
                return http
                                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                                .csrf(csrf -> csrf.disable())
                                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
                                .build();
        }

}