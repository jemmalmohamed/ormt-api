package ma.org.ormt.modules.roleacces.services.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import ma.org.ormt.core.commun.base.service.BaseServiceImpl;
import ma.org.ormt.core.commun.base.service.SpecificationService;
import ma.org.ormt.core.commun.rest.queries.QueryParams;
import ma.org.ormt.core.utilities.EntityInspector;
import ma.org.ormt.core.utilities.PaginationUtils;
import ma.org.ormt.core.validators.ObjectsValidator;
import ma.org.ormt.modules.roleacces.dtos.request.RoleAccesRequestDto;
import ma.org.ormt.modules.roleacces.models.RoleAcces;
import ma.org.ormt.modules.roleacces.repositories.RoleAccesRepository;
import ma.org.ormt.modules.roleacces.services.RoleAccesService;
import ma.org.ormt.security.authentication.services.AuthService;

@Service
@Transactional
public class RoleAccesServiceImpl extends BaseServiceImpl<RoleAcces> implements RoleAccesService {

    @Autowired
    private RoleAccesRepository roleAccesRepository;

    @Autowired
    private AuthService authService;

    @Autowired
    private ObjectsValidator<RoleAccesRequestDto> validator;

    private static final String NOT_FOUND_STRING = "Role accés non trouvée";

    public RoleAccesServiceImpl(RoleAccesRepository roleAccesRepository, SpecificationService specificationService) {
        super(roleAccesRepository, specificationService);
    }

    @Override
    public boolean existsById(Long id) {
        return roleAccesRepository.existsById(id);
    }

    @Override
    public Page<RoleAcces> getEntityList(QueryParams requestParams) {
        if (requestParams.getPageSize() == -1) {
            requestParams.setPageSize(Integer.MAX_VALUE);
        }
        Pageable pageable = PaginationUtils.createPageable(requestParams);
        if (!EntityInspector.isFieldPresentInEntity(pageable.getSort().toString(), RoleAcces.class)) {
            pageable = PaginationUtils.createPageable(requestParams);
        }
        Specification<RoleAcces> specification = specificationService
                .createSpecificationWithDynamicGlobalFilter(requestParams.getFilters(),
                        requestParams.getGlobalFilter(), RoleAcces.class);
        return findAll(specification, pageable);
    }

    @Override
    public Page<RoleAcces> getEntitiesByIds(List<Long> ids, QueryParams requestParams) {
        if (requestParams.getPageSize() == -1) {
            requestParams.setPageSize(Integer.MAX_VALUE);
        }
        Pageable pageable = PaginationUtils.createPageable(requestParams);

        if (!EntityInspector.isFieldPresentInEntity(pageable.getSort().toString(), RoleAcces.class)) {
            pageable = PaginationUtils.createPageable(requestParams);
        }

        // If no IDs are provided or empty list, return empty page
        if (ids == null || ids.isEmpty()) {
            return Page.empty(pageable);
        }
        // Create specification for filtering by IDs
        Specification<RoleAcces> idSpecification = (root, _, _) -> root.get("id").in(ids);

        // Get filter specification and handle null case
        Specification<RoleAcces> filterSpecification = specificationService
                .createSpecificationWithDynamicGlobalFilter(requestParams.getFilters(),
                        requestParams.getGlobalFilter(), RoleAcces.class);

        // Combine specifications, handling null case
        Specification<RoleAcces> specification = filterSpecification != null
                ? filterSpecification.and(idSpecification)
                : idSpecification;

        return findAll(specification, pageable);
    }

    @Override
    public RoleAcces save(RoleAcces roleAcces) {
        return roleAccesRepository.save(roleAcces);
    }

    @Override
    public RoleAcces create(RoleAccesRequestDto requestDto) {

        validator.validate(requestDto);
        RoleAcces newRoleAcces = new RoleAcces();
        RoleAcces roleAccesToCreate = updateRoleAccesFields(newRoleAcces, requestDto);

        return roleAccesRepository.save(roleAccesToCreate);

    }

