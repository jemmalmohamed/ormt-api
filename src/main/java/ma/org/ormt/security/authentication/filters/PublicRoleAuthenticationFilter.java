package ma.org.ormt.security.authentication.filters;

import java.io.IOException;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import ma.org.ormt.security.oauth.providers.PublicRoleProvider;

/**
 * Filtre pour garantir que les requêtes anonymes obtiennent les permissions de
 * rôle public
 * appropriées lorsqu'aucun token JWT n'est présent.
 * 
 * Ce filtre s'exécute avant AnonymousAuthenticationFilter pour injecter des
 * autorités
 * publiques dynamiques depuis Keycloak dans le contexte de sécurité pour les
 * utilisateurs anonymes.
 * 
 * Optimisations:
 * - Évite le traitement des ressources statiques
 * - Cache l'authentification publique pour éviter les créations répétées
 * - Gestion d'erreur gracieuse sans casser la chaîne de filtres
 */
@Component
@RequiredArgsConstructor
@Log4j2
public class PublicRoleAuthenticationFilter extends OncePerRequestFilter {

    private final PublicRoleProvider publicRoleProvider;

    // Cache de l'authentification publique pour éviter les créations répétées
    private volatile Authentication cachedPublicAuth;
    private volatile long lastCacheTime = 0;
    private static final long CACHE_DURATION_MS = 300_000; // 5 minutes

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        // Éviter le filtrage pour les ressources statiques et les endpoints actuator
        String path = request.getRequestURI();
        return path.startsWith("/actuator/") ||
                path.startsWith("/swagger-") ||
                path.startsWith("/webjars/") ||
                path.matches(".*\\.(css|js|ico|png|jpg|gif|woff|woff2|ttf|svg)$");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String requestUri = request.getRequestURI();
        String method = request.getMethod();

        try {
            Authentication existingAuth = SecurityContextHolder.getContext().getAuthentication();

            if (shouldApplyPublicAuthentication(existingAuth, request)) {
                if (log.isTraceEnabled()) {
                    log.trace("Application de l'authentification publique pour {} {}", method, requestUri);
                }

                Authentication publicAuth = getOrCreatePublicAuthentication();
                if (publicAuth != null) {
                    SecurityContextHolder.getContext().setAuthentication(publicAuth);

                    if (log.isDebugEnabled()) {
                        log.debug("Authentification publique définie pour {} {} avec autorités: {}",
                                method, requestUri, publicAuth.getAuthorities());
                    }
                }
            } else {
                log.trace("Passage de l'authentification publique pour {} {} - auth existante: {}",
                        method, requestUri, existingAuth != null ? existingAuth.getClass().getSimpleName() : "aucune");
            }

        } catch (Exception e) {
            log.error("Erreur lors du traitement de l'authentification publique pour {} {}: {}",
                    method, requestUri, e.getMessage(), e);

            // Ne pas faire échouer la requête - laisser Spring Security gérer
            // l'autorisation correctement
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Détermine si l'authentification publique doit être appliquée
     */
    private boolean shouldApplyPublicAuthentication(Authentication existingAuth, HttpServletRequest request) {
        // Passer si déjà authentifié avec un utilisateur réel
        if (existingAuth != null && existingAuth.isAuthenticated() &&
                !isAnonymousAuthentication(existingAuth)) {
            return false;
        }

        // Passer pour certains chemins qui n'ont pas besoin de rôles publics
        String requestPath = request.getRequestURI();
        if (isExcludedPath(requestPath)) {
            return false;
        }

        return true;
    }

    /**
     * Vérifie si l'authentification est anonyme
     */
    private boolean isAnonymousAuthentication(Authentication auth) {
        return auth instanceof AnonymousAuthenticationToken ||
                "anonymousUser".equals(auth.getPrincipal()) ||
                auth.getPrincipal() == null;
    }

    /**
     * Chemins qui n'ont pas besoin de traitement de rôle public
     */
    private boolean isExcludedPath(String path) {
        return path.startsWith("/actuator/") ||
                path.startsWith("/swagger-") ||
                path.startsWith("/v3/api-docs") ||
                path.endsWith(".css") ||
                path.endsWith(".js") ||
                path.endsWith(".ico");
    }

    /**
     * Obtient ou crée l'authentification publique avec mise en cache
     */
    private Authentication getOrCreatePublicAuthentication() {
        long currentTime = System.currentTimeMillis();

        // Utiliser la version en cache si encore valide
        if (cachedPublicAuth != null && (currentTime - lastCacheTime) < CACHE_DURATION_MS) {
            return cachedPublicAuth;
        }

        synchronized (this) {
            // Vérification double du verrouillage
            if (cachedPublicAuth != null && (currentTime - lastCacheTime) < CACHE_DURATION_MS) {
                return cachedPublicAuth;
            }

            try {
                cachedPublicAuth = publicRoleProvider.createPublicAuthentication();
                lastCacheTime = currentTime;

                log.debug("Nouvelle authentification publique créée avec autorités: {}",
                        cachedPublicAuth.getAuthorities());

                return cachedPublicAuth;
            } catch (Exception e) {
                log.error("Échec de la création de l'authentification publique: {}", e.getMessage(), e);
                return null;
            }
        }
    }
}