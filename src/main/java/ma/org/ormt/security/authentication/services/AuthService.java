package ma.org.ormt.security.authentication.services;

import ma.org.ormt.security.authorization.dto.AuthorisationDto;

public interface AuthService {

    AuthorisationDto getCurrentUserAuthorities();

    public String getCurrentUserRole();

    boolean isAdmin();

    boolean isMaster();

}