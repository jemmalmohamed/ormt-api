package ma.org.ormt.modules.dashboard.tbd.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import ma.org.ormt.core.commun.rest.responses.RestResponse;
import ma.org.ormt.modules.dashboard.tbd.categories.dtos.TbdCategoryDto;
import ma.org.ormt.modules.dashboard.tbd.categories.dtos.TbdCategoryMapper;
import ma.org.ormt.modules.dashboard.tbd.categories.services.TbdCategoryService;
import ma.org.ormt.modules.dashboard.tbd.dtos.TbdDashboardFullDto;
import ma.org.ormt.modules.dashboard.tbd.services.TbdDashboardService;

@RestController
@RequestMapping("/api/v1/public/tbd")
@RequiredArgsConstructor
@Tag(name = "TBD Public", description = "Endpoints publics pour les tableaux de bord dynamiques")
public class TbdPublicController {

    private final TbdDashboardService service;
    private final TbdCategoryService categoryService;
    private final TbdCategoryMapper categoryMapper;

    @Operation(summary = "Catégories ayant un TBD actif et publié")
    @GetMapping("/categories")
    public ResponseEntity<RestResponse<List<TbdCategoryDto>>> findCategoriesWithPublishedTbd() {
        List<TbdCategoryDto> dtos = categoryMapper.mapCategoriesToDtos(categoryService.findCategoriesWithPublishedTbd());
        return ResponseEntity.ok(RestResponse.<List<TbdCategoryDto>>builder()
                .success(true)
                .status(org.springframework.http.HttpStatus.OK)
                .data(dtos)
                .build());
    }

    @Operation(summary = "TBD publié par son identifiant")
    @GetMapping("/dashboard/{id}")
    public ResponseEntity<TbdDashboardFullDto> findPublishedById(@PathVariable Long id) {
        return service.findPublishedById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.noContent().build());
    }

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
