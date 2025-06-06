package ma.org.ormt.security.oauth.converters;

import org.keycloak.representations.idm.authorization.AuthorizationResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.convert.converter.Converter;
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
import ma.org.ormt.security.keycloak.services.KeycloakConnectService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@Log4j2
public class JwtAuthResourceConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    private final JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();

    // @Autowired
    // private AuthzClient authzClient;
    @Autowired
    private KeycloakConnectService keycloakService;

    @Autowired
    private JwtDecoder jwtDecoder;

    @Value("${jwt.auth.converter.principle-attribute}")
    private String principleAttribute;

    @Value("${keycloak.auth-server-url}")
    private String keycloakServerUrl;

    @Value("${keycloak.realm}")
    private String realm;

    @Value("${keycloak.clients.backend.id}")
    private String clientId;

    @Value("${keycloak.admin.username}")
    private String adminUser;

    @Value("${keycloak.admin.password}")
    private String adminPassword;

    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {

        Collection<GrantedAuthority> authorities = Stream.concat(
                jwtGrantedAuthoritiesConverter.convert(jwt).stream(),
                extractResourceRoles(jwt).stream()).collect(Collectors.toSet());

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

        jwt.getClaimAsString("preferred_username");

        return jwt.getClaim(claimName);
    }

    @SuppressWarnings("unchecked")
    private Collection<? extends GrantedAuthority> extractResourceRoles(Jwt jwt) {

        Optional<Map<String, Object>> resourceAccessOpt = Optional.ofNullable(jwt.getClaim("resource_access"));

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
        log.debug("resource: {}", resource);
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

            AuthorizationResponse authResponse = keycloakService.getAuthzClient().authorization(token).authorize();

            String rpt = authResponse.getToken();

            Jwt rptAsJwt = jwtDecoder.decode(rpt);

            return rptAsJwt;

        } catch (Exception e) {
            log.error("Authorization error: {}", e.getCause());
            throw new RuntimeException(e);
        }
    }

    private Collection<String> extractResourcePermissions(Collection<Map<String, Object>> permissions) {
        return permissions.stream()
                .filter(permission -> permission.get("scopes") != null && permission.get("rsname") != null)
                .flatMap(permission -> {
                    Collection<String> scopes = safelyCastToCollection(permission.get("scopes"));
                    return scopes != null
                            ? scopes.stream().map(scope -> permission.get("rsname") + ":" + scope)
                            : Stream.empty();
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
                        .map(role -> new SimpleGrantedAuthority("ROLE_" + role.toUpperCase())),
                resourcePermissions.stream()
                        .map(SimpleGrantedAuthority::new))
                .collect(Collectors.toSet());
    }
}
