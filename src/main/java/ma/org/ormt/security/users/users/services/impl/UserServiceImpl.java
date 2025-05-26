package ma.org.ormt.security.users.users.services.impl;

import java.util.List;

import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.ClientResource;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.log4j.Log4j2;
import ma.org.ormt.core.exceptions.handlers.KeycloakException;
import ma.org.ormt.core.validators.ObjectsValidator;
import ma.org.ormt.security.keycloak.services.KeycloakConnectService;
import ma.org.ormt.security.keycloak.services.realm.KeycloakRealmService;
import ma.org.ormt.security.keycloak.services.roles.client.KeycloakClientRoleService;
import ma.org.ormt.security.keycloak.services.users.KeycloakUserService;
import ma.org.ormt.security.users.users.dtos.request.UserRequestDto;
import ma.org.ormt.security.users.users.services.UserService;

@Service
@Transactional
@Log4j2
public class UserServiceImpl implements UserService {

    @Value("${keycloak.realm}")
    private String realmName;

    @Value("${keycloak.clients.backend.id}")
    private String backendClientName;

    @Autowired
    private KeycloakUserService keycloakUserService;

    @Autowired
    private KeycloakConnectService keycloakConnectService;

    @Autowired
    private KeycloakRealmService keycloakRealmService;
    @Autowired
    private KeycloakClientRoleService keycloakClientRoleService;

    @Autowired
    private ObjectsValidator<UserRequestDto> validator;

    @Override
    public UserRepresentation create(UserRequestDto requestDto) throws Exception {
        // Validate the request DTO
        validator.validate(requestDto);

        // Get realm resource
        RealmResource realmResource = getRealmResource();

        ClientResource backendClientResource = realmResource.clients()
                .get(backendClientName);

        // Check if user exists and delete it (similar to seeder approach)
        if (keycloakUserService.userExists(realmResource, requestDto.getUsername())) {

        }

        // Create user representation
        UserRepresentation userRepresentation = new UserRepresentation();
        userRepresentation.setUsername(requestDto.getUsername());
        userRepresentation.setFirstName(requestDto.getFirstName());
        userRepresentation.setLastName(requestDto.getLastName());
        userRepresentation.setEmail(requestDto.getEmail());
        userRepresentation.setEnabled(requestDto.getEnabled());
        userRepresentation.setEmailVerified(true);

        // Create password credential
        CredentialRepresentation credential = keycloakUserService.createPasswordCredentials(
                requestDto.getPassword());

        userRepresentation.setCredentials(List.of(credential));

        // Set client roles if provided
        if (requestDto.getClientRoles() != null) {
            for (String clientId : requestDto.getClientRoles().keySet()) {
                List<String> roles = requestDto.getClientRoles().get(clientId);
                for (String roleName : roles) {

                    RoleRepresentation roleRepresentation = keycloakClientRoleService
                            .findRoleClientByName(backendClientResource, roleName).get();

                    if (roleRepresentation != null) {
                        keycloakUserService.addClientRoleToUser(
                                realmResource.users().get(userRepresentation.getId()),
                                clientId, roleRepresentation);
                    }
                }
            }
        }
        // Create the user using the same method as in the seeder
        UserRepresentation createdUser = keycloakUserService.createKeycloakUser(realmResource, userRepresentation);

        return createdUser;
    }

    @Override
    public void assignRoleToUser(RealmResource realmResource,
            ClientResource clientResource, RoleRepresentation roleRepresentation,
            UserRepresentation userRepresentation) {
        keycloakUserService.addClientRoleToUser(
                realmResource.users().get(userRepresentation.getId()),
                clientResource.toRepresentation().getId(),
                roleRepresentation);
    }

    private RealmResource getRealmResource() {
        Keycloak keycloak = keycloakConnectService.getKeyCloakAdminCli();
        if (keycloak == null) {
            throw new KeycloakException("Keycloak connection is null");
        }
        RealmResource realmResource = keycloakRealmService.getRealmResource(keycloak,
                realmName);
        return realmResource;
    }

    // @Autowired
    // private DomaineService domaineService;

    // @Autowired
    // private MinioService minioService;

    // @Autowired
    // private ObjectsValidator<UserRequestDto> validator;

    // @Autowired
    // private UserRequestDtoMapper userRequestMapper;

    // private static final String NOT_FOUND_STRING = "User non trouvée";

    // public UserServiceImpl(UserRepository userRepository, SpecificationService
    // specificationService) {
    // super(userRepository, specificationService);
    // }