    @Override
    public RoleAcces update(Long id, RoleAccesRequestDto requestDto) {
        validator.validate(requestDto);
        checkPathId(id, requestDto.getId());
        RoleAcces roleAcces = roleAccesRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(NOT_FOUND_STRING));
        updateRoleAccesFields(roleAcces, requestDto);
        return roleAccesRepository.save(roleAcces);
    }

    private RoleAcces updateRoleAccesFields(RoleAcces roleAcces, RoleAccesRequestDto dto) {
        if (roleAcces == null) {
            roleAcces = new RoleAcces();
        }
        roleAcces.setNiveauAcces(dto.getNiveauAcces());
        roleAcces.setRoleCode("role_" + dto.getRoleCode().toLowerCase());
        roleAcces.setDescription(dto.getDescription());
        roleAcces.setTypeRessource(dto.getTypeRessource());
        roleAcces.setRessourceId(Long.valueOf(dto.getRessourceId()));
        return roleAcces;
    }

    /**
     * Check if current user has access to a specific resource
     * Admin role always has access to all resources
     */

    @Override
    public boolean hasAccessToResource(Long resourceId, String resourceName, String permission) {

        if (authService.isAdmin() || authService.isMaster()) {
            return true;
        }

        String currentUserRole = authService.getCurrentUserRole();

        List<Long> accessibleIds = getAccessibleResourceIds(
                currentUserRole, resourceName, permission);

        return accessibleIds != null && accessibleIds.contains(resourceId);
    }

    /**
     * Get accessible resource IDs for current user
     */

    @Override
    public List<Long> getAccessibleResourceIdsForCurrentUser(String resourceType, String permission) {
        // Admin and Master roles have access to everything - return null to indicate
        // "all"
        if (authService.isAdmin() || authService.isMaster()) {
            return null; // null means access to all resources
        }

        String currentUserRole = authService.getCurrentUserRole();
        return getAccessibleResourceIds(currentUserRole, resourceType, permission);
    }

    /**
     * Vérifie si un rôle a accès à une ressource
     */

    @Override
    public boolean hasAccess(String roleCode, String typeRessource, Long ressourceId, String niveauAcces) {
        return roleAccesRepository.existsByRoleCodeAndTypeRessourceAndRessourceIdAndNiveauAcces(
                roleCode, typeRessource, ressourceId, niveauAcces);
    }

    /**
     * Retourne toutes les ressources accessibles pour un rôle
     */

    @Override
    public List<Long> getAccessibleResourceIds(String roleCode, String typeRessource, String niveauAcces) {
        return roleAccesRepository.findRessourceIdsByRoleCodeAndTypeRessourceAndNiveauAcces(
                roleCode.toLowerCase(), typeRessource, niveauAcces);
    }

    /**
     * Retourne tous les acces d une source
     */

    @Override
    public List<RoleAcces> getAccesByRessource(String typeRessource, Long ressourceId) {
        return roleAccesRepository.findByTypeRessourceAndRessourceId(typeRessource, ressourceId);
    }

    /**
     * Ajoute un accès pour un rôle sur une ressource
     */

    @Override
    public RoleAcces addAccess(String roleCode, String typeRessource, Long ressourceId, String niveauAcces,
            String username) {
        RoleAcces acces = new RoleAcces();
        acces.setRoleCode(roleCode);
        acces.setTypeRessource(typeRessource);
        acces.setRessourceId(ressourceId);
        acces.setNiveauAcces(niveauAcces);

        return roleAccesRepository.save(acces);
    }

    /**
     * Supprime un accès
     */

    @Override
    public void removeAccess(String roleCode, String typeRessource, Long ressourceId) {
        roleAccesRepository.deleteByRoleCodeAndTypeRessourceAndRessourceId(roleCode, typeRessource, ressourceId);
    }

    /**
     * Gère les accès en cascade (ex: donner accès à un domaine et tous ses
     * sous-domaines)
     */

    @Override
    public void setHierarchicalAccess(String roleCode, String typeRessource, Long ressourceId,
            String niveauAcces, boolean applyToChildren, String username) {
        // Ajout de l'accès au niveau spécifié
        addAccess(roleCode, typeRessource, ressourceId, niveauAcces, username);

        // Si on doit appliquer aux enfants
        if (applyToChildren) {
            // if ("roleAcces".equals(typeRessource)) {
            // // Trouver tous les domaines de cet roleAcces et leur donner accès
            // List<Long> domaineIds = domaineRepository.findIdsByRoleAccesId(ressourceId);
            // for (Long domaineId : domaineIds) {
            // setHierarchicalAccess(roleCode, "domaine", domaineId, niveauAcces, true,
            // username);
            // }
            // } else if ("domaine".equals(typeRessource)) {
            // // Trouver tous les sous-domaines de ce domaine et leur donner accès
            // List<Long> sousDomaineIds =
            // sousDomaineRepository.findIdsByDomaineId(ressourceId);
            // for (Long sousDomaineId : sousDomaineIds) {
            // setHierarchicalAccess(roleCode, "sous_domaine", sousDomaineId, niveauAcces,
            // true, username);
            // }
            // } else if ("sous_domaine".equals(typeRessource)) {
            // // Trouver tous les indicateurs de ce sous-domaine et leur donner accès
            // List<Long> indicateurIds =
            // indicateurRepository.findIdsBySousDomaineId(ressourceId);
            // for (Long indicateurId : indicateurIds) {
            // addAccess(roleCode, "indicateur", indicateurId, niveauAcces, username);
            // }
            // }
        }
    }
}
