package ma.org.ormt.security.oauth;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

        @Value("${keycloak.clients.frontend.root-url}")
        private String originFrontendUrl;

        @Value("${spring.security.enabled}")
        private boolean securityEnabled;

        private final JwtAuthConverter jwtAuthConverter;

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

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
                if (securityEnabled) {
                        http
                                        .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                                        .csrf(csrf -> csrf.disable())
                                        .authorizeHttpRequests(authorizeHttpRequests -> authorizeHttpRequests
                                                        .requestMatchers(AUTH_SWAGGER_WHITELIST).permitAll()
                                                        .anyRequest().authenticated())
                                        .oauth2ResourceServer(oauth2ResourceServer -> oauth2ResourceServer
                                                        .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthConverter)))
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

        // @Bean
        // public SecurityFilterChain securityFilterChain(HttpSecurity http) throws
        // Exception {
        // if (securityEnabled) {
        // http
        // .cors(cors -> cors.configurationSource(corsConfigurationSource()))
        // .csrf(csrf -> csrf.disable())
        // .authorizeHttpRequests(authorizeHttpRequests -> authorizeHttpRequests
        // .requestMatchers(AUTH_SWAGGER_WHITELIST).permitAll()
        // .anyRequest().authenticated())
        // .oauth2ResourceServer(oauth2ResourceServer -> oauth2ResourceServer
        // .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthConverter)))
        // .sessionManagement(sessionManagement -> sessionManagement
        // .sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        // } else {
        // http
        // .cors(cors -> cors.configurationSource(corsConfigurationSource()))
        // .csrf(csrf -> csrf.disable())
        // .authorizeHttpRequests(authorizeHttpRequests -> authorizeHttpRequests
        // .anyRequest().permitAll());
        // }
        // return http.build();
        // }

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