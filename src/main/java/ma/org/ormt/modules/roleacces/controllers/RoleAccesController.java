package ma.org.ormt.modules.roleacces.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ma.org.ormt.modules.roleacces.models.RoleAcces;
import ma.org.ormt.modules.roleacces.services.RoleAccesService;

import org.springframework.security.core.Authentication;

@RestController
@RequestMapping("/api/acces")
public class RoleAccesController {

    @Autowired
    private RoleAccesService roleAccesService;

    @GetMapping("role/{roleCode}/type/{typeRessource}")
    public List<Long> getAccessibleResources(
            @PathVariable String roleCode,
            @PathVariable String typeRessource,
            @RequestParam(defaultValue = "lecture") String niveauAcces) {

        return roleAccesService.getAccessibleResourceIds(roleCode, typeRessource,
                niveauAcces);
    }

    @GetMapping("role/{roleCode}")
    public List<Long> getAccessibleResourcesByRole(
            @PathVariable String roleCode,

            @RequestParam(defaultValue = "lecture") String niveauAcces) {

        return roleAccesService.getAccessibleResourceIds(roleCode, "espace",
                niveauAcces);
    }

    @PostMapping("/role/{roleCode}/type/{typeRessource}/{ressourceId}")
    public ResponseEntity<?> addAccess(
            @PathVariable String roleCode,
            @PathVariable String typeRessource,
            @PathVariable Long ressourceId,
            @RequestParam(defaultValue = "lecture") String niveauAcces,
            @RequestParam(defaultValue = "false") boolean applyToChildren,
            Authentication authentication) {

        String username = authentication.getName();

        if (applyToChildren) {
            roleAccesService.setHierarchicalAccess(roleCode, typeRessource, ressourceId,
                    niveauAcces, true, username);
            return ResponseEntity.ok("Accès hiérarchique ajouté avec succès");
        } else {
            RoleAcces acces = roleAccesService.addAccess(roleCode, typeRessource,
                    ressourceId, niveauAcces, username);
            return ResponseEntity.ok(acces);
        }
    }

    @DeleteMapping("/role/{roleCode}/type/{typeRessource}/{ressourceId}")
    public ResponseEntity<?> removeAccess(
            @PathVariable String roleCode,
            @PathVariable String typeRessource,
            @PathVariable Long ressourceId) {

        roleAccesService.removeAccess(roleCode, typeRessource, ressourceId);
        return ResponseEntity.ok("Accès supprimé avec succès");
    }

    @GetMapping("/check")
    public ResponseEntity<Boolean> checkAccess(
            @RequestParam String roleCode,
            @RequestParam String typeRessource,
            @RequestParam Long ressourceId,
            @RequestParam(defaultValue = "lecture") String niveauAcces) {

        boolean hasAccess = roleAccesService.hasAccess(roleCode, typeRessource,
                ressourceId, niveauAcces);
        return ResponseEntity.ok(hasAccess);
    }
}
