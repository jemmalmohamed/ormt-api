package ma.org.ormt.modules.dashboard.tbd.categories.controllers.admin;

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
import lombok.RequiredArgsConstructor;
import ma.org.ormt.core.commun.rest.responses.RestResponse;
import ma.org.ormt.modules.dashboard.tbd.categories.dtos.TbdCategoryDto;
import ma.org.ormt.modules.dashboard.tbd.categories.dtos.TbdCategoryMapper;
import ma.org.ormt.modules.dashboard.tbd.categories.dtos.request.TbdCategoryRequestDto;
import ma.org.ormt.modules.dashboard.tbd.categories.services.TbdCategoryService;

@Validated
@RestController
@RequestMapping("/api/v1/admin/dashboard-dynamic/categories")
@RequiredArgsConstructor
@Tag(name = "TbdCategory", description = "Catégories des dashboards dynamiques")
public class TbdCategoryAdminController {

    private final TbdCategoryService service;
    private final TbdCategoryMapper mapper;

    @GetMapping
    @PreAuthorize("hasAuthority('dashboard:list')")
    public ResponseEntity<RestResponse<List<TbdCategoryDto>>> getCategories() {
        return response(mapper.mapCategoriesToDtos(service.findActiveCategories()), HttpStatus.OK);
    }

    @PostMapping("/sync")
    @PreAuthorize("hasAuthority('dashboard:edit')")
    public ResponseEntity<RestResponse<List<TbdCategoryDto>>> syncCategories() {
        service.syncCategoriesFromDomaines();
        return response(mapper.mapCategoriesToDtos(service.findActiveCategories()), HttpStatus.OK);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('dashboard:create')")
    public ResponseEntity<RestResponse<TbdCategoryDto>> createCategory(
            @Validated @RequestBody TbdCategoryRequestDto requestDto) {
        return response(mapper.mapCategoryToDto(service.createCategory(requestDto)), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('dashboard:edit')")
    public ResponseEntity<RestResponse<TbdCategoryDto>> updateCategory(
            @PathVariable Long id,
            @Validated @RequestBody TbdCategoryRequestDto requestDto) {
        return response(mapper.mapCategoryToDto(service.updateCategory(id, requestDto)), HttpStatus.OK);
    }

    @PutMapping("/reorder")
    @PreAuthorize("hasAuthority('dashboard:edit')")
    public ResponseEntity<Void> reorderCategories(
            @RequestBody java.util.List<ma.org.ormt.modules.dashboard.tbd.categories.services.TbdCategoryService.ReorderItem> items) {
        service.reorderCategories(items);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('dashboard:delete')")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        service.deleteCategory(id);
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
