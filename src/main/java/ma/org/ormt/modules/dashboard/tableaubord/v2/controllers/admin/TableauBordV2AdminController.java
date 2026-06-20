package ma.org.ormt.modules.dashboard.tableaubord.v2.controllers.admin;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import ma.org.ormt.core.commun.rest.responses.RestResponse;
import ma.org.ormt.modules.dashboard.tableaubord.v2.dtos.TableauBordV2CategorieDto;
import ma.org.ormt.modules.dashboard.tableaubord.v2.dtos.TableauBordV2Dto;
import ma.org.ormt.modules.dashboard.tableaubord.v2.dtos.TableauBordV2Mapper;
import ma.org.ormt.modules.dashboard.tableaubord.v2.dtos.request.TableauBordV2CategorieRequestDto;
import ma.org.ormt.modules.dashboard.tableaubord.v2.dtos.request.TableauBordV2RequestDto;
import ma.org.ormt.modules.dashboard.tableaubord.v2.models.TableauBordV2;
import ma.org.ormt.modules.dashboard.tableaubord.v2.services.TableauBordV2Service;

@Validated
@RestController
@RequestMapping("/api/v1/admin/dashboards-v2")
@RequiredArgsConstructor
@Tag(name = "TableauBordV2", description = "Tableau de bord dynamique V2 API")
public class TableauBordV2AdminController {

    private final TableauBordV2Service service;
    private final TableauBordV2Mapper mapper;

    @GetMapping
    @PreAuthorize("hasAuthority('dashboard:list')")
    public ResponseEntity<RestResponse<List<TableauBordV2Dto>>> getDashboards() {
        List<TableauBordV2Dto> summaries = service.findAll().stream()
                .map(mapper::mapToDtoSummary)
                .toList();
        return response(summaries, HttpStatus.OK);
    }

    @GetMapping("/categories")
    @PreAuthorize("hasAuthority('dashboard:list')")
    public ResponseEntity<RestResponse<List<TableauBordV2CategorieDto>>> getCategories() {
        return response(mapper.mapCategoriesToDtos(service.findActiveCategories()), HttpStatus.OK);
    }

    @PostMapping("/categories/sync")
    @PreAuthorize("hasAuthority('dashboard:edit')")
    public ResponseEntity<RestResponse<List<TableauBordV2CategorieDto>>> syncCategories() {
        service.syncCategoriesFromLegacyDomaines();
        return response(mapper.mapCategoriesToDtos(service.findActiveCategories()), HttpStatus.OK);
    }

    @PostMapping("/categories")
    @PreAuthorize("hasAuthority('dashboard:create')")
    public ResponseEntity<RestResponse<TableauBordV2CategorieDto>> createCategorie(
            @Validated @RequestBody TableauBordV2CategorieRequestDto requestDto) {
        return response(mapper.mapCategorieToDto(service.createCategorie(requestDto)), HttpStatus.CREATED);
    }

    @PutMapping("/categories/{id}")
    @PreAuthorize("hasAuthority('dashboard:edit')")
    public ResponseEntity<RestResponse<TableauBordV2CategorieDto>> updateCategorie(
            @PathVariable Long id,
            @Validated @RequestBody TableauBordV2CategorieRequestDto requestDto) {
        return response(mapper.mapCategorieToDto(service.updateCategorie(id, requestDto)), HttpStatus.OK);
    }

    @DeleteMapping("/categories/{id}")
    @PreAuthorize("hasAuthority('dashboard:delete')")
    public ResponseEntity<Void> deleteCategorie(@PathVariable Long id) {
        service.deleteCategorie(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('dashboard:read')")
    public ResponseEntity<RestResponse<TableauBordV2Dto>> getDashboard(@PathVariable Long id) {
        TableauBordV2 dashboard = service.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Tableau de bord V2 non trouvé"));
        return response(mapper.mapToDto(dashboard), HttpStatus.OK);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('dashboard:create')")
    public ResponseEntity<RestResponse<TableauBordV2Dto>> createDashboard(
            @Validated @RequestBody TableauBordV2RequestDto requestDto) {
        return response(mapper.mapToDto(service.create(requestDto)), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('dashboard:edit')")
    public ResponseEntity<RestResponse<TableauBordV2Dto>> updateDashboard(
            @PathVariable Long id,
            @Validated @RequestBody TableauBordV2RequestDto requestDto) {
        return response(mapper.mapToDto(service.update(id, requestDto)), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('dashboard:delete')")
    public ResponseEntity<Void> deleteDashboard(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    private <T> ResponseEntity<RestResponse<T>> response(T data, HttpStatus status) {
        return ResponseEntity.status(status).body(RestResponse.<T>builder()
                .status(status)
                .success(true)
                .data(data)
                .build());
    }
}
