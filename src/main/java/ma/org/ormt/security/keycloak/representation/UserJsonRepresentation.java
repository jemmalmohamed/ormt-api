package ma.org.ormt.security.keycloak.representation;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UserJsonRepresentation {

    protected String self; // link
    protected String id;
    protected String origin;
    protected Long createdTimestamp;
    protected String username;
    protected Boolean enabled;
    protected Boolean totp;
    protected Boolean emailVerified;
    protected String firstName;
    protected String lastName;
    protected String email;
    protected String federationLink;
    protected String serviceAccountClientId;
    protected String password;
    List<String> clientRoles;

}