package ma.org.ormt.security.core.config;

public final class SecurityEndpoints {

        private SecurityEndpoints() {
        }

        /**
         * Endpoints de documentation - Accès complet sans authentification
         */
        public static final String[] DOCUMENTATION_ENDPOINTS = {
                        "/v2/api-docs",
                        "/v3/api-docs",
                        "/v3/api-docs/**",
                        "/swagger-resources",
                        "/swagger-resources/**",
                        "/configuration/ui",
                        "/configuration/security",
                        "/webjars/**",
                        "/swagger-ui/**",
                        "/swagger-ui.html"
        };

        /**
         * Endpoints vraiment publics - Pas d'authentification requise
         */
        public static final String[] TRULY_PUBLIC_ENDPOINTS = {
                        "/api/v1/public/**",
                        "/api/v1/files/**",

        };

        /**
         * Endpoints nécessitant une authentification
         */
        public static final String[] AUTHENTICATED_ENDPOINTS = {
                        "/api/v1/files/**",
                        "/api/v1/**"
        };
}