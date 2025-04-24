package ma.org.ormt.security.roleacces.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.Query;

import ma.org.ormt.core.commun.base.repository.BaseRepository;
import ma.org.ormt.security.roleacces.models.RoleAcces;

public interface RoleAccesRepository extends BaseRepository<RoleAcces> {

        // Vérifier si un rôle a accès à une ressource spécifique
        boolean existsByRoleCodeAndTypeRessourceAndRessourceIdAndNiveauAcces(
                        String roleCode, String typeRessource, Long ressourceId, String niveauAcces);

        // Trouver toutes les ressources d'un type donné accessibles à un rôle
        @Query("SELECT r.ressourceId FROM RoleAcces r WHERE r.roleCode = :roleCode " +
                        "AND r.typeRessource = :typeRessource AND r.niveauAcces = :niveauAcces")
        List<Long> findRessourceIdsByRoleCodeAndTypeRessourceAndNiveauAcces(
                        String roleCode, String typeRessource, String niveauAcces);

        // Supprimer les accès pour un rôle et une ressource
        void deleteByRoleCodeAndTypeRessourceAndRessourceId(
                        String roleCode, String typeRessource, Long ressourceId);

        // Trouver tous les accès pour un rôle
        List<RoleAcces> findByRoleCode(String roleCode);

        // Trouver tous les accès pour une ressource spécifique
        List<RoleAcces> findByTypeRessourceAndRessourceId(String typeRessource, Long ressourceId);

}