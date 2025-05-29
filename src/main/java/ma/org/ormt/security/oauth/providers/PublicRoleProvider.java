package ma.org.ormt.security.oauth.providers;

import java.security.Policy;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.ClientResource;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.authorization.PolicyRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import ma.org.ormt.security.authentication.filters.PublicRoleAuthenticationFilter;
import ma.org.ormt.security.keycloak.services.KeycloakConnectService;
import ma.org.ormt.security.keycloak.services.authorization.policy.KeycloakPolicyService;
import ma.org.ormt.security.keycloak.services.realm.KeycloakRealmService;
import ma.org.ormt.security.keycloak.services.roles.client.KeycloakClientRoleService;
import ma.org.ormt.security.policy.dto.PolicyDto;
import ma.org.ormt.security.policy.dto.PolicyRoleDto;

/**
 * PublicRoleProvider - Composant de sécurité pour l'autorisation des
 * utilisateurs anonymes
 * 
 * Ce composant gère les permissions d'accès public pour les utilisateurs
 * anonymes en s'intégrant
 * avec Keycloak pour récupérer et mettre en cache les autorités du rôle public.
 * Il permet aux
 * utilisateurs anonymes d'accéder à des endpoints spécifiques basés sur les
 * permissions définies
 * dans le rôle "public" de Keycloak.
 * 
 * Architecture:
 * - Récupère les permissions depuis le rôle client "public" de Keycloak
 * - Extrait les autorités depuis les attributs de rôle et les politiques
 * d'autorisation
 * - Implémente un cache intelligent pour minimiser les appels API Keycloak
 * - Fournit des autorités de fallback pour la résilience du système
 * 
 * Modèle de sécurité:
 * - Les utilisateurs anonymes obtiennent ROLE_PUBLIC et ROLE_ANONYMOUS par
 * défaut
 * - Les permissions supplémentaires sont chargées depuis la configuration du
 * rôle Keycloak
 * - Les permissions sont mises en cache pendant 5 minutes pour équilibrer
 * fraîcheur et performance
 * 
 * Utilisation:
 * - Utilisé par PublicRoleAuthenticationFilter pour définir l'authentification
 * anonyme
 * - Supporte les annotations @PreAuthorize avec des vérifications de rôle
 * public
 * - Permet un contrôle d'accès fin pour les endpoints publics
 * 
 * Exigences de configuration:
 * - Realm Keycloak avec un client backend contenant le rôle "public"
 * - Attributs de rôle nommés "permission_*" pour les permissions personnalisées
 * - Politiques d'autorisation référençant le rôle public
 * 
 * @author Équipe ORMT
 * @since 1.0
 * @see PublicRoleAuthenticationFilter
 * @see SecurityConfig
 */
@Component
@RequiredArgsConstructor
@Log4j2
public class PublicRoleProvider {

    // Propriétés de configuration depuis application.yml
    @Value("${keycloak.realm}")
    private String realm;

    @Value("${keycloak.clients.backend.id}")
    private String clientId;

    // Dépendances - utilisation de la couche de service Keycloak existante
    private final KeycloakConnectService keycloakService; // Gestion des connexions Keycloak
    private final KeycloakRealmService keycloakRealmService; // Opérations sur le realm
    private final KeycloakClientRoleService keycloakClientRoleService; // Opérations sur les rôles
    private final KeycloakPolicyService keycloakPolicyService; // Opérations sur les politiques

    // Infrastructure de cache pour l'optimisation des performances
    private volatile Collection<GrantedAuthority> cachedPublicAuthorities;
    private volatile long lastCacheTime = 0;
    private static final long CACHE_TTL_MS = 300_000; // TTL du cache de 5 minutes
    private static final String CACHE_LOCK = "cacheLock"; // Verrou de synchronisation

    /**
     * Autorités par défaut fournies à tous les utilisateurs anonymes.
     * Elles servent de permissions de fallback quand Keycloak n'est pas disponible.
     */
    private static final Collection<GrantedAuthority> DEFAULT_AUTHORITIES = List.of(
            new SimpleGrantedAuthority("ROLE_PUBLIC"), // Accès public de base
            new SimpleGrantedAuthority("PUBLIC"), // Accès public de base

            new SimpleGrantedAuthority("ROLE_ANONYMOUS") // Identification utilisateur anonyme
    );

