package ma.org.ormt.security.services;

import ma.org.ormt.security.dtos.AuthorisationDto;

public interface AuthService {

    AuthorisationDto getCurrentUserAuth();

}