package ma.org.ormt.modules.users.auth;

import ma.org.ormt.security.dtos.AuthorisationDto;

public interface AuthService {

    AuthorisationDto getCurrentUserAuth();

    AuthorisationDto getAppRoles();

}