    /**
     * Initialise le provider avec les autorités par défaut et déclenche un
     * rafraîchissement
     * asynchrone du cache. Cela garantit que l'application peut démarrer même si
     * Keycloak
     * est temporairement indisponible.
     */
    @PostConstruct
    public void init() {
        // Initialisation avec des valeurs par défaut sécurisées
        this.cachedPublicAuthorities = new ArrayList<>(DEFAULT_AUTHORITIES);

        // Chargement asynchrone depuis Keycloak pour éviter de bloquer le démarrage
        CompletableFuture.runAsync(() -> {
            try {
                refreshPublicAuthoritiesCache();
                log.info("Cache des autorités publiques initialisé avec succès avec {} autorités",
                        cachedPublicAuthorities.size());
            } catch (Exception e) {
                log.error(
                        "Échec de l'initialisation du cache des autorités publiques, utilisation des valeurs par défaut",
                        e);
            }
        });
    }

    /**
     * Crée un AnonymousAuthenticationToken avec les autorités du rôle public.
     * Ce token représente un utilisateur anonyme avec des permissions d'accès
     * public.
     * 
     * @return Token d'authentification pour les utilisateurs anonymes avec
     *         autorités publiques
     */
    public Authentication createPublicAuthentication() {
        Collection<GrantedAuthority> authorities = getPublicAuthorities();

        // ✅ AJOUT: Log pour débogage
        log.info("Création de l'authentification publique avec {} autorités: {}",
                authorities.size(), authorities);
        if (log.isDebugEnabled()) {
            log.debug("Création de l'authentification publique avec {} autorités: {}",
                    authorities.size(), authorities);
        }

        return new AnonymousAuthenticationToken("public-anonymous-key", "anonymousUser", authorities);
    }

    /**
     * Récupère les autorités publiques avec mise en cache intelligente.
     * Retourne les autorités en cache si encore valides, sinon rafraîchit depuis
     * Keycloak.
     * 
     * Stratégie de cache:
     * - Retour immédiat si le cache est valide (dans la TTL)
     * - Rafraîchissement thread-safe utilisant le double-checked locking
     * - Fallback gracieux vers le cache existant en cas d'erreur
     * 
     * @return Collection de GrantedAuthority pour l'accès public
     */
    public Collection<GrantedAuthority> getPublicAuthorities() {
        long currentTime = System.currentTimeMillis();

        // Chemin rapide : retourner les autorités en cache si encore valides
        if (cachedPublicAuthorities != null && (currentTime - lastCacheTime) < CACHE_TTL_MS) {
            return cachedPublicAuthorities;
        }

        // Chemin lent : rafraîchir le cache avec sécurité thread
        synchronized (CACHE_LOCK) {
            // Vérification double pour éviter les rafraîchissements inutiles
            if (cachedPublicAuthorities != null && (currentTime - lastCacheTime) < CACHE_TTL_MS) {
                return cachedPublicAuthorities;
            }

            try {
                refreshPublicAuthoritiesCache();
            } catch (Exception e) {
                log.error("Échec du rafraîchissement du cache des autorités publiques: {}", e.getMessage(), e);
                // Résilience : s'assurer d'avoir toujours des autorités
                if (cachedPublicAuthorities == null) {
                    cachedPublicAuthorities = new ArrayList<>(DEFAULT_AUTHORITIES);
                }
            }
        }

        return cachedPublicAuthorities;
    }

