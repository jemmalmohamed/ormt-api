package ma.org.ormt.modules.users.users.services;

import org.keycloak.admin.client.resource.ClientResource;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;

import ma.org.ormt.modules.users.users.dtos.request.UserRequestDto;

public interface UserService {

    // Optional<UserRepresentation> findByNom(String nom);

    // Page<UserRepresentation> getEntityList(QueryParams requestParams);

    // public Page<UserRepresentation> getEntitiesByIds(List<Long> ids, QueryParams
    // params);

    UserRepresentation create(UserRequestDto requestDto) throws Exception;

    void assignRoleToUser(RealmResource realmResource,
            ClientResource clientResource, RoleRepresentation roleRepresentation,
            UserRepresentation userRepresentation);

    // UserRepresentation update(Long id, UserRequestDto userRequestDto) throws
    // Exception;

    // UserRepresentation save(UserRepresentation user);

    // boolean existsById(Long id);

    // void attachDomaine(Long userId, Long domaineId);

    // void detachDomaine(Long eppaceDomaineId);

}