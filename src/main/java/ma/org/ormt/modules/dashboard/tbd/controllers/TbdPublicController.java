package ma.org.ormt.modules.dashboard.tbd.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import ma.org.ormt.modules.dashboard.tbd.dtos.TbdDashboardFullDto;
import ma.org.ormt.modules.dashboard.tbd.services.TbdDashboardService;

@RestController
@RequestMapping("/api/v1/public/tbd")
@RequiredArgsConstructor
@Tag(name = "TBD Public", description = "Endpoints publics pour les tableaux de bord dynamiques")
public class TbdPublicController {

    private final TbdDashboardService service;

    @Operation(summary = "TBD publié assigné à une catégorie")
    @GetMapping("/dashboard/by-categorie/{categorieId}")
    public ResponseEntity<TbdDashboardFullDto> findPublishedByCategorie(@PathVariable Long categorieId) {
        return service.findPublishedByCategorie(categorieId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.noContent().build());
    }

    @Operation(summary = "TBD publié assigné à un sous-domaine")
    @GetMapping("/dashboard/by-sous-domaine/{sousDomaineId}")
    public ResponseEntity<TbdDashboardFullDto> findPublishedBySousDomaine(@PathVariable Long sousDomaineId) {
        return service.findPublishedBySousDomaine(sousDomaineId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.noContent().build());
    }
}
