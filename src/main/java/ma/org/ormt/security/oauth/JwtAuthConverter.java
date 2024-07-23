package ma.org.ormt.security.oauth;

import org.keycloak.authorization.client.AuthzClient;

import org.keycloak.representations.idm.authorization.AuthorizationResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimNames;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.stereotype.Component;

import lombok.extern.log4j.Log4j2;
import ma.org.ormt.core.exceptions.handlers.KeycloakException;
import ma.org.ormt.security.keycloak.config.KeycloakAuthzService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@Log4j2
public class JwtAuthConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    private final JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();

    @Autowired
    private KeycloakAuthzService keycloakAuthzService;

    @Autowired
    private JwtDecoder jwtDecoder;

    @Value("${jwt.auth.converter.principle-attribute}")
    private String principleAttribute;

    @Value("${keycloak.clients.backend.id}")
    private String clientId;

    @Override
    public AbstractAuthenticationToken convert(@NonNull Jwt jwt) {

        Collection<GrantedAuthority> authorities = jwtGrantedAuthoritiesConverter.convert(jwt);
        if (authorities == null) {
            authorities = new ArrayList<>();
        }

        authorities.addAll(extractResourceRoles(jwt));
        log.info("authorities: {}", authorities);
        return new JwtAuthenticationToken(
                jwt,
                authorities,
                getPrincipleClaimName(jwt));
    }

    private String getPrincipleClaimName(Jwt jwt) {
        String claimName = JwtClaimNames.SUB;
        if (principleAttribute != null) {
            claimName = principleAttribute;
        }

        jwt.getClaimAsString(principleAttribute);

        return jwt.getClaim(claimName);
    }

    @SuppressWarnings("unchecked")
    private Collection<? extends GrantedAuthority> extractResourceRoles(Jwt jwt) {

        Optional<Map<String, Object>> resourceAccessOpt = Optional.ofNullable(jwt.getClaim("resource_access"));
        log.info("resourceAccess: {}", resourceAccessOpt);
        if (resourceAccessOpt.isEmpty()) {
            return Set.of();
        }

        Map<String, Object> resourceAccess = resourceAccessOpt.get();

        Optional<Map<String, Object>> resourceOpt = Optional
                .ofNullable((Map<String, Object>) resourceAccess.get(clientId));
        if (resourceOpt.isEmpty()) {
            return Set.of();
        }

        Map<String, Object> resource = resourceOpt.get();
        log.info("resource: {}", resource);
        Collection<String> resourceRoles = safelyCastToCollection(resource.get("roles"));

        Jwt rpt = getAuthorizeRequestRpt(jwt);
        Map<String, Object> authorization = rpt.getClaim("authorization");

        Collection<Map<String, Object>> permissions = (Collection<Map<String, Object>>) authorization
                .get("permissions");

        Collection<String> resourcePermissions = extractResourcePermissions(permissions);

        return combineResourceRolesAndPermissions(resourceRoles, resourcePermissions);

    }

    private Jwt getAuthorizeRequestRpt(Jwt accessToken) {
        try {

            String token = accessToken.getTokenValue();
            log.info("token: {}", token);
            AuthzClient authzClient = keycloakAuthzService.createAuthzClient();
            log.info(authzClient.obtainAccessToken());
            AuthorizationResponse authResponse = authzClient.authorization(token).authorize();
            log.info(token, authResponse);
            String rpt = authResponse.getToken();
            log.info("rpt: {}", rpt);
            return jwtDecoder.decode(rpt);

        } catch (Exception e) {
            log.error("Authorization error: {}", e.getCause());
            throw new KeycloakException(clientId + " authorization error");
        }
    }

    private Collection<String> extractResourcePermissions(Collection<Map<String, Object>> permissions) {
        return permissions.stream()
                .flatMap(permission -> {
                    Collection<?> scopes = safelyCastToCollection(permission.get("scopes"));
                    Object rsname = permission.get("rsname");
                    if (scopes == null || rsname == null) {
                        return Stream.empty(); // Skip if scopes or rsname is null
                    }
                    return scopes.stream()
                            .map(scope -> rsname + ":" + scope);
                })
                .collect(Collectors.toList());
    }

    @SuppressWarnings("unchecked")
    private Collection<String> safelyCastToCollection(Object object) {
        try {
            return (Collection<String>) object;
        } catch (ClassCastException e) {
            log.error("Error casting to Collection: ", e);
            return new ArrayList<>();
        }
    }

    private Set<GrantedAuthority> combineResourceRolesAndPermissions(Collection<String> resourceRoles,
            Collection<String> resourcePermissions) {
        return Stream.concat(
                resourceRoles.stream()
                        .map(role -> new SimpleGrantedAuthority("ROLE_" + role)),
                resourcePermissions.stream()
                        .map(SimpleGrantedAuthority::new))
                .collect(Collectors.toSet());
    }
}
