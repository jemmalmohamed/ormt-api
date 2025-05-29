package ma.org.ormt.modules.roleacces.services;

import java.util.List;

import org.springframework.data.domain.Page;

import ma.org.ormt.core.commun.base.service.BaseService;
import ma.org.ormt.core.commun.rest.queries.QueryParams;
import ma.org.ormt.modules.roleacces.dtos.request.RoleAccesRequestDto;
import ma.org.ormt.modules.roleacces.models.RoleAcces;

public interface RoleAccesService extends BaseService<RoleAcces> {

        Page<RoleAcces> getEntityList(QueryParams requestParams);

        public Page<RoleAcces> getEntitiesByIds(List<Long> ids, QueryParams params);

        RoleAcces create(RoleAccesRequestDto requestDto);

        RoleAcces update(Long id, RoleAccesRequestDto roleAccRoleAccesRequestDto);

        RoleAcces save(RoleAcces roleAccRoleAcces);

        boolean existsById(Long id);

        public boolean hasAccessToResource(Long resourceId, String resourceName, String permission);

        /**
         * Get accessible resource IDs for current user
         */
        public List<Long> getAccessibleResourceIdsForCurrentUser(String resourceType, String permission);

        /**
         * Vérifie si un rôle a accès à une ressource
         */
        public boolean hasAccess(String roleCode, String typeRessource, Long ressourceId, String niveauAcces);

        /**
         * Retourne toutes les ressources accessibles pour un rôle
         */
        public List<Long> getAccessibleResourceIds(String roleCode, String typeRessource, String niveauAcces);

        /**
         * Retourne tous les acces d une source
         */
        public List<RoleAcces> getAccesByRessource(String typeRessource, Long ressourceId);

        /**
         * Ajoute un accès pour un rôle sur une ressource
         */
        public RoleAcces addAccess(String roleCode, String typeRessource, Long ressourceId, String niveauAcces,
                        String username);

        /**
         * Supprime un accès
         */
        public void removeAccess(String roleCode, String typeRessource, Long ressourceId);

        /**
         * Gère les accès en cascade (ex: donner accès à un domaine et tous ses
         * sous-domaines)
         */
        public void setHierarchicalAccess(String roleCode, String typeRessource, Long ressourceId,
                        String niveauAcces, boolean applyToChildren, String username);
}