    /**
     * Rafraîchit le cache des autorités en récupérant les permissions actuelles
     * depuis Keycloak.
     * Utilise les services existants pour l'intégration avec Keycloak.
     * 
     * Processus:
     * 1. Connexion à Keycloak via le client admin
     * 2. Localisation du client backend et de son rôle "public"
     * 3. Extraction des permissions depuis les attributs de rôle
     * 4. Extraction des permissions depuis les politiques d'autorisation
     * 5. Mise à jour du cache avec les autorités combinées
     * 
     * Gestion d'erreur:
     * - Dégradation gracieuse en cas de problème de connexion Keycloak
     * - Continue avec les autorités par défaut si rôle/client non trouvé
     * - Logs détaillés pour le dépannage
     */
    private void refreshPublicAuthoritiesCache() {
        log.debug("Rafraîchissement du cache des autorités publiques via les services existants");

        Collection<GrantedAuthority> authorities = new ArrayList<>(DEFAULT_AUTHORITIES);

        try (Keycloak keycloak = keycloakService.getKeyCloakAdminCli()) {

            // Étape 1: Localiser le client backend
            Optional<ClientResource> clientResourceOpt = keycloakRealmService.getClientResource(keycloak, realm,
                    clientId);

            if (clientResourceOpt.isEmpty()) {
                log.warn("Client '{}' non trouvé dans le realm '{}'", clientId, realm);
                updateCache(authorities);
                return;
            }

            ClientResource clientResource = clientResourceOpt.get();

            // Étape 2: Rechercher le rôle "public" dans le client
            Optional<RoleRepresentation> publicRoleOpt = keycloakClientRoleService.findRoleClientByName(clientResource,
                    "public");

            if (publicRoleOpt.isEmpty()) {
                log.warn("Rôle public non trouvé pour le client '{}'", clientId);
                updateCache(authorities);
                return;
            }

            RoleRepresentation publicRole = publicRoleOpt.get();

            // Étape 3: Extraction des permissions depuis différentes sources
            extractPermissionsFromRoleAttributes(authorities, publicRole);
            extractPermissionsFromPolicies(authorities, clientResource, publicRole.getId());

            log.info("Chargement réussi de {} autorités publiques depuis Keycloak",
                    authorities.size() - DEFAULT_AUTHORITIES.size());

        } catch (Exception e) {
            log.error("Erreur lors de la récupération des autorités depuis Keycloak: {}", e.getMessage(), e);
            // Continue avec les autorités par défaut pour la résilience
        }

        updateCache(authorities);
    }

    /**
     * Extrait les permissions depuis les attributs de rôle Keycloak.
     * 
     * Format d'attribut attendu:
     * - Noms d'attributs: "permission_*" (ex: "permission_files",
     * "permission_content")
     * - Valeurs d'attributs: Liste de chaînes de permissions (ex: ["files:read",
     * "files:list"])
     * 
     * @param authorities Collection pour ajouter les permissions extraites
     * @param publicRole  Représentation du rôle Keycloak contenant les attributs
     */
    private void extractPermissionsFromRoleAttributes(Collection<GrantedAuthority> authorities,
            RoleRepresentation publicRole) {
        Map<String, List<String>> attributes = publicRole.getAttributes();
        if (attributes == null || attributes.isEmpty()) {
            log.debug("Aucun attribut trouvé sur le rôle public");
            return;
        }

        log.debug("Traitement de {} attributs de rôle", attributes.size());

        attributes.entrySet().stream()
                .filter(entry -> entry.getKey().startsWith("permission_"))
                .flatMap(entry -> entry.getValue().stream())
                .filter(permission -> !permission.trim().isEmpty())
                .forEach(permission -> {
                    String cleanPermission = permission.trim();
                    authorities.add(new SimpleGrantedAuthority(cleanPermission));
                    log.trace("Permission ajoutée depuis l'attribut: {}", cleanPermission);
                });
    }