    // @Override
    // public boolean existsById(Long id) {
    // return userRepository.existsById(id);
    // }

    // @Override
    // public Optional<User> findByNom(String nom) {
    // return userRepository.findByNom(nom);
    // }

    // @Override
    // public Page<User> getEntityList(QueryParams requestParams) {
    // if (requestParams.getPageSize() == -1) {
    // requestParams.setPageSize(Integer.MAX_VALUE);
    // }
    // Pageable pageable = PaginationUtils.createPageable(requestParams);
    // if (!EntityInspector.isFieldPresentInEntity(pageable.getSort().toString(),
    // User.class)) {
    // pageable = PaginationUtils.createPageable(requestParams);
    // }
    // Specification<User> specification = specificationService
    // .createSpecificationWithDynamicGlobalFilter(requestParams.getFilters(),
    // requestParams.getGlobalFilter(), User.class);
    // return findAll(specification, pageable);
    // }

    // @Override
    // public Page<User> getEntitiesByIds(List<Long> ids, QueryParams requestParams)
    // {
    // if (requestParams.getPageSize() == -1) {
    // requestParams.setPageSize(Integer.MAX_VALUE);
    // }
    // Pageable pageable = PaginationUtils.createPageable(requestParams);

    // if (!EntityInspector.isFieldPresentInEntity(pageable.getSort().toString(),
    // User.class)) {
    // pageable = PaginationUtils.createPageable(requestParams);
    // }

    // // If no IDs are provided or empty list, return empty page
    // if (ids == null || ids.isEmpty()) {
    // return Page.empty(pageable);
    // }
    // // Create specification for filtering by IDs
    // Specification<User> idSpecification = (root, _, _) -> root.get("id").in(ids);

    // // Get filter specification and handle null case
    // Specification<User> filterSpecification = specificationService
    // .createSpecificationWithDynamicGlobalFilter(requestParams.getFilters(),
    // requestParams.getGlobalFilter(), User.class);

    // // Combine specifications, handling null case
    // Specification<User> specification = filterSpecification != null
    // ? filterSpecification.and(idSpecification)
    // : idSpecification;

    // return findAll(specification, pageable);
    // }

    // @Override
    // public User save(User user) {
    // return userRepository.save(user);
    // }

    // @Override
    // public User create(UserRequestDto requestDto) throws Exception {

    // validator.validate(requestDto);
    // User userToCreate = userRequestMapper.mapToEntity(requestDto);
    // String imageFileName = minioService.uploadFile(requestDto.getImageFile());
    // userToCreate.setImageUrl(imageFileName); // Store just the filename
    // return userRepository.save(userToCreate);

    // }

    // @Override
    // public User update(Long id, UserRequestDto requestDto) throws Exception {
    // validator.validate(requestDto);
    // checkPathId(id, requestDto.getId());
    // User user = userRepository.findById(id)
    // .orElseThrow(() -> new EntityNotFoundException(NOT_FOUND_STRING));
    // updateUserFields(user, requestDto);
    // handleImageUpdate(user, requestDto);
    // return userRepository.save(user);
    // }

    // private void updateUserFields(User user, UserRequestDto dto) {
    // user.setNom(dto.getNom());
    // user.setDescription(dto.getDescription());
    // user.setApropos(dto.getApropos());
    // user.setActif(dto.getActif());
    // }

    // private void handleImageUpdate(User user, UserRequestDto dto) throws
    // Exception {
    // if (dto.getImageFile() != null && !dto.getImageFile().isEmpty()) {

    // String imageFileName = minioService.uploadFile(dto.getImageFile());
    // user.setImageUrl(imageFileName);

    // }
    // }

    // public void attachDomaine(Long userId, Long domaineId) {
    // User user = userRepository.findById(userId)
    // .orElseThrow(() -> new EntityNotFoundException(NOT_FOUND_STRING));
    // Domaine domaine = domaineService.findById(domaineId)
    // .orElseThrow(() -> new EntityNotFoundException("Domaine non trouvé"));

    // UserDomaine userDomaine = new UserDomaine();
    // userDomaine.setUser(user);
    // userDomaine.setDomaine(domaine);

    // userDomaineRepository.save(userDomaine);
    // }

    // public void detachDomaine(Long userDomaineId) {

    // UserDomaine userDomaine = userDomaineRepository.findById(userDomaineId)
    // .orElseThrow(() -> new EntityNotFoundException("Association non trouvée"));

    // userDomaineRepository.delete(userDomaine);
    // }

}