package ma.org.ormt.security.roleacces.services;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import ma.org.ormt.security.roleacces.models.RoleAcces;
import ma.org.ormt.security.roleacces.repositories.RoleAccesRepository;

@Service
@Transactional
public class RoleAccesService {

    @Autowired
    private RoleAccesRepository roleAccesRepository;

    /**
     * Gets the current user's role from security context
     */
    public String getCurrentUserRole() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return "role_public"; // Default role if no authentication
        }

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        for (GrantedAuthority authority : authorities) {
            String authorityName = authority.getAuthority();
            // Assuming roles are prefixed with "ROLE_" in your system
            if (authorityName.startsWith("ROLE_")) {
                return authorityName.replace("ROLE_", "role_").toLowerCase();
            }
        }

        // Fallback to public role if no appropriate role found
        return "role_public";
    }

    /**
     * Vérifie si un rôle a accès à une ressource
     */
    public boolean hasAccess(String roleCode, String typeRessource, Long ressourceId, String niveauAcces) {
        return roleAccesRepository.existsByRoleCodeAndTypeRessourceAndRessourceIdAndNiveauAcces(
                roleCode, typeRessource, ressourceId, niveauAcces);
    }

    /**
     * Retourne toutes les ressources accessibles pour un rôle
     */
    public List<Long> getAccessibleResources(String roleCode, String typeRessource, String niveauAcces) {
        return roleAccesRepository.findRessourceIdsByRoleCodeAndTypeRessourceAndNiveauAcces(
                roleCode, typeRessource, niveauAcces);
    }

    /**
     * Ajoute un accès pour un rôle sur une ressource
     */
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
    public void removeAccess(String roleCode, String typeRessource, Long ressourceId) {
        roleAccesRepository.deleteByRoleCodeAndTypeRessourceAndRessourceId(roleCode, typeRessource, ressourceId);
    }

    /**
     * Gère les accès en cascade (ex: donner accès à un domaine et tous ses
     * sous-domaines)
     */
    public void setHierarchicalAccess(String roleCode, String typeRessource, Long ressourceId,
            String niveauAcces, boolean applyToChildren, String username) {
        // Ajout de l'accès au niveau spécifié
        addAccess(roleCode, typeRessource, ressourceId, niveauAcces, username);

        // Si on doit appliquer aux enfants
        if (applyToChildren) {
            // if ("espace".equals(typeRessource)) {
            // // Trouver tous les domaines de cet espace et leur donner accès
            // List<Long> domaineIds = domaineRepository.findIdsByEspaceId(ressourceId);
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