    /**
     * Extrait les permissions depuis les politiques d'autorisation Keycloak.
     * 
     * Processus:
     * 1. Récupère toutes les politiques pour le client
     * 2. Filtre les politiques basées sur les rôles
     * 3. Vérifie si la politique référence le rôle public
     * 4. Convertit les noms de politique en format de permission
     * 
     * Convention de nom de politique:
     * - "DOMAINE CREATE" → "domaine:create"
     * - "USER_PROFILE READ" → "userprofile:read"
     * 
     * @param authorities    Collection pour ajouter les permissions extraites
     * @param clientResource Ressource client Keycloak
     * @param publicRoleId   ID du rôle public
     */
    private void extractPermissionsFromPolicies(Collection<GrantedAuthority> authorities,
            ClientResource clientResource,
            String publicRoleId) {
        try {
            // Récupération de toutes les politiques d'autorisation pour ce client
            List<PolicyDto> allPolicies = keycloakPolicyService.getAllPolicies();

            allPolicies.stream()
                    .filter(policy -> "role".equals(policy.getType())) // Seulement les politiques basées sur les
                                                                       // rôles
                    .filter(policy -> policyContainsRole(policy, publicRoleId)) // Seulement les politiques référençant
                                                                                // le rôle public
                    .forEach(policy -> {
                        String policyName = policy.getName();
                        if (policyName != null) {
                            String permission = convertPolicyNameToPermission(policyName);
                            authorities.add(new SimpleGrantedAuthority(permission));
                            log.trace("Permission ajoutée depuis la politique: {} -> {}", policyName, permission);
                        }
                    });

        } catch (Exception e) {
            log.warn("Impossible d'extraire les permissions depuis les politiques: {}", e.getMessage());
        }
    }

    /**
     * Vérifie si une politique d'autorisation référence le rôle spécifié.
     * 
     * @param policy Représentation de politique sous forme de Map
     * @param roleId ID du rôle à vérifier
     * @return true si la politique contient le rôle, false sinon
     */
    private boolean policyContainsRole(PolicyDto policy, String roleId) {
        try {
            List<PolicyRoleDto> roles = policy.getRoles();

            if (roles == null) {
                return false;
            }

            return roles.stream()
                    .anyMatch(role -> roleId.equals(role.getId()));

        } catch (Exception e) {
            log.trace("Erreur lors de la vérification des références de rôle de politique: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Convertit les noms de politique Keycloak en format de permission standardisé.
     * 
     * Règles de conversion:
     * - Les espaces deviennent des deux-points: "USER READ" → "user:read"
     * - Les underscores sont supprimés: "USER_PROFILE" → "userprofile"
     * - Tout est en minuscules
     * 
     * @param policyName Nom de politique original depuis Keycloak
     * @return Chaîne de permission standardisée
     */
    private String convertPolicyNameToPermission(String policyName) {
        return policyName.toLowerCase()
                .replace(" ", ":")
                .replace("_", "");
    }

    /**
     * Mise à jour thread-safe du cache avec synchronisation appropriée.
     * 
     * @param authorities Nouvelles autorités à mettre en cache
     */
    private void updateCache(Collection<GrantedAuthority> authorities) {
        this.cachedPublicAuthorities = new ArrayList<>(authorities);
        this.lastCacheTime = System.currentTimeMillis();

        log.debug("Cache des autorités publiques mis à jour avec {} autorités: {}",
                authorities.size(), authorities);
    }

    // === Méthodes d'administration et de surveillance ===

    /**
     * Force un rafraîchissement immédiat du cache en contournant le TTL.
     * Utile pour les opérations administratives ou les changements de
     * configuration.
     */
    public void forceRefreshCache() {
        log.info("Rafraîchissement forcé du cache des autorités publiques");
        synchronized (CACHE_LOCK) {
            lastCacheTime = 0; // Force le rafraîchissement en invalidant le timestamp
            refreshPublicAuthoritiesCache();
        }
    }

    /**
     * Vérifie si le cache actuel est encore valide.
     * 
     * @return true si le cache est dans le TTL, false s'il a expiré
     */
    public boolean isCacheValid() {
        long currentTime = System.currentTimeMillis();
        return cachedPublicAuthorities != null && (currentTime - lastCacheTime) < CACHE_TTL_MS;
    }

    /**
     * Fournit des statistiques de cache pour la surveillance et le débogage.
     * 
     * @return Map contenant la taille du cache, l'heure du dernier
     *         rafraîchissement, la validité et le TTL
     */
    public Map<String, Object> getCacheStats() {
        return Map.of(
                "cacheSize", cachedPublicAuthorities != null ? cachedPublicAuthorities.size() : 0,
                "lastRefresh", new java.util.Date(lastCacheTime),
                "isValid", isCacheValid(),
                "ttlMs", CACHE_TTL_MS);
    }
}