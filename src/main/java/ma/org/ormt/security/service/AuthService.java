package ma.org.ormt.security.service;

import ma.org.ormt.security.model.AuthorisationDto;

public interface AuthService {

    AuthorisationDto getCurrentUserAuth();

